package com.ackerman.reddit.service;

import com.ackerman.reddit.dao.NewsDAO;
import com.ackerman.reddit.model.News;
import com.ackerman.reddit.model.ScoreModel;
import com.ackerman.reddit.utils.Entity;
import com.ackerman.reddit.utils.JedisUtil;
import com.ackerman.reddit.utils.RedditUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class NewsService {
    private static Logger logger = LoggerFactory.getLogger(NewsService.class);

    @Autowired
    private NewsDAO newsDAO;

    @Autowired
    private JedisUtil jedisUtil;


    public int addNews(News news){
        return newsDAO.addNews(news);
    }

    public int countNews(){
        return newsDAO.countNews();
    }

    public News getOldestNews(){
        return newsDAO.selectOldestNews();
    }

    public int countNewsBeforeId(int id){
        return newsDAO.countNewsBeforeId(id);
    }

    public List<News> getLastNews(int userId, int offset, int limit){
        return newsDAO.selectNewsByUserIdAndOffset(userId, offset, limit);
    }

    public List<News> getBeforeIdAndLimit(int newsId, int offset, int limit){
        return newsDAO.selectBeforeIdAndLimit(newsId, offset, limit);
    }

    public List<News> getAfterIdAndLimit(int newsId, int offset, int limit){
        return  newsDAO.selectAfterIdAndLimit(newsId, offset, limit);
    }

    public List<News> getNewsAfterDate(Date date){
        return newsDAO.selectNewsAfterDate(date);
    }

    public List<News> getNewsBeforeId(int id){
        return newsDAO.selectBeforeId(id);
    }

    public List<News> getNewsByUserId(int userId){
        return newsDAO.selectNewsByUserId(userId);
    }

    public News getNewsById(int id){
        return newsDAO.selectNewsById(id);
    }


    public void updateLikeCount(int likeCount, int newsId){
        newsDAO.updateLikeCountById(likeCount, newsId);
    }

    public void incrCommentCount(int newsId){
        newsDAO.incrCommentCountById(newsId);
    }

    public int getCommentCount(int newsId){
        return newsDAO.getCommentCount(newsId);
    }

    public int getAttitude(int userId, int entityType, int entityId){
        String likeKey = Entity.getAttitudeKey(Entity.ATTITUDE_LIKE, entityType, entityId);
        String dislikeKey = Entity.getAttitudeKey(Entity.ATTITUDE_DISLIKE, entityType, entityId);
        boolean flag = jedisUtil.sismember(likeKey, String.valueOf(userId));
        if(flag){
            return 1;
        }
        flag = jedisUtil.sismember(dislikeKey, String.valueOf(userId));
        return flag ? -1 : 0;
    }

    public long resetAttitude(int userId, int entityType, int entityId){
        String likeKey = Entity.getAttitudeKey(Entity.ATTITUDE_LIKE, entityType, entityId);
        String dislikeKey = Entity.getAttitudeKey(Entity.ATTITUDE_DISLIKE, entityType, entityId);

        jedisUtil.srem(likeKey, String.valueOf(userId));
        jedisUtil.srem(dislikeKey, String.valueOf(userId));

        return jedisUtil.scard(likeKey);
    }

    public long support(int userId, int entityType, int entityId){
        String likeKey = Entity.getAttitudeKey(Entity.ATTITUDE_LIKE, entityType, entityId);
        String dislikeKey = Entity.getAttitudeKey(Entity.ATTITUDE_DISLIKE, entityType, entityId);

        jedisUtil.sadd(likeKey, String.valueOf(userId));
        jedisUtil.srem(dislikeKey, String.valueOf(userId));

        return jedisUtil.scard(likeKey) - jedisUtil.scard(dislikeKey);
    }

    public long oppose(int userId, int entityType, int entityId){
        String dislikeKey = Entity.getAttitudeKey(Entity.ATTITUDE_DISLIKE, entityType, entityId);
        String likeKey = Entity.getAttitudeKey(Entity.ATTITUDE_LIKE, entityType, entityId);

        jedisUtil.sadd(dislikeKey, String.valueOf(userId));
        jedisUtil.srem(likeKey, String.valueOf(userId));

        return jedisUtil.scard(likeKey) - jedisUtil.scard(dislikeKey);
    }

    public void updateScore(int newsId, int likeCount){
        String key = Entity.getScoreHashKey();
        String filed = String.valueOf(newsId);
        String value = jedisUtil.hget(key, filed);

        ScoreModel scoreModel = RedditUtil.getObject(value, ScoreModel.class);

        //此判断条件 主要是拦截一个星期以前的新闻
        //在初始化后他们一定不在redis中
        if(scoreModel == null){
            return;
        }
        scoreModel.setLikeCount(likeCount);

        //返回true,表示新闻未过期, 更新数据库
        if(RedditUtil.calculateScore(scoreModel)){
            value = JSONObject.toJSONString(scoreModel);
            jedisUtil.hset(key, filed, value);
        }
        else{
            jedisUtil.hdel(key, filed);
        }
    }

    //多种排序算法, 依靠传入的比较器实现
    public PriorityQueue<News> getPopularNews(Object... objects){
        String key = Entity.getScoreHashKey();
        Map<String, String> maps = jedisUtil.hgetAll(key);
        Map<String, Integer> idMap = RedditUtil.findMinMax(maps.keySet());
        List<News> newsList = newsDAO.selectByIdFields(idMap.get("min"), idMap.get("max"));

        PriorityQueue<News> queue = new PriorityQueue<>(new Comparator<News>() {
            @Override
            public int compare(News o1, News o2) {
                if(o1.getScore() == o2.getScore()){
                    return  0;
                }
                return o1.getScore() < o2.getScore() ? 1 : -1;
            }
        });

        for(News news: newsList){
            String value = maps.get(String.valueOf(news.getId()));
            ScoreModel scoreModel = JSONObject.parseObject(value, ScoreModel.class);
            //刷新页面时,顺便更新redis中的每个field的分值
            //这样就不用设置定时器了, 分值是每次刷新都得到更新了
            updateScore(news.getId(), news.getLikeCount());

            news.setScore(scoreModel.getScore());
            queue.add(news);
        }

        return queue;
    }


    public List<Integer> getMyFans(int userId){
        List<Integer> ret = new ArrayList<>();
        String myFansKey = Entity.getMyFansKey(userId);
        Set<String> fansSet = jedisUtil.smembers(myFansKey);
        for(String fansId : fansSet){
            ret.add(Integer.valueOf(fansId));
        }
        return ret;
    }

}
