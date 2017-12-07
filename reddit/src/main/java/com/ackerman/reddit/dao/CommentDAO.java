package com.ackerman.reddit.dao;

import com.ackerman.reddit.model.Comment;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;


@Mapper
public interface CommentDAO {
    String TABLE_NAME = " comment ";
    String INSERT_FIELDS = " user_id, entity_type, entity_id, status, create_date, content ";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    @Insert({"INSERT INTO ", TABLE_NAME, "(", INSERT_FIELDS, ")",
            "VALUES(#{userId}, #{entityType}, #{entityId}, #{status}, #{createDate}, #{content})"})
    int insertComment(Comment comment);

    @Select({"SELECT ", SELECT_FIELDS, " FROM ", TABLE_NAME,
            " WHERE entity_type = #{entityType} and entity_id = #{entityId} ORDER BY id DESC"})
    List<Comment> selectCommentsByEntity(@Param("entityType") int entityType,
                                        @Param("entityId") int entityId);

    @Select({"SELECT", SELECT_FIELDS, "FROM", TABLE_NAME, "WHERE user_id = #{userId} ORDER BY id DESC"})
    List<Comment> selectCommentByUserId(int userId);
}
