package com.ackerman.reddit.dao;

import com.ackerman.reddit.model.News;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;

@Mapper
public interface NewsDAO {
    String TABLE_NAME = " news ";
    String INSERT_FIELDS = " type, user_id, like_count, comment_count, title, link, image_link, create_date ";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    @Insert({"INSERT INTO ", TABLE_NAME, "(", INSERT_FIELDS, ")",
            "VALUES ( #{type}, #{userId}, #{likeCount}, #{commentCount}, #{title}, #{link}, #{imageLink}, #{createDate})"})
    int addNews(News news);

    @Select({"SELECT count(id) FROM", TABLE_NAME})
    int countNews();

    @Select({"SELECT comment_count FROM", TABLE_NAME, "WHERE id = #{newsId}"})
    int getCommentCount(int newsId);

    @Select({"SELECT", SELECT_FIELDS, "FROM", TABLE_NAME, "WHERE id = #{id}"})
    News selectNewsById(int id);

    @Select({"SELECT", SELECT_FIELDS, "FROM", TABLE_NAME, "ORDER BY id ASC LIMIT 1"})
    News selectOldestNews();

    @Select({"SELECT", SELECT_FIELDS, "FROM", TABLE_NAME, "WHERE create_date >= #{date}"})
    List<News> selectNewsAfterDate(Date date);

    @Select({"SELECT", SELECT_FIELDS, "FROM", TABLE_NAME, "WHERE id >= #{min} and id <= #{max}"})
    List<News> selectByIdFields(@Param("min") int min,
                                @Param("max") int max);

    @Select({"SELECT", SELECT_FIELDS, "FROM", TABLE_NAME, "WHERE id < #{id} ORDER BY id DESC"})
    List<News> selectBeforeId(int id);

    @Select({"SELECT", SELECT_FIELDS, "FROM", TABLE_NAME, "WHERE id < #{id} ORDER BY id DESC LIMIT #{offset}, #{limit}"})
    List<News> selectBeforeIdAndLimit(@Param("id")int id,
                             @Param("offset")int offset,
                             @Param("limit") int limit);

    @Select({"SELECT", SELECT_FIELDS, "FROM", TABLE_NAME, "WHERE id > #{id} ORDER BY id ASC LIMIT #{offset}, #{limit}"})
    List<News> selectAfterIdAndLimit(@Param("id") int id,
                                     @Param("offset") int offset,
                                     @Param("limit") int limit);

    @Select({"SELECT", SELECT_FIELDS, "FROM", TABLE_NAME, "WHERE user_id = #{id} ORDER BY id DESC"})
    List<News> selectNewsByUserId(int userId);

    @Select({"SELECT COUNT(id) FROM", TABLE_NAME, "WHERE id < #{id}"})
    int countNewsBeforeId(int id);

    @Update({"UPDATE", TABLE_NAME, "SET like_count = #{likeCount} WHERE id = #{newsId}"})
    void updateLikeCountById(@Param("likeCount") int likeCount,
                             @Param("newsId") int newsId);

    @Update({"UPDATE", TABLE_NAME, "SET comment_count = comment_count+1 WHERE id = #{newsId}"})
    void incrCommentCountById(@Param("newsId") int newsId);



    List<News> selectNewsByUserIdAndOffset(@Param("userId") int userId,
                                           @Param("offset") int offset,
                                           @Param("limit") int limit);


}
