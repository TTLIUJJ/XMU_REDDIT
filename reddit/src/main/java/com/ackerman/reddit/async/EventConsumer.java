package com.ackerman.reddit.async;

import com.ackerman.reddit.async.entityHandler.*;
import com.ackerman.reddit.utils.Entity;
import com.ackerman.reddit.utils.JedisUtil;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class EventConsumer implements InitializingBean, ApplicationContextAware{
    private static Logger logger = LoggerFactory.getLogger(EventConsumer.class);
    private Map<EventType, List<EventHandler>> config = new HashMap<>();
    private ApplicationContext applicationContext;

    @Autowired
    private JedisUtil jedisUtil;

    @Autowired
    private HandlerFactory handlerFactory;

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, EventHandler> beans = applicationContext.getBeansOfType(EventHandler.class);
        if(beans != null){
            for(Map.Entry<String, EventHandler> entry : beans.entrySet()){
                EventType type = entry.getValue().getEventType();
                config.put(type, new ArrayList<EventHandler>());
                config.get(type).add(entry.getValue());
                logger.info("1 type: " + String.valueOf(type));
            }
        }

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    String key = Entity.getEventQueueKey();
                    List<String> events = jedisUtil.brpop(key, 0);

                    for(String jsonModel : events){
                        if(jsonModel.equals(key)){
                            continue;
                        }
                        EventModel eventModel = JSONObject.parseObject(jsonModel, EventModel.class);
                        logger.info(jsonModel);
                        if(eventModel == null){
                            logger.info("错误的事件model");
                            continue;
                        }

                        EventType type = eventModel.getEventType();
                        logger.info("type: " + type);
                        if(!config.containsKey(type)){
                            logger.error("事件类型: " +String.valueOf(type) + " 未被注册 请查询事件类型");
                            continue;
                        }

                        EventHandler handler = handlerFactory.getEventHandler(type);
                        handler.processEvent(eventModel);
                    }
                }
            }
        });

        thread.start();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
