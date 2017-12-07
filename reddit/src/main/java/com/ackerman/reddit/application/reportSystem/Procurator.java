package com.ackerman.reddit.application.reportSystem;

import com.ackerman.reddit.application.Observer;
import com.ackerman.reddit.application.Subject;
import com.ackerman.reddit.service.NewsService;
import com.ackerman.reddit.utils.Entity;
import com.ackerman.reddit.utils.JedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;


@Component
public class Procurator implements Subject {
    private static Logger logger = LoggerFactory.getLogger(Procurator.class);

    @Autowired
    private Reporter reporter;

    @Autowired
    private JedisUtil jedisUtil;

    @Autowired
    private NewsService newsService;

    @Override
    public void registerObserver(Observer observer) {
        try{
            Reporter reporter = (Reporter)observer;

            //举报的每一条新闻, 要有对应的举报者集合, 因为需要反馈信息
            String newsKey = Entity.getReportNewsKey(reporter.getNewsId());
            jedisUtil.sadd(newsKey, String.valueOf(reporter.getUserId()));

            int cnt = (int)jedisUtil.scard(newsKey);
            //举报的新闻集, zset排序, 分数就是举报者的人数
            String newsZset = Entity.REPORT_NEWS;
            jedisUtil.zadd(newsZset, cnt, String.valueOf(reporter.getNewsId()));
        }catch (Exception e){
            logger.info("registerObserver异常: " + e.getMessage());
            e.getStackTrace();
        }
    }

    @Override
    public void removeObserver(Observer observer) {
        try{
            Reporter reporter = (Reporter)observer;

            //举报的每一条新闻, 要有对应的举报者集合, 因为需要反馈信息
            String newsKey = Entity.getReportNewsKey(reporter.getNewsId());
            jedisUtil.srem(newsKey, String.valueOf(reporter.getUserId()));

            int cnt = (int)jedisUtil.scard(newsKey);
            //举报的新闻集, zset排序, 分数就是举报者的人数
            String newsZset = Entity.REPORT_NEWS;
            jedisUtil.zadd(newsZset, cnt, String.valueOf(reporter.getNewsId()));
        }catch (Exception e){
            logger.info("removeObserver异常: " + e.getMessage());
            e.getStackTrace();
        }
    }

    @Override
    public void notifyObservers(Object object) {
        //当处理完被举报的新报, 通知并发布消息给举报者
        //传递进来的参数object: newsId, response
        try {
            Map<String, Object> info = (Map<String, Object>) object;
            int newsId = (Integer) info.get("newsId");

            String newsKey = Entity.getReportNewsKey(newsId);
            Set<String> set = jedisUtil.smembers(newsKey);
            for (String id : set) {
                //通知每一位举报者
                info.put("communicator", Integer.valueOf(id));
                reporter.update(info);
            }

            //处理完这条被举报的新闻, 进行redis缓存清理工作
            jedisUtil.del(newsKey);

            String newsSet = Entity.REPORT_NEWS;
            jedisUtil.zrem(newsSet, String.valueOf(newsId));


        }catch (Exception e){
            e.getStackTrace();
        }
    }
}
