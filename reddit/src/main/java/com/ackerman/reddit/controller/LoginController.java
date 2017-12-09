package com.ackerman.reddit.controller;

import com.ackerman.reddit.application.mailSystem.MailSender;
import com.ackerman.reddit.async.EventModel;
import com.ackerman.reddit.async.EventProducer;
import com.ackerman.reddit.async.EventType;
import com.ackerman.reddit.model.User;
import com.ackerman.reddit.service.LoginTicketService;
import com.ackerman.reddit.service.UserService;
import com.ackerman.reddit.utils.Entity;
import com.ackerman.reddit.utils.RedditUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@Controller
public class LoginController {
    private static final Logger logger = Logger.getLogger("LoginController");

    @Autowired
    private UserService userService;

    @Autowired
    private LoginTicketService loginTicketService;

    @Autowired
    private EventProducer eventProducer;

    @Autowired
    private MailSender mailSender;

    @RequestMapping(path={"/register"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String register(){
        return "register";
    }

    @RequestMapping(path={"/doRegister"}, method = {RequestMethod.POST})
    public String doRegister(@RequestParam("username") String username,
                             @RequestParam("password") String password,
                             @RequestParam(value = "remember", defaultValue = "0") int remember,
                             HttpServletResponse response,
                             HttpSession session){

        Map<String, Object> map = userService.register(username, password);
        if(map.containsKey("ticket")){
            try{
                Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
                cookie.setPath("/");
                if(remember != 0){
                    cookie.setMaxAge(7 * 24 * 3600 * 1000);
                }
                response.addCookie(cookie);

            }catch (Exception e){
                logger.info("注册时发生异常 " + e.getMessage());
                map.put("type", 222);
                map.put("msg", "注册失败: 系统异常");
                session.setAttribute("exceptionMap", map);
                return "redirect:/exception";
            }
        }
        else{
            map.put("type", 222);
            session.setAttribute("exceptionMap", map);
            return "redirect:/exception";
        }

        int userId = (Integer) map.get("userId");

        EventModel eventModel = new EventModel();
        Map<String, Object> extraInfo = new HashMap<>();
        extraInfo.put("userId", userId);
        eventModel.setEventType(EventType.REGISTER).setEntityType(Entity.ENTITY_COMMENT).setEventDate(new Date()).setProducerId(1).setExtraInfo(extraInfo);
        eventProducer.produceEvent(eventModel);

        String subject = "[XUM_Reddit]verify your email address";
        Map<String, Object> info = new HashMap<>();
        User toUser = userService.getUser(userId);
        info.put("user", toUser);

        mailSender.sendMail(toUser.getUsername(), subject, "mailTemplate/register.html", info);

        return "redirect:/";
    }

    @RequestMapping(path={"/login"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String login(){
        return "login";
    }



    @RequestMapping(path = {"/doLogin"}, method = {RequestMethod.POST})
    public String doLogin(@RequestParam("username") String username,
                          @RequestParam("password") String password,
                          @RequestParam(value = "remember", defaultValue = "0") int remember,
                          HttpSession session,
                          HttpServletResponse response) {

        Map<String, Object> map = userService.login(username, password);

        if(map.containsKey("ticket")){
            Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
            cookie.setPath("/");
            if(remember != 0){
                cookie.setMaxAge( 7 * 24 * 3600 * 1000);
            }
            response.addCookie(cookie);
        }
        else{
            map.put("type", 111);
            session.setAttribute("exceptionMap", map);
            return "redirect:/exception";
        }

        return "redirect:/";
    }

    @RequestMapping(path = {"/logout"}, method = {RequestMethod.POST, RequestMethod.GET})
    public String logout(@CookieValue("ticket") String ticket){
        loginTicketService.deleteLoginTicket(ticket, 1);
        return "redirect:/";
    }
}
