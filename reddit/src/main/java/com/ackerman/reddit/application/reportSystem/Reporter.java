package com.ackerman.reddit.application.reportSystem;

import com.ackerman.reddit.application.Observer;
import com.ackerman.reddit.async.EventModel;
import com.ackerman.reddit.async.EventProducer;
import com.ackerman.reddit.async.EventType;
import com.ackerman.reddit.utils.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;


@Component
public class Reporter implements Observer {
    private static Logger logger = LoggerFactory.getLogger(Reporter.class);

    @Autowired
    private EventProducer eventProducer;

    private int userId;
    private int newsId;

    public int getNewsId() {
        return newsId;
    }

    public Reporter setNewsId(int newsId) {
        this.newsId = newsId;
        return this;
    }

    public int getUserId() {
        return userId;
    }

    public Reporter setUserId(int userId) {
        this.userId = userId;
        return this;
    }

    @Override
    public void update(Object info) {
        //通知我干什么: 发一条消息, 告诉你处理结果
        //然后触发eventHandler
        Map<String, Object> extraInfo = (Map<String, Object>)info;
        EventModel model = new EventModel();

        model.setEventType(EventType.REPORTED_NEWS).setEntityType(Entity.ENTITY_MESSAGE).setProducerId((Integer)extraInfo.get("producerId")).setEventDate(new Date()).setExtraInfo(extraInfo);
        eventProducer.produceEvent(model);
    }


}
