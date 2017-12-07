package com.ackerman.reddit.utils;

import com.ackerman.reddit.model.User;
import org.springframework.stereotype.Component;


@Component
public class HostHolder {
    private ThreadLocal<User> users;

    public HostHolder(){
        users = new ThreadLocal<>();
    }

    public void setUser(User user){
        users.set(user);
    }

    public User getUser(){
        return users.get();
    }

    public void clear(){
        users.remove();
    }
}
