package com.ackerman.reddit.service;

import com.ackerman.reddit.dao.MessageDAO;
import com.ackerman.reddit.model.Message;
import com.ackerman.reddit.utils.Entity;
import com.ackerman.reddit.utils.JedisUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class MessageService {
    private static Logger logger = LoggerFactory.getLogger(MessageService.class);

    @Autowired
    private MessageDAO messageDAO;

    @Autowired
    private JedisUtil jedisUtil;

    public int addMessage(Message message){
        return messageDAO.insertMessage(message);
    }

    public Message getMessageByConversationId(String conversationId){
        return messageDAO.selectMessageByConversationId(conversationId);
    }

    public List<String> getConversationIds(int id){
        return messageDAO.selectConversationId(id);
    }

    public List<Message> getMessageByconversationId(String conversationId){
        return messageDAO.selectMessagesInConversationByIds(conversationId);
    }

    public List<Message> getConversationListByUserId(int userId){
        return messageDAO.selectConversationListByUserId(userId);
    }

    public int getConversationUnReadMessage(String conversationId, int communicatorId){
        return messageDAO.countConversationUnReadMessage(conversationId, communicatorId);
    }

    public int getUnreadMessages(int userId){
        return messageDAO.countUnreadMessages(userId);
    }

    public void updateReadStatusInConversation(int userId, String conversationId){
        messageDAO.updateReadStatusInConversationByIds(userId, conversationId);
    }

    public Set<Integer> getFans(int idolId){
        Set<Integer> fans= null;
        try{
            String key = Entity.getFansHashKey();
            String field = Entity.getMyIdolKey(idolId);
            if(!jedisUtil.hexists(key, field)){
                return null;
            }
            String value = jedisUtil.hget(key, field);
            Set<String> jsonFansSet = JSONObject.parseObject(value, new TypeReference<Set<String>>(){});

            if(jsonFansSet.isEmpty()){
                return null;
            }
            Set<Integer> fansSet = new HashSet<>();
            for(String id: jsonFansSet){
                fansSet.add(Integer.valueOf(id));
            }
            return fansSet;
        }catch (Exception e){
            logger.error("获取粉丝集合失败: " + e.getMessage());
        }
        return fans;
    }
}
