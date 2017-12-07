package com.ackerman.reddit.controller;

import com.ackerman.reddit.model.User;
import com.ackerman.reddit.service.QiniuService;
import com.ackerman.reddit.service.UserService;
import com.ackerman.reddit.utils.RedditUtil;
import com.ackerman.reddit.utils.ViewObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

@Controller
public class TestController {
    private static Logger logger = LoggerFactory.getLogger(TestController.class);

    @Autowired
    private QiniuService qiniuService;

    @Autowired
    private UserService userService;

    @RequestMapping(path = {"/aa", "/indexaa"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String index(){

        return "home";
    }

    @RequestMapping(value="/request")
    @ResponseBody
    public String request(HttpServletRequest request,
                          HttpServletResponse response,
                          HttpSession session){
        StringBuilder sb = new StringBuilder();
        Enumeration<String> headNames = request.getHeaderNames();

        while(headNames.hasMoreElements()){
            String name = headNames.nextElement();
            sb.append(name + ": " + request.getHeader(name) + "<br/>");
        }

        sb.append("<br/>");
        for(Cookie cookie: request.getCookies()){
            sb.append(cookie.getName() + ": " + cookie.getValue() + "<br/>");
        }

        return sb.toString();
    }

    @RequestMapping(value ="/response")
    @ResponseBody
    public String response(@RequestParam(value = "key", defaultValue = "IamKey") String key,
                           @RequestParam(value = "value", defaultValue = "IamValue") String value,
                            HttpServletResponse response){
        response.addCookie(new Cookie(key, value));

        return "";
    }

    @RequestMapping(value="/redirect/{code}")
    public RedirectView redirect(@PathVariable("code") int code,
                                 HttpSession session){
        RedirectView red = new RedirectView("/cherry", true);
        if(code == 301){
            red.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
        }

        session.setAttribute("myMsg", "hello ni hao a ");

        return red;
    }

    @RequestMapping(value="/cherry")
    public String cherry(HttpSession session){
        StringBuilder sb = new StringBuilder();
        sb.append(session.getAttribute("myMsg"));

        return "shareNews";
    }

    @RequestMapping(path={"/apple"}, method = {RequestMethod.POST})
    @ResponseBody
    public String apple(@RequestParam("pic") MultipartFile file){
        logger.info("aaaa");
        String name = file.getOriginalFilename();
        logger.info("BBBB");
        logger.info(name);

        String xx = null;
        try{
            xx = qiniuService.savaImage(file);
        }catch (Exception e){

        }
        return xx;
    }

    @RequestMapping(path = {"/getApple"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String getAapple(Model model){
        List<User> users = new ArrayList<>();
        logger.info("??");
        for(int i = 0; i < 7; ++i){
            User user = new User();
            user.setUsername(String.format("username%d%d", i, i));
            user.setPassword("iiiiiiiiiii");
            users.add(user);
        }
        model.addAttribute("vos", users);
        logger.info("!!");
        return "fuck";
    }

    @RequestMapping(path={"/ajax"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String ajax(){
        return "ajax";
    }

    @RequestMapping(path={"/shit"}, method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String shit(@RequestParam(value = "name", defaultValue = "") String name,
                       @RequestParam(value = "age", defaultValue = "0") int age){
        logger.info("进来了" + name + age);

        return RedditUtil.getJSONString(0, String.valueOf(1));
    }

    @RequestMapping(path={"/fuck"}, method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String fuck(@RequestParam(value = "name", defaultValue = "") String name,
                       @RequestParam(value = "age", defaultValue = "0") int age){
        logger.info("来了 " + name + " " + age);

        return RedditUtil.getJSONString(0, String.valueOf(1));
    }



    @RequestMapping(path = {"/bear"}, method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public String xxx(@RequestParam("attt") int attt,
                      @RequestParam(value = "aa", defaultValue = "0") int aa){

        logger.info(String.valueOf(attt));
        logger.info(String.valueOf(aa));

        return RedditUtil.getJSONString(1, "xxxxx");
    }

    @RequestMapping(path={"/girl"}, method = {RequestMethod.POST, RequestMethod.GET})
    public String girl(Model model){

        List<ViewObject> list = new ArrayList<>();
        for(int i = 1; i <= 20; ++i){
            User user = new User();
            user.setId(i);
            user.setUsername(String.format("%d%d", i,i));
            ViewObject vo = new ViewObject();
            vo.set("user", user);
            list.add(vo);
        }
        model.addAttribute("vos", list);
        return "zheader";
    }


}