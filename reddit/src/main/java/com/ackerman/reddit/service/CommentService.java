package com.ackerman.reddit.service;

import com.ackerman.reddit.dao.CommentDAO;
import com.ackerman.reddit.model.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class CommentService {
    @Autowired
    private CommentDAO commentDAO;

    public int addComment(Comment comment){
        return commentDAO.insertComment(comment);
    }

    public List<Comment> getCommentsByEntity(int entityType, int entityId){
        return commentDAO.selectCommentsByEntity(entityType, entityId);
    }

    public List<Comment> getCommentsByUserId(int userId){
        return commentDAO.selectCommentByUserId(userId);
    }
}
