package com.ackerman.reddit.async.entityHandler;


import com.ackerman.reddit.async.EventModel;
import com.ackerman.reddit.async.EventType;
import com.ackerman.reddit.model.Message;
import com.ackerman.reddit.model.News;
import com.ackerman.reddit.service.MessageService;
import com.ackerman.reddit.service.NewsService;
import com.ackerman.reddit.utils.RedditUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

@Component
public class ReportedNewsHandler implements EventHandler{
    private static Logger logger = LoggerFactory.getLogger(ReportedNewsHandler.class);

    @Autowired
    private MessageService messageService;

    @Autowired
    private NewsService newsService;

    @Override
    public void processEvent(EventModel model) {
        try{
            Message message = new Message();
            Map<String, Object> extraInfo = (Map<String, Object>)model.getExtraInfo();

            int toId = (Integer) extraInfo.get("communicator");
            News news = newsService.getNewsById((Integer)extraInfo.get("newsId"));
            String content = (String) extraInfo.get("content");
            content = "您之前举报的： " + news.getTitle() + ", 管理员已经做了如下的处理: " + content;

            message.setFromId(model.getProducerId());
            message.setToId(toId);
            message.setConversationId(RedditUtil.setConversationId(toId, model.getProducerId()));
            message.setHasRead(0);
            message.setCreateDate(new Date());
            message.setContent(content);

            messageService.addMessage(message);


            //还可以把 那些举报被举报的 不好的给删了
            //...
        }catch ( Exception e){
            logger.info("消息发送失败");
            e.getStackTrace();
        }
    }

    @Override
    public EventType getEventType() {
        return EventType.REPORTED_NEWS;
    }
}
