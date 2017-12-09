package com.ackerman.reddit.service;

import com.ackerman.reddit.dao.LoginTicketDAO;
import com.ackerman.reddit.dao.UserDAO;
import com.ackerman.reddit.model.LoginTicket;
import com.ackerman.reddit.model.User;
import com.ackerman.reddit.utils.RedditUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.logging.Logger;

@Service
public class UserService {
    private static final Logger logger = Logger.getLogger("UserService");
    private static Random random = new Random(47);

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private LoginTicketService loginTicketService;

    public void addUser(User user){
        userDAO.addUser(user);
    }

    public User getUser(int id){
        return userDAO.selectUserById(id);
    }

    public User getUserByUsername(String username){
        return userDAO.selectUserByUsername(username);
    }


    public Map<String, Object> register(String username, String password){
        Map<String, Object> map = new HashMap<>();
        if(StringUtils.isBlank(username)){
            map.put("msg", "输入错误: 用户名不能为空");
            return map;
        }

        if(StringUtils.isBlank(password) || StringUtils.length(password) < 6){
            map.put("msg", "输入错误: 密码必须大于等于6位");
            return map;
        }

        User user = userDAO.selectUserByUsername(username);
        if(user != null){
            map.put("msg", "输入错误: 用户名已被注册");
            return map;
        }


        user = new User();
        user.setUsername(username);
        user.setSalt(UUID.randomUUID().toString().substring(0, 10));
        String md5password = RedditUtil.MD5(password + user.getSalt());
        if(md5password == null){
            map.put("msg", "注册失败: 系统异常");
            return map;
        }
        user.setPassword(md5password);
        //设置默认头像图片
        user.setHeadImageUrl("http://oz15aje2y.bkt.clouddn.com/e5c8021c4d6a40c593749f602aebdbe2.jpg");
        userDAO.addUser(user);

        String ticket =  loginTicketService.addLoginTicketByUserId(user.getId());
        map.put("ticket", ticket);
        map.put("userId", user.getId());

        return map;
    }

    public Map<String, Object> login(String username, String password){
        Map<String, Object> map = new HashMap<>();
        if(StringUtils.isBlank(username) || StringUtils.isBlank(password)){
            map.put("msg", "输入错误: 用户名或密码输入为空");
            return  map;
        }
        User user = userDAO.selectUserByUsername(username);
        if(user == null){
            map.put("msg", "输入错误: 用户不存在");
            return map;
        }
        if(!user.getPassword().equals(RedditUtil.MD5(password + user.getSalt()))){
            map.put("msg", "输入错误: 密码错误");
            return map;
        }

        String ticket = loginTicketService.addLoginTicketByUserId(user.getId());
        map.put("ticket", ticket);
        map.put("user", user);

        return map;
    }
}
