package com.ackerman.reddit.controller;

import com.ackerman.reddit.model.Message;
import com.ackerman.reddit.model.User;
import com.ackerman.reddit.service.MessageService;
import com.ackerman.reddit.service.UserService;
import com.ackerman.reddit.utils.HostHolder;
import com.ackerman.reddit.utils.RedditUtil;
import com.ackerman.reddit.utils.ViewObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Controller
public class MessageController {
    private static Logger logger = LoggerFactory.getLogger(MessageController.class);

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    @RequestMapping(path = {"/conversation/list"}, method = {RequestMethod.POST, RequestMethod.GET})
    public String conversationList(Model model){

        try {
            User user = hostHolder.getUser();
            if(user == null){
                return "redirect:/login";
            }
            List<ViewObject> conversationVOs = new ArrayList<>();

//            由于mysql的 GROUP BY 不能将最新的消息删选出来, 只好分两次查询
//            List<Message> conversationList = messageService.getConversationListByUserId(user.getId());

            //排序过的conversationId 按照createDate倒序
            List<String> conversationIds = messageService.getConversationIds(user.getId());
            List<Message> conversationList = new ArrayList<>();
            for(String conversationId : conversationIds){
                conversationList.add(messageService.getMessageByConversationId(conversationId));
            }
            Collections.sort(conversationList, new Comparator<Message>() {
                @Override
                public int compare(Message o1, Message o2) {
                    if(o1.getId() < o2.getId()){
                        return 1;
                    }
                    else{
                        return -1;
                    }
                }
            });

            for(Message message: conversationList){
                ViewObject vo = new ViewObject();
                int communicatorId = user.getId() == message.getFromId() ? message.getToId() : message.getFromId();
                int unReadMessages = messageService.getConversationUnReadMessage(message.getConversationId(), communicatorId);

                vo.set("conversation", message);
                vo.set("communicator", userService.getUser(communicatorId));
                vo.set("unReadMessages", unReadMessages);
                vo.set("showTime", RedditUtil.parseShowTime(message.getCreateDate()));
                conversationVOs.add(vo);
            }
            model.addAttribute("conversationVOs", conversationVOs);
        }catch (Exception e){
            logger.info("获取消息列表失败"  + e.getMessage());

        }
        model.addAttribute("navActive", 4);
        return "conversationList";
    }

    @RequestMapping(path={"/conversation/messageList"}, method = {RequestMethod.POST, RequestMethod.GET})
    public String messageList(@RequestParam("communicatorId") int communicatorId,
                              @RequestParam(value = "unReadMessages", defaultValue = "0") int unReadMessages,
                              Model model){

        try{
            User user = hostHolder.getUser();
            if(user == null){
                return "redirect:/login";
            }
            String conversationId = String.valueOf(Math.min(user.getId(), communicatorId)+"_"+Math.max(user.getId(), communicatorId));
            List<Message> messageList = messageService.getMessageByconversationId(conversationId);
            List<ViewObject> messageVOs = new ArrayList<>();

            for(Message message: messageList){
               ViewObject vo  = new ViewObject();
               vo.set("message", message);
               vo.set("sender", userService.getUser(message.getFromId()));
               vo.set("showTime", RedditUtil.parseShowTime(message.getCreateDate()));
               messageVOs.add(vo);
            }
            model.addAttribute("messageVOs", messageVOs);
            model.addAttribute("unReadMessages", unReadMessages);

            messageService.updateReadStatusInConversation(user.getId(), conversationId);
        }catch (Exception e){
            logger.info("获取会话详细页面失败" + e.getMessage());
        }

        model.addAttribute("navActive", 4);
        return "messageList";
    }


}
