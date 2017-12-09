package com.ackerman.reddit.controller;

import com.ackerman.reddit.application.mailSystem.MailSender;
import com.ackerman.reddit.application.reportSystem.Procurator;
import com.ackerman.reddit.application.reportSystem.Reporter;
import com.ackerman.reddit.async.EventModel;
import com.ackerman.reddit.async.EventProducer;
import com.ackerman.reddit.async.EventType;
import com.ackerman.reddit.model.Comment;
import com.ackerman.reddit.model.News;
import com.ackerman.reddit.model.User;
import com.ackerman.reddit.service.CommentService;
import com.ackerman.reddit.service.IndividualService;
import com.ackerman.reddit.service.NewsService;
import com.ackerman.reddit.service.UserService;
import com.ackerman.reddit.utils.*;
import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
public class IndividualController {
    private static Logger logger = LoggerFactory.getLogger(IndividualController.class);

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private JedisUtil jedisUtil;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private NewsService newsService;

    @Autowired
    private IndividualService individualService;

    @Autowired
    private EventProducer eventProducer;

    @Autowired
    private Procurator procurator;

    @Autowired
    private Reporter reporter;

    @RequestMapping(path={"/individual/index"}, method = {RequestMethod.POST, RequestMethod.GET})
    public String individualIndex(@RequestParam(value = "who", defaultValue = "0") int who,
                                  Model model) {
        try {
            User user = hostHolder.getUser();
            //必须要登陆才能查看他人的页面
            if(user == null){
                //这里可以返回exception的, 这样就可以传递我们的信息： 你先给我登陆在看
                return "redirect:/login";
            }

            //简单加一条判断就可以查看别人的页面
            //rightBar选择格式2
            if(who != 0 && user.getId() != who){
                user = userService.getUser(who);
                model.addAttribute("barType", 2);
                model.addAttribute("otherUser", user);
            }
            else{
                model.addAttribute("barType", 1);
            }

            List<News> myNewsList = newsService.getNewsByUserId(user.getId());
            List<ViewObject> VOs = new ArrayList<>();


            for(News news: myNewsList){
                ViewObject vo = new ViewObject();
                vo.set("news", news);
                vo.set("showTime", RedditUtil.parseShowTime(news.getCreateDate()));

                String LikeKey = Entity.getAttitudeKey(Entity.ATTITUDE_LIKE, Entity.ENTITY_NEWS, news.getId());
                String DisLikeKey = Entity.getAttitudeKey(Entity.ATTITUDE_DISLIKE, Entity.ENTITY_NEWS, news.getId());
                int support = (int)jedisUtil.scard(LikeKey);
                int oppose = (int)jedisUtil.scard(DisLikeKey);
                vo.set("support", support);
                vo.set("oppose", oppose);

                VOs.add(vo);
            }
            model.addAttribute("VOs", VOs);
        }catch (Exception e){
            logger.info("/individual/myIndex异常: " + e.getMessage());
            return "/";
        }
        return "individualIndex";
    }

    @RequestMapping(path = {"/individual/blog"}, method = {RequestMethod.POST, RequestMethod.GET})
    public String individualBlog(Model model,
                                 @RequestParam(value = "who", defaultValue = "0") int who){
        try {
            logger.info("who: " + who);
            User user = hostHolder.getUser();
            if (user == null) {
                return "redirect:login/";
            }
            if(who != 0 && user.getId() != who){
                user = userService.getUser(who);
                model.addAttribute("otherUser", user);
                model.addAttribute("barType", 2);
            }
            else{
                model.addAttribute("barType", 1);
            }

            List<Comment> commentList = commentService.getCommentsByUserId(user.getId());
            List<ViewObject> commentVOs = new ArrayList<>();
            for(Comment comment: commentList){
                ViewObject vo = new ViewObject();
                News news = newsService.getNewsById(comment.getEntityId());

                vo.set("comment", comment);
                vo.set("news", news);
                vo.set("showTime", RedditUtil.parseShowTime(comment.getCreateDate()));
                commentVOs.add(vo);
            }
            model.addAttribute("commentVOs", commentVOs);
        }catch (Exception e){
            logger.info("获取个人留言日志失败: " + e.getMessage());
        }
        return "individualBlog";
    }

    @RequestMapping(path={"/individual/collection"}, method = {RequestMethod.POST, RequestMethod.GET})
    public String individualCollection(Model model){
        try{
            User user = hostHolder.getUser();
            if(user == null){
                return "redirect:/login";
            }
            List<News> collectNews = individualService.getCollectNews(user.getId());

            List<ViewObject> collectionVOs = new ArrayList<>();
            for(News news: collectNews){
                ViewObject vo = new ViewObject();
                vo.set("news", news);
                vo.set("user", userService.getUser(news.getUserId()));
                collectionVOs.add(vo);
            }
            model.addAttribute("collectionVOs", collectionVOs);
        }catch (Exception e){
            logger.info("获取收藏页面异常: " + e.getMessage());
            return "redirect:/";
        }

        return "individualCollections";
    }


    @RequestMapping(path = {"/individual/idols"}, method = {RequestMethod.POST, RequestMethod.GET})
    public String individualIdols(Model model){
        User user = hostHolder.getUser();
        String myIdolKey = Entity.getMyIdolKey(user.getId());
        Set<String> set = jedisUtil.smembers(myIdolKey);

        List<ViewObject> idolVOs = new ArrayList<>();
        for(String idolId : set){
            ViewObject vo = new ViewObject();
            User idol = userService.getUser(Integer.valueOf(idolId));
            vo.set("idol", idol);

            idolVOs.add(vo);
        }

        model.addAttribute("idolVOs", idolVOs);
        return "individualIdols";
    }

    @RequestMapping(path = {"/individual/talk"}, method = {RequestMethod.POST, RequestMethod.GET})
    public String talk(@RequestParam(value = "who", defaultValue = "0") int who,
                       Model model){
        try{
            User user = hostHolder.getUser();
            if(user == null){
                return "redirect:/login";
            }
            User otherUser = userService.getUser(who);
            if(who != 0 && user.getId() != who){
                user = userService.getUser(who);
                model.addAttribute("barType", 2);
                model.addAttribute("otherUser", user);
            }
            else{
                model.addAttribute("barType", 1);
            }

        }catch (Exception e){
            logger.info("/individual/talk异常: " + e.getMessage());
        }

        return "individualTalk";
    }

    @RequestMapping(path = {"/individual/message"}, method = {RequestMethod.POST})
    @ResponseBody
    public String privateMessage(@RequestParam(value="message", defaultValue = "") String message,
                                 @RequestParam("otherId") int otherId){
        String content = StringEscapeUtils.escapeHtml(message);
        if(content.length() < 3){
            return RedditUtil.getJSONString(1, "输入字数太少");
        }

        try{
            User user = hostHolder.getUser();

            EventModel model = new EventModel();
            Map<String, Object> map = new HashMap<>();
            map.put("content", content);
            map.put("communicator", otherId);

            model.setEventType(EventType.CONVERSATION).setEntityType(Entity.ENTITY_MESSAGE).setProducerId(user.getId()).setEventDate(new Date()).setExtraInfo(map);
            eventProducer.produceEvent(model);


        }catch (Exception e){
            logger.info("/individual/message异常: " + e.getMessage());
            return RedditUtil.getJSONString(1, "系统错误");
        }

        return RedditUtil.getJSONString(0, "消息发送成功");
    }



    @RequestMapping(path={"/individual/subscript"}, method = {RequestMethod.POST})
    @ResponseBody
    public String subscript(@RequestParam("userId") int userId){
        try{
            logger.info("subscript");
            User user = hostHolder.getUser();
            if(user == null){
                return RedditUtil.getJSONString(3, "登陆后才能关注");
            }

            User idol = userService.getUser(userId);
            String message = individualService.addFansAndIdol(idol.getId(), user.getId());
            return message;
        }catch (Exception e){
            logger.info("关注失败" + e.getMessage());
            return RedditUtil.getJSONString(-1, "系统异常");
        }
    }



    @RequestMapping(path = {"/collect"}, method = {RequestMethod.GET})
    @ResponseBody
    public String collect(@RequestParam(value = "newsId") int newsId){
        User user = hostHolder.getUser();
        if(user == null){
            return RedditUtil.getJSONString(1, "登陆后才能收藏");
        }
        try{
            int ret = individualService.collectNews(newsId, user.getId());
            if(ret == 1){
                return RedditUtil.getJSONString(2, "添加收藏成功");
            }
            else if(ret == -1){
                return RedditUtil.getJSONString(3, "取消收藏成功");
            }
            else{
                return RedditUtil.getJSONString(1, "系统出错");
            }
        }catch (Exception e){
            logger.info("添加收藏失败: " +  e.getMessage());
            return RedditUtil.getJSONString(1, "系统异常");
        }
    }


    @RequestMapping(path = {"/report"}, method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public String reportNews(@RequestParam("newsId") int newsId,
                             @RequestParam(value = "type", defaultValue = "") String type){

        try{
            if(type.equals("")){
                logger.info("前端type传递空值");
                return RedditUtil.getJSONString(1, "未知错误");
            }

            User user = hostHolder.getUser();
            if(user == null){
                return RedditUtil.getJSONString(1, "登陆后才能举报");
            }

            reporter.setUserId(user.getId()).setNewsId(newsId);
            if(type.equals("举报")) {
                procurator.registerObserver(reporter);
                return RedditUtil.getJSONString(2, "举报成功, Reddit将会在处理之后给您回复");
            }
            else if(type.equals("取消举报")){
                procurator.removeObserver(reporter);
                return RedditUtil.getJSONString(3, "您已取消举报");
            }
        }catch (Exception e){
            logger.info("reportNews异常: " + e.getMessage());
        }
        return RedditUtil.getJSONString(1, "系统异常");
    }
}
