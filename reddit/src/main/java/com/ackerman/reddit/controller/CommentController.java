package com.ackerman.reddit.controller;

import com.ackerman.reddit.model.Comment;
import com.ackerman.reddit.model.News;
import com.ackerman.reddit.model.User;
import com.ackerman.reddit.service.CommentService;
import com.ackerman.reddit.service.NewsService;
import com.ackerman.reddit.utils.Entity;
import com.ackerman.reddit.utils.HostHolder;
import com.ackerman.reddit.utils.RedditUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
public class CommentController {
    private static Logger logger = LoggerFactory.getLogger(CommentController.class);

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private CommentService commentService;

    @Autowired
    private NewsService newsService;

    @RequestMapping(path = "/newsPage/addComment", method = {RequestMethod.POST})
    @ResponseBody
    public String addComment(@RequestParam("newsId") int newsId,
                             @RequestParam("content") String content){

        try {
            User user = hostHolder.getUser();
            if(user == null){
                return RedditUtil.getJSONString(1, "登陆后才能评论");
            }

            Comment comment = new Comment();
            comment.setEntityType(Entity.ENTITY_COMMENT);
            comment.setEntityId(newsId);
            comment.setStatus(0);
            comment.setUserId(user.getId());
            comment.setCreateDate(new Date());
            comment.setContent(content);

            commentService.addComment(comment);
            newsService.incrCommentCount(newsId);

            Map<String, Object> map = new HashMap<>();
            map.put("reviewer", user.getUsername());
            map.put("comment_content", content);
            map.put("showTime", RedditUtil.parseShowTime(comment.getCreateDate()));
            map.put("commentCnt", newsService.getCommentCount(newsId));

            return RedditUtil.getJSONString(0, map);
        }catch (Exception e){
            logger.info("addComment异常: " + e.getMessage());
            return RedditUtil.getJSONString(1, "系统异常");
        }

    }
}
