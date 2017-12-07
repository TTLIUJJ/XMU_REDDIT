package com.ackerman.reddit;

import com.ackerman.reddit.model.*;
import com.ackerman.reddit.service.*;
import com.ackerman.reddit.utils.RedditUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = RedditApplication.class)
@Sql("/ini-sql.sql")
public class RedditInitDataTests {
    @Autowired
    private UserService userService;

    @Autowired
    private NewsService newsService;

    @Autowired
    private LoginTicketService loginTicketService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private MessageService messageService;



//    @Test
//    public void contextLoads(){
//        Random random = new Random(47);
//        for(int i = 0; i < 11; ++i){
//            User user = new User();
//            String username = String.format("user00%d", (i));
//            user.setUsername(username);
//            user.setSalt("salt");
//            String password = "ljj5588006";
//            user.setPassword(RedditUtil.MD5(password+user.getSalt()));
//            user.setHeadImageUrl(String.format("http://owj98yrme.bkt.clouddn.com//lufei/%d.jpg", i));
//            userService.addUser(user);
//        }
//        for(int i = 0; i < 20; ++i){
//            News news = new News();
//            news.setUserId(random.nextInt(10)+1);
//            news.setCommentCount(random.nextInt(100)+ 10);
//            news.setLikeCount(110 + random.nextInt(1000));
//            news.setTitle("News" + (i+1));
//            news.setLink("http://www.baidu.com");
//            news.setImageLink("http://owj98yrme.bkt.clouddn.com//nab/2.jpg");
//
//            Date date = new Date();
//            date.setTime(date.getTime() + 4 * 3600 * i * 1000);
//            news.setCreateDate(date);
//
//            newsService.addNews(news);
//
//            for(int j = 0; j < 6; ++j){
//                Comment comment = new Comment();
//                comment.setUserId(random.nextInt(11) + 1);
//                comment.setEntityType(1);
//                comment.setEntityId(news.getId());
//                comment.setCreateDate(new Date());
//                comment.setStatus(0);
//                comment.setContent(UUID.randomUUID().toString());
//
//                commentService.addComment(comment);
//            }
//        }
//
//        for(int i = 1; i < 6; ++i){
//            LoginTicket ticket = new LoginTicket();
//            ticket.setUserId(i);
//            ticket.setStatus(0);
//            ticket.setExpired(new Date());
//            ticket.setTicket(UUID.randomUUID().toString().replaceAll("-", "").substring(0,16));
//
//            loginTicketService.addLoginTicket(ticket);
//        }
//
//        for(int i = 1; i < 12; ++i){
//            for(int j = 0; j < 200; ++j){
//                Message message = new Message();
//                int to_id = random.nextInt(11) + 1;
//                int from_id = random.nextInt(11) + 1;
//                if(from_id == to_id){
//                    from_id = 0;
//                }
//                if(from_id == 3){
//                    message.setHasRead(1);
//                }
//                message.setFromId(i);
//                message.setToId(to_id);
//                message.setHasRead(0);
//                message.setContent(UUID.randomUUID().toString());
//                Date date = new Date();
//                date.setTime(date.getTime() + 1000 * 5 * 10);
//                message.setCreateDate(date);
//                if(to_id < i){
//                    message.setConversationId(String.valueOf(to_id) + "_" + String.valueOf(i));
//                }
//                else{
//                    message.setConversationId(String.valueOf(i) + "_" + String.valueOf(to_id));
//                }
//
//                messageService.addMessage(message);
//            }
//        }
//
//    }

    @Test
    public void testNews(){
        Random random = new Random(47);
        for(int i = 1; i <= 34; ++i){
            News news = new News();
            news.setImageLink(String.format("http://owj98yrme.bkt.clouddn.com//lufei/%d.jpg", random.nextInt(11)));
            if( i % 2 == 0){
                news.setType(3);
                news.setLink("http://www.baidu.com");
            }else{
                news.setType(6);
                news.setLink("this is no link it is original content");
            }
            news.setScore(0.0);
            news.setUserId(random.nextInt(10) + 1);
            news.setTitle("title" + i);
            news.setCommentCount(0);
            news.setLikeCount(0);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try{
                Date initDate = sdf.parse("2017-11-01 00:00:00");
                initDate.setTime(initDate.getTime() + i * 12 * 3600 * 1000);
                news.setCreateDate(initDate);
            }catch (Exception e){
                e.printStackTrace();
            }
            newsService.addNews(news);
        }
    }
}
