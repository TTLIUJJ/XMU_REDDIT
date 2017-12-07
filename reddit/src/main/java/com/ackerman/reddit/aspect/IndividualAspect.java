package com.ackerman.reddit.aspect;

import com.ackerman.reddit.model.User;
import com.ackerman.reddit.utils.Entity;
import com.ackerman.reddit.utils.HostHolder;
import com.ackerman.reddit.utils.JedisUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.support.BindingAwareModelMap;


@Aspect
@Component
public class IndividualAspect {
    private static Logger logger = LoggerFactory.getLogger(IndividualAspect.class);

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private JedisUtil jedisUtil;

    @Before("execution(* com.ackerman.reddit.controller.IndividualController.*(..))")
    public void before(JoinPoint joinPoint){
//        logger.info("before");
//        for(Object obj: joinPoint.getArgs()){
//            logger.info(obj.toString());
//        }
//        StringBuilder sb = new StringBuilder();
//        for(Object arg: joinPoint.getArgs()){
//            sb.append("arg: " + arg.toString()+ "\n");
//        }
//        sb.append((new Date()).toString());
//
//        logger.info(sb.toString());
    }

    @After("execution(* com.ackerman.reddit.controller.IndividualController.*(..))")
    public void after(JoinPoint joinPoint){
//        for(Object obj: joinPoint.getArgs()){
//            logger.info(joinPoint.getSignature().getName());
//            logger.info(obj.toString());
//
//        }

        try {
            BindingAwareModelMap map = null;
            for(Object obj: joinPoint.getArgs()){
                if(obj instanceof BindingAwareModelMap){
                    map = (BindingAwareModelMap) obj;
                    break;
                }
            }
            map.put("navActive", 5);
            User otherUser = (User)map.get("otherUser");
            if(otherUser != null){
                User user = hostHolder.getUser();
                String myFansKey = Entity.getMyFansKey(otherUser.getId());
                String myIdolKey = Entity.getMyIdolKey(user.getId());

                //设置是否已关注
                //isFans == 1 前端显示为已关注
                if(jedisUtil.sismember(myFansKey, String.valueOf(user.getId())) && jedisUtil.sismember(myIdolKey, String.valueOf(otherUser.getId()))){
                    map.put("isFans", 1);
                }
                else{
                    map.put("isFans", -1);
                }
            }
        }catch (Exception e){
            e.getStackTrace();
        }

    }
}
