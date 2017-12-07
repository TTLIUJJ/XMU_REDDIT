package com.ackerman.reddit.async;

import com.ackerman.reddit.utils.Entity;
import com.ackerman.reddit.utils.JedisUtil;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EventProducer {
    private static Logger logger = LoggerFactory.getLogger(EventProducer.class);
    private static String key = Entity.getEventQueueKey();

    @Autowired
    private JedisUtil jedisUtil;

    public boolean produceEvent(EventModel eventModel){
        try{
            String value = JSONObject.toJSONString(eventModel);
            jedisUtil.lpush(key, value);
            return true;
        }catch (Exception e){
            logger.error("添加注册事件异常: " + e.getMessage());
            return false;
        }
    }


}
