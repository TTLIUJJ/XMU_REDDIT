package com.ackerman.reddit.controller;

import com.ackerman.reddit.application.reportSystem.Procurator;
import com.ackerman.reddit.model.News;
import com.ackerman.reddit.service.AdminService;
import com.ackerman.reddit.service.NewsService;
import com.ackerman.reddit.utils.*;
import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class AdminController {
    private static Logger logger = LoggerFactory.getLogger(AdminController.class);


    @Autowired
    private AdminService adminService;

    @Autowired
    private Procurator procurator;

    @RequestMapping(path = {"/admin"}, method = {RequestMethod.POST, RequestMethod.GET})
    public String adminIndex(){

        return "home";
    }

    @RequestMapping(path = {"/admin/reportedNews"}, method = {RequestMethod.POST, RequestMethod.GET})
    public String adminReportedNews(Model model){
        try{
            List<ViewObject> reportedVOs = new ArrayList<>();
            List<News> newsList = adminService.getReportedNews(Double.MAX_VALUE, 0);

            for(News news : newsList){
                ViewObject vo = new ViewObject();
                vo.set("news", news);
                vo.set("reporterCnt", (int)news.getScore());
                reportedVOs.add(vo);
            }
            model.addAttribute("reportedVOs", reportedVOs);

        }catch (Exception e){
            e.getStackTrace();
        }

        return "reportedNews";
    }

    @RequestMapping(path = {"/admin/processReport"}, method = {RequestMethod.POST})
    @ResponseBody
    public String processReport(@RequestParam("newsId") int newsId,
                                @RequestParam("content") String content){
        try{
            logger.info("newsId: " + newsId);
            logger.info("content: " + content);

            Map<String, Object> info = new HashMap<>();
            info.put("newsId", newsId);
            info.put("content", StringEscapeUtils.escapeHtml(content));
            info.put("producerId", 1);
            procurator.notifyObservers(info);

            return RedditUtil.getJSONString(0, "提交成功");
        }catch (Exception e){
            e.getStackTrace();
            return RedditUtil.getJSONString(1, "系统异常");
        }

    }

}
