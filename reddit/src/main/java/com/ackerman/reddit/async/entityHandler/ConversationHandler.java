package com.ackerman.reddit.async.entityHandler;

import com.ackerman.reddit.async.EventModel;
import com.ackerman.reddit.async.EventType;
import com.ackerman.reddit.model.Message;
import com.ackerman.reddit.service.MessageService;
import com.ackerman.reddit.utils.RedditUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

@Component
public class ConversationHandler implements EventHandler {
    private static Logger logger = LoggerFactory.getLogger(ConversationHandler.class);

    @Autowired
    private MessageService messageService;

    @Override
    public void processEvent(EventModel model) {
        try {
            Message message = new Message();
            Map<String, Object> map = model.getExtraInfo();

            String conversationId = RedditUtil.setConversationId(model.getProducerId(), (Integer) map.get("communicator"));

            message.setConversationId(conversationId);
            message.setHasRead(0);
            message.setContent((String) map.get("content"));
            message.setFromId(model.getProducerId());
            message.setToId((Integer) map.get("communicator"));
            message.setCreateDate(new Date());

            messageService.addMessage(message);
        }catch (Exception e){
            logger.info("消息发送失败");
            e.getStackTrace();
        }

    }

    @Override
    public EventType getEventType() {
        return EventType.CONVERSATION;
    }
}
