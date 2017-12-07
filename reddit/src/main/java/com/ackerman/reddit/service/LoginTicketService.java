package com.ackerman.reddit.service;

import com.ackerman.reddit.dao.LoginTicketDAO;
import com.ackerman.reddit.model.LoginTicket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class LoginTicketService {
    @Autowired
    private LoginTicketDAO loginTicketDAO;

    public void addLoginTicket(LoginTicket ticket){
        loginTicketDAO.addLoginTicket(ticket);
    }

    public String addLoginTicketByUserId(int userId){
        String ticket = String.valueOf(userId);

        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(userId);
        loginTicket.setStatus(0);
        Date date = new Date();
        date.setTime(date.getTime() + 1000 * 7 * 24 * 3600);
        loginTicket.setExpired(date);
        ticket =  (ticket + UUID.randomUUID().toString().replaceAll("-", "")).substring(0, 32);
        loginTicket.setTicket(ticket);

        loginTicketDAO.addLoginTicket(loginTicket);

        return ticket;
    }

    public void deleteLoginTicket(String ticket, int status){
        loginTicketDAO.invalidTicket(ticket, status);
    }

    public String getLastestTicketByUserId(int userId){
        return loginTicketDAO.selectLastestTicketByUserId(userId);
    }

    public LoginTicket getLoginTicketByTicket(String ticket){
        return loginTicketDAO.selectLoginTicketByTicket(ticket);
    }
}
