package com.ackerman.reddit.service;

import com.ackerman.reddit.model.News;
import com.ackerman.reddit.utils.Entity;
import com.ackerman.reddit.utils.JedisUtil;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AdminService {
    private static Logger logger = LoggerFactory.getLogger(AdminService.class);

    @Autowired
    private JedisUtil jedisUtil;

    @Autowired
    private NewsService newsService;

    public List<News> getReportedNews(double max, double min){
        //按举报人数逆序排序
        List<News> newsList = new ArrayList<>();
        try{
            String key = Entity.REPORT_NEWS;
            Set<String> newsSet = jedisUtil.zrevrangeByScore(key, max, min);

            for(String newsId: newsSet){
                //zscore卡住了, 不知道什么原因
                News news = newsService.getNewsById(Integer.valueOf(newsId));
                news.setScore(jedisUtil.scard(Entity.getReportNewsKey(Integer.valueOf(newsId))));
                newsList.add(news);
            }
        }catch (Exception e){
            e.getStackTrace();
        }

        return newsList;
    }

}
