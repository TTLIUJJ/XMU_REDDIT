package com.ackerman.reddit.async.entityHandler;

import com.ackerman.reddit.async.EventModel;
import com.ackerman.reddit.async.EventType;
import com.ackerman.reddit.model.Message;
import com.ackerman.reddit.service.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

@Component
public class RegisterHandler implements EventHandler{
    private static Logger logger = LoggerFactory.getLogger(RegisterHandler.class);

    @Autowired
    private MessageService messageService;

    @Override
    public void processEvent(EventModel model) {
        try {
            Message message = new Message();
            Map<String, Object> map = model.getExtraInfo();

            message.setFromId(0);
            message.setToId((Integer) map.get("userId"));
            message.setHasRead(0);
            message.setConversationId(String.format("0_%d", (Integer) map.get("userId")));
            message.setContent("Welcome to Reddit, now you should login in your email account to finish the registered");
            message.setCreateDate(new Date());

            messageService.addMessage(message);
        }catch (Exception e){
            logger.info("消息发送失败");
            e.getStackTrace();
        }
        
    }

    @Override
    public EventType getEventType(){
        return EventType.REGISTER;
    }
}
