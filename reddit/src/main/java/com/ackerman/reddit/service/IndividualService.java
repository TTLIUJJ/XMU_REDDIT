package com.ackerman.reddit.service;

import com.ackerman.reddit.model.News;
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
public class IndividualService {
    private static Logger logger = LoggerFactory.getLogger(IndividualService.class);

    @Autowired
    private JedisUtil jedisUtil;

    @Autowired
    private NewsService newsService;

    public String addFans(int idolId, int fansId){
        String message = null;
        try{
            //fans_set 在redis中是一个hash key
            //首先判断idol是否有粉丝, 如果有就在redis新增粉丝集合,
            // hmset key <filed, value>
            String key = Entity.getFansHashKey();
            String field = Entity.getMyIdolKey(idolId);
            if(jedisUtil.hexists(key, field)){
                //接下判断 该fansId是否已经存在于该idol的粉丝集合
                //如果是, 那就是取消关注
                String value = jedisUtil.hget(key, field);
                Set<String> fansSet = JSONObject.parseObject(value, new TypeReference<Set<String>>(){});

                if(fansSet.contains(String.valueOf(fansId))){
                    fansSet.remove(String.valueOf(fansId));
                    message = "取消关注成功";
                    if(fansSet.isEmpty()){
                        jedisUtil.hdel(key, field);
                    }
                }
                else{
                    fansSet.add(String.valueOf(fansId));
                    message = "关注成功";
                }
                //更新粉丝集合
                value = JSONObject.toJSONString(fansSet);
                jedisUtil.hset(key, field, value);
            }
            else{
                //在此之前 没有粉丝 故在FANS_SET中没有对应的field
                Set<String> fansSet = new HashSet<>();
                fansSet.add(String.valueOf(fansId));
                String value = JSONObject.toJSONString(fansSet);

                jedisUtil.hset(key, field, value);
                message = "关注成功";
            }
        }catch (Exception e){
            logger.error("addFans异常: " + e.getMessage());
            message = "系统异常";
        }
        return message;
    }


    //用户点击添加关注, 触发redis的两个Key
    //一个被关注者idol的粉丝集合要增加
    //另一个是用户的关注者集合要增加
    public String addFansAndIdol(int idolId, int myId){
        String myIdolKey = Entity.getMyIdolKey(myId);   //我的关注者的集合
        String myFansKey = Entity.getMyFansKey(idolId); //明星的粉丝的集合

        try{
            //idol的粉丝中已经有你了, 那么你就是不想关注他了
            //操作应该是原子的
            if(jedisUtil.sismember(myIdolKey, String.valueOf(idolId))){
                jedisUtil.srem(myIdolKey, String.valueOf(idolId));
                jedisUtil.srem(myFansKey, String.valueOf(myId));
                return RedditUtil.getJSONString(2, "取消关注成功");
            }else{
                jedisUtil.sadd(myIdolKey, String.valueOf(idolId));
                jedisUtil.sadd(myFansKey, String.valueOf(myId));
                return RedditUtil.getJSONString(1, "关注成功");
            }
        }catch (Exception e){
            logger.info("关注操作异常: "+e.getMessage());
            return RedditUtil.getJSONString(-1, "系统异常");
        }

    }


    public int collectNews(int newsId, int userId){

        String key = Entity.getMyCollectKey(userId);

        try{
            if(jedisUtil.sismember(key, String.valueOf(newsId))){
                jedisUtil.srem(key, String.valueOf(newsId));
                return  -1;
            }
            else{
                jedisUtil.sadd(key, String.valueOf(newsId));
                return 1;
            }
        }catch (Exception e){
            logger.info("收藏新闻失败: " + e.getMessage());
            return  0;
        }
    }

    public List<News> getCollectNews(int userId){
        String key = Entity.getMyCollectKey(userId);
        try{
            Set<String> newsSet = jedisUtil.smembers(key);
            List<News> newsList = new ArrayList<>();

            for(String n: newsSet){
                News news = newsService.getNewsById(Integer.valueOf(n));
                newsList.add(news);
            }
            return newsList;
        }catch (Exception e){
            logger.info("获取收藏文章异常: " + e.getMessage());
            return null;
        }
    }
}
