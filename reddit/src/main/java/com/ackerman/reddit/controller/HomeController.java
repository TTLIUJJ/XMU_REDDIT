package com.ackerman.reddit.controller;

import com.ackerman.reddit.model.News;
import com.ackerman.reddit.model.ScoreModel;
import com.ackerman.reddit.model.User;
import com.ackerman.reddit.service.NewsService;
import com.ackerman.reddit.service.UserService;
import com.ackerman.reddit.utils.Entity;
import com.ackerman.reddit.utils.JedisUtil;
import com.ackerman.reddit.utils.RedditUtil;
import com.ackerman.reddit.utils.ViewObject;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;
import java.util.logging.Logger;

@Controller
public class HomeController implements InitializingBean{
    private Logger logger = Logger.getLogger("HomeController Log");

    @Autowired
    private NewsService newsService;

    @Autowired
    private UserService userService;

    @Autowired
    private JedisUtil jedisUtil;


    private long oneWeekMilliSeconds = 604800000;

    @RequestMapping(path={"/news"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String getNews(@RequestParam(value="index", defaultValue="0") int index,
                          @RequestParam(value="prevNewsId", defaultValue = "0") int prevNewsId,
                          @RequestParam(value="direction", defaultValue = "0") int direction,
                          Model model){
        List<News> newsList = new ArrayList<>();


        if(prevNewsId == 0){
            newsList = newsService.getLastNews(0, 0, 10);
        }else {
            //direction 判断向前或向后
            if(direction == 1) {
                newsList = newsService.getBeforeIdAndLimit(prevNewsId, 0, 10);
            }
            else{
                index -= 11;
                newsList = newsService.getAfterIdAndLimit(prevNewsId, 0, 10);
                //倒序处理
                Collections.reverse(newsList);
            }
        }


        List<ViewObject> vos = new ArrayList<>();
        for(News news: newsList){
            ViewObject vo = new ViewObject();
            User user = userService.getUser(news.getUserId());
            vo.set("news", news);
            vo.set("user", user);
            vo.set("index", ++index);

            vos.add(vo);
        }

        if(vos.size() != 10){
            model.addAttribute("stopNext", 1);
        }

        model.addAttribute("vos", vos);
        model.addAttribute("navActive", 2);

        return "home";
    }

    @RequestMapping(path={"/popular", "/"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String getPopular(@RequestParam(value="index", defaultValue="0") int index,
                             @RequestParam(value="prevNewsId", defaultValue = "0") int prevNewsId,
                             @RequestParam(value="direction", defaultValue = "0") int direction,
                             Model model){


        //优先队列: 分值大的在队头
        PriorityQueue<News> queue = newsService.getPopularNews();
        if(queue == null || queue.size() == 0){
            return "redirect:/news";
        }


        List<ViewObject> vos = new ArrayList<>();
        int oldestId = Integer.MAX_VALUE;

        // direction == 0 : 首页
        // 将传进来的index个全部pop掉

        if(direction == -1){
            index -= 11;
        }
        int pop_num = index;

        //direction == 0 情况只出现在第一页
        if(direction != 0){
            while(pop_num --  != 0){
                if(!queue.isEmpty()){
                    News news = queue.poll();
                    //找到热度排序中id最小(最旧)的那条新闻
                    if(news.getId() < oldestId){
                        oldestId = news.getId();
                    }
                }
            }
        }


        while(!queue.isEmpty()){
            News news = queue.poll();

            if(news.getId() < oldestId){
                oldestId = news.getId();
            }

            User user = userService.getUser(news.getUserId());

            ViewObject vo = new ViewObject();
            vo.set("news", news);
            vo.set("user", user);
            vo.set("index", ++index);

            vos.add(vo);

            if(index % 10 == 0){
                break;
            }
        }


        //现在先考虑下一页,
        //热门和旧的新闻拼凑的页面
        List<News> newsList = null;
        if(queue.isEmpty() && vos.size() != 0){
           newsList = newsService.getBeforeIdAndLimit(oldestId, 0, 10);
        }
        //完全由旧新闻拼凑的页面
        else if(queue.isEmpty() && vos.size() == 0){
            if(direction == 1) {
                newsList = newsService.getBeforeIdAndLimit(prevNewsId, 0, 10);
            }
            else if(direction == -1){
                newsList = newsService.getAfterIdAndLimit(prevNewsId, 0, 10);
                Collections.reverse(newsList);
            }
        }

        while(vos.size() != 10 && !newsList.isEmpty()){
            News news = newsList.remove(0);
            User user = userService.getUser(news.getUserId());

            ViewObject vo = new ViewObject();
            vo.set("news", news);
            vo.set("user", user);
            vo.set("index", ++index);

            vos.add(vo);
        }

        //判断是否需要加入stopNext
        if(vos.size() != 10 || newsService.countNews() <= index){
            model.addAttribute("stopNext", 1);
        }


        model.addAttribute("vos", vos);
        model.addAttribute("navActive", 1);

        return "home";
    }

    @Override
    public void afterPropertiesSet() throws Exception {


        Date oneWeekAgo = new Date();
        oneWeekAgo.setTime(oneWeekAgo.getTime() - oneWeekMilliSeconds);
        //找出一周以内新闻
        List<News> newsList = newsService.getNewsAfterDate(oneWeekAgo);
        if(newsList == null || newsList.size() == 0){
            return;
        }

        String key = Entity.getScoreHashKey();

        //计算每个新闻的分值
        //并且加入redis队列
        for(News news: newsList){

            Date createDate = news.getCreateDate();
            Date expiredDate = new Date();
            expiredDate.setTime(createDate.getTime() + oneWeekMilliSeconds);

            ScoreModel scoreModel = new ScoreModel();
            scoreModel.setLikeCount(news.getLikeCount());
            scoreModel.setExpiredDate(expiredDate);

            RedditUtil.calculateScore(scoreModel);

            String field = String.valueOf(news.getId());
            String value = JSONObject.toJSONString(scoreModel);
            jedisUtil.hset(key, field, value);
        }

    }

}
