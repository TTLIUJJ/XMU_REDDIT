package com.ackerman.reddit.async.entityHandler;

import com.ackerman.reddit.async.EventModel;
import com.ackerman.reddit.async.EventType;
import com.ackerman.reddit.model.Message;
import com.ackerman.reddit.model.News;
import com.ackerman.reddit.model.User;
import com.ackerman.reddit.service.MessageService;
import com.ackerman.reddit.service.NewsService;
import com.ackerman.reddit.service.UserService;
import com.ackerman.reddit.utils.RedditUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
public class SubscriptionPushHandler implements EventHandler{
    private static Logger logger = LoggerFactory.getLogger(SubscriptionPushHandler.class);

    @Autowired
    private UserService userService;

    @Autowired
    private NewsService newsService;

    @Autowired
    private MessageService messageService;

    @Override
    public void processEvent(EventModel model) {
        try {
            Map<String, Object> map = model.getExtraInfo();

            List<Integer> fansList = (List<Integer>) map.get("fansList");
            News news = newsService.getNewsById((Integer) map.get("newsId"));
            User user = userService.getUser((Integer) map.get("userId"));

            for (Integer i : fansList) {
                Message message = new Message();
                message.setCreateDate(new Date());
                message.setContent("您关注的用户: " + user.getUsername() + ", 上传一篇新文章: " + "<a href=http://127.0.0.1:8080/newsDisplay/" + news.getId() + ">点击链接查看</a>");
                message.setHasRead(0);
                message.setFromId(user.getId());
                message.setToId(i);
                message.setConversationId(RedditUtil.setConversationId(user.getId(), i));

                messageService.addMessage(message);
            }
        }catch (Exception e){
            logger.info("消息发送失败");
            e.getStackTrace();
        }
    }

    @Override
    public EventType getEventType() {
        return EventType.SUBSCRIPTION_PUSH;
    }
}
