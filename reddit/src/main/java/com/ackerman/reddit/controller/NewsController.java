package com.ackerman.reddit.controller;

import com.ackerman.reddit.async.EventProducer;
import com.ackerman.reddit.async.EventType;
import com.ackerman.reddit.async.EventModel;
import com.ackerman.reddit.model.Comment;
import com.ackerman.reddit.model.News;
import com.ackerman.reddit.model.User;
import com.ackerman.reddit.service.*;
import com.ackerman.reddit.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Controller
public class NewsController{
    private static Logger logger = LoggerFactory.getLogger(NewsController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private NewsService newsService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private QiniuService qiniuService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private EventProducer eventProducer;

    @RequestMapping(path = {"/share"}, method = {RequestMethod.POST, RequestMethod.GET})
    public String share(Model model,
                        @RequestParam(value = "type", defaultValue = "3") int type){
        try {
            User user = hostHolder.getUser();
            if(user == null){
                return "redirect: login";
            }

            if (type == 3) {
                model.addAttribute("navActive", 3);
            } else {
                model.addAttribute("navActive", 6);
            }
        }catch (Exception e){
            logger.info("share页面异常: " + e.getMessage());
            e.getStackTrace();
        }

        return "share";
    }

    @RequestMapping(path = {"/uploadImage"}, method = {RequestMethod.POST})
    @ResponseBody
    public String uploadImage(@RequestParam("uploadImg") MultipartFile imageFile){
        logger.info("in uploadImage");
        Map<String, Object> map = new HashMap<>();
        String imageUrl = null;
        try{
            imageUrl = qiniuService.savaImage(imageFile);
            if(imageUrl == null){
                map.put("fail", "图片上传异常");
            }
        }catch (Exception e){
            logger.error("添加分享图片失败" + e.getMessage());
            map.put("fail", "图片上传异常");
        }
        return RedditUtil.getJSONString(0, imageUrl);
    }

    @RequestMapping(path = {"/publish"}, method = {RequestMethod.POST, RequestMethod.GET})
    public String publish(){
        return "shareNews";
    }

    @RequestMapping(path = {"/addShareNews"}, method={RequestMethod.POST})
    public String addShareNews(@RequestParam("title") String title,
                               @RequestParam("link") String link,
                               @RequestParam(value = "imageUrl", defaultValue = "") String imageUrl,
                               @RequestParam(value = "type", defaultValue = "3") int type){
        try {
            //link有两个意思
            //当type == 3时, link是分享的页面的链接
            //当type == 6时, link的是原创的内容

            User user = hostHolder.getUser();

            if(user == null){
                return "redirect:/login";
            }

            News news = new News();
            news.setType(type);
            news.setTitle(title);
            news.setLink(link);
            if(imageUrl.equals("")){
                //设置默认图片
                news.setImageLink("http://oz15aje2y.bkt.clouddn.com/b402d0f985e64470a33a954ee400cb17.jpg");
            }
            else {
                news.setImageLink(imageUrl);
            }
            news.setCreateDate(new Date());
            news.setUserId(hostHolder.getUser().getId());

            //往MySQL数据库添加新闻
            newsService.addNews(news);

            //往Redis数据库添加新闻
            //news.id自动增加....有点神奇, news不是取出来的, 是新建的...
            newsService.addPopularNews(news.getId());

            try{
                List<Integer> fansList = newsService.getMyFans(user.getId());
                Map<String, Object> extraInfo = new HashMap<>();
                extraInfo.put("fansList", fansList);
                extraInfo.put("userId", user.getId());
                extraInfo.put("newsId", news.getId());

                EventModel eventModel = new EventModel();
                eventModel.setEventType(EventType.SUBSCRIPTION_PUSH).setEntityType(Entity.ENTITY_MESSAGE).setEventDate(new Date()).setProducerId(user.getId()).setExtraInfo(extraInfo);
                eventProducer.produceEvent(eventModel);
            }catch (Exception e){
                logger.info("触发推送事件失败: " + e.getMessage());
            }

        }catch (Exception e){
            logger.error("添加分享新闻失败" + e.getMessage());
            return "redirect:/exception";
        }

        return "redirect:/news";
    }

    @RequestMapping(path={"/newsDisplay/{newsId}"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String newsPage(@PathVariable("newsId") int newsId,
                           Model model){

        try {
            News news = newsService.getNewsById(newsId);
            if(news == null){
                return "redirect:/";
            }
            List<ViewObject> commentVOs = new ArrayList<>();
            List<Comment> commentList = commentService.getCommentsByEntity(Entity.ENTITY_COMMENT, newsId);

            for (Comment comment : commentList) {
                ViewObject vo = new ViewObject();
                vo.set("comment", comment);
                vo.set("user", userService.getUser(comment.getUserId()));
                vo.set("showTime", RedditUtil.parseShowTime(comment.getCreateDate()));

                commentVOs.add(vo);
            }
            model.addAttribute("commentVOs", commentVOs);
            model.addAttribute("news", news);
            model.addAttribute("showTime", RedditUtil.parseShowTime(news.getCreateDate()));
            model.addAttribute("owner", userService.getUser(news.getUserId()));
        }catch (Exception e){
            logger.info("获取新闻页面失败" + e.getMessage());
            return "redirect:/";
        }

        return "newsDisplay";
    }

    @RequestMapping(path = {"/newsAttitude"}, method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String setUserAttitudeWithNews(@RequestParam("newsId") int newsId,
                                          @RequestParam(value = "attitude", defaultValue = "0") int attitude){
        long likeCount = 0;
        User user = hostHolder.getUser();
        if(user == null){
            return RedditUtil.getJSONString(1, "登陆后才能点击");
        }
        try {
            //flag判断用户是否为新闻的支持者
            //likeCount是有符号的
            int flag = newsService.getAttitude(user.getId(), Entity.ENTITY_NEWS, newsId);
            if (attitude == flag) {
                //无态度
                likeCount = newsService.resetAttitude(user.getId(), Entity.ENTITY_NEWS, newsId);
            }
            else if(attitude == 1){
                //不管以前咋样, 现在就是喜欢
                likeCount = newsService.support(user.getId(), Entity.ENTITY_NEWS, newsId);
            }
            else{
                //不管以前咋样, 现在就是不喜欢
                likeCount = newsService.oppose(user.getId(), Entity.ENTITY_NEWS, newsId);
            }
            newsService.updateScore(newsId, (int)likeCount);
        }catch (Exception e){
            logger.error("更新新闻态度异常" + e.getMessage());
            return RedditUtil.getJSONString(1, "系统出错");
        }
        newsService.updateLikeCount((int)likeCount, newsId);
        return RedditUtil.getJSONString(0, String.valueOf(likeCount));
    }


}
