package com.ackerman.reddit.dao;

import com.ackerman.reddit.model.Message;
import jdk.nashorn.internal.objects.annotations.Where;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface MessageDAO {
    String TABLE_NAME = " message ";
    String INSERT_FIELDS = " from_id, to_id, has_read, conversation_id, content, create_date ";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    @Insert({"INSERT INTO", TABLE_NAME, "(", INSERT_FIELDS, ")",
            "VALUES", "( #{fromId}, #{toId}, #{hasRead}, #{conversationId}, #{content}, #{createDate})"})
    int insertMessage(Message message);

    @Select({"SELECT", SELECT_FIELDS, "FROM", TABLE_NAME, "WHERE conversation_id = #{id} ORDER BY id DESC LIMIT 1"})
    Message selectMessageByConversationId(String id);



    @Select({"SELECT DISTINCT(conversation_id) FROM", TABLE_NAME, "WHERE from_id = #{id} or to_id = #{id} ORDER BY id DESC"})
    List<String> selectConversationId(int id);

    @Select({"SELECT", SELECT_FIELDS, "FROM", TABLE_NAME, "WHERE conversation_id = #{conversationId} ORDER BY id DESC"})
    List<Message> selectMessagesInConversationByIds(String conversationId);


    @Select({"SELECT", INSERT_FIELDS, ", count(id) AS id FROM ",
            "(SELECT", SELECT_FIELDS, "FROM", TABLE_NAME,
            "WHERE to_id = #{userId} or from_id = #{userId} ORDER BY id DESC) AS a ",
            "GROUP BY conversation_id ORDER BY create_date DESC"})
    List<Message> selectConversationListByUserId(int userId);

    @Select({"SELECT count(id) AS unreadMessages FROM ", TABLE_NAME,
            "WHERE has_read = 0 and conversation_id = #{conversationId} and from_id = #{communicatorId}"})
    int countConversationUnReadMessage(@Param("conversationId") String conversationId,
                                       @Param("communicatorId") int communicatorId);

    @Select({"SELECT COUNT(id) FROM", TABLE_NAME, "WHERE to_id = #{userId} and has_read = 0"})
    int countUnreadMessages(int userId);

    @Update({"Update", TABLE_NAME, "SET has_read = 1 WHERE conversation_id = #{conversationId} and to_id = #{userId}"})
    void updateReadStatusInConversationByIds(@Param("userId") int userId,
                                             @Param("conversationId") String conversationId);

}