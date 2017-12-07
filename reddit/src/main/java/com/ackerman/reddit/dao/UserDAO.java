package com.ackerman.reddit.dao;


import com.ackerman.reddit.model.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserDAO {
    String TABLE_NAME = " user ";
    String INSERT_FIELDS = " username, password, salt, head_image_url ";
    String SELECT_FIELDS = "id, " + INSERT_FIELDS;

    @Insert({"INSERT INTO ", TABLE_NAME, "(", INSERT_FIELDS, ")",
            "VALUES ( #{username}, #{password}, #{salt}, #{headImageUrl} )"})
    int addUser(User user);

    @Select({"SELECT ", SELECT_FIELDS, " FROM ", TABLE_NAME, "WHERE id = #{id}"})
    User selectUserById(int id);

    @Select({"SELECT ", SELECT_FIELDS, " FROM ", TABLE_NAME, " WHERE username = #{username}"})
    User selectUserByUsername(String username);

}
