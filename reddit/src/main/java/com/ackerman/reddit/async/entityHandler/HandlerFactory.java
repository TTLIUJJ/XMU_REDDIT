package com.ackerman.reddit.async.entityHandler;

import com.ackerman.reddit.async.EventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HandlerFactory {
    private static Logger logger = LoggerFactory.getLogger(HandlerFactory.class);

    @Autowired
    private RegisterHandler registerHandler;

    @Autowired
    private ConversationHandler conversationHandler;

    @Autowired
    private SubscriptionPushHandler subscriptionPushHandler;

    @Autowired
    private ReportedNewsHandler reportedNewsHandler;

    public EventHandler getEventHandler(EventType type){
        try{
            switch (type){
                case REGISTER: return registerHandler;
                case LOGIN: break;
                case COMMENT: break;
                case NEWS: break;
                case CONVERSATION: return conversationHandler;
                case SUBSCRIPTION_PUSH: return subscriptionPushHandler;
                case REPORTED_NEWS: return reportedNewsHandler;
            }
        }catch (Exception e){
            e.getStackTrace();
        }
        return null;
    }
}
