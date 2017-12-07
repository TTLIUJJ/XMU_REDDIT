package com.ackerman.reddit.interceptor;

import com.ackerman.reddit.model.LoginTicket;
import com.ackerman.reddit.model.User;
import com.ackerman.reddit.service.LoginTicketService;
import com.ackerman.reddit.service.MessageService;
import com.ackerman.reddit.service.UserService;
import com.ackerman.reddit.utils.HostHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Component
public class PassPortInterceptor implements HandlerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(PassPortInterceptor.class);

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private LoginTicketService loginTicketService;

    @Autowired
    private UserService userService;

    @Autowired
    private MessageService messageService;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        String ticket = null;
        for(Cookie cookie : httpServletRequest.getCookies()){
            if(cookie.getName().equals("ticket")){
                ticket = cookie.getValue();
                break;
            }
        }

        if(ticket != null){
            LoginTicket loginTicket = loginTicketService.getLoginTicketByTicket(ticket);
            if(loginTicket != null && loginTicket.getStatus() == 0 && loginTicket.getExpired().after(new Date())){
                User user = userService.getUser(loginTicket.getUserId());
                hostHolder.setUser(user);
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

        if (modelAndView != null && hostHolder.getUser() != null) {
            modelAndView.addObject("user", hostHolder.getUser());

            int cnt = messageService.getUnreadMessages(hostHolder.getUser().getId());
            modelAndView.addObject("unreadMessages", cnt);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        if(hostHolder != null) {
            hostHolder.clear();
        }
    }
}
