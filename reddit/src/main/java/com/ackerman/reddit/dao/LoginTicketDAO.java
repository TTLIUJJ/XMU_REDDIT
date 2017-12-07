package com.ackerman.reddit.dao;

import com.ackerman.reddit.model.LoginTicket;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Mapper
public interface LoginTicketDAO {
    String TABLE_NAME = " login_ticket ";
    String INSERT_FIELDS = " user_id, status, ticket, expired ";
    String SELECT_FIELDS = " id, "+ INSERT_FIELDS;

    @Insert({"INSERT INTO ", TABLE_NAME, "(", INSERT_FIELDS, ")",
            "VALUES( #{userId}, #{status}, #{ticket}, #{expired})"})
    int addLoginTicket(LoginTicket loginTicket);

    @Update({"UPDATE ", TABLE_NAME, "SET status = #{status} WHERE ticket = #{ticket}"})
    int invalidTicket(@Param("ticket") String ticket,
                      @Param("status") int status);

    @Select({"SELECT ticket FROM ", TABLE_NAME, " WHERE user_id = #{userId} ORDER BY id DESC LIMIT 1"})
    String selectLastestTicketByUserId(int userId);

    @Select({"SELECT ", SELECT_FIELDS, " FROM ", TABLE_NAME, " WHERE ticket = #{ticket}"})
    LoginTicket selectLoginTicketByTicket(String ticket);
}
