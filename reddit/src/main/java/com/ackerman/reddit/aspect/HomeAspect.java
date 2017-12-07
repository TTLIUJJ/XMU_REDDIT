package com.ackerman.reddit.aspect;

import com.ackerman.reddit.model.News;
import com.ackerman.reddit.model.User;
import com.ackerman.reddit.utils.Entity;
import com.ackerman.reddit.utils.HostHolder;
import com.ackerman.reddit.utils.JedisUtil;
import com.ackerman.reddit.utils.ViewObject;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.support.BindingAwareModelMap;

import java.util.List;

@Aspect
@Component
public class HomeAspect {
    private static Logger logger = LoggerFactory.getLogger(HomeAspect.class);

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private JedisUtil jedisUtil;

    @After("execution(* com.ackerman.reddit.controller.HomeController.*(..))")
    public void after(JoinPoint joinPoint){
        BindingAwareModelMap map = null;
        try{
            User user = hostHolder.getUser();
            if(user == null){
                return ;
            }
            for(Object object: joinPoint.getArgs()){
                if(object instanceof BindingAwareModelMap){
                    map = (BindingAwareModelMap) object;
                    break;
                }
            }
            if(map == null){
                return;
            }
            List<ViewObject> vos = (List<ViewObject>) map.get("vos");

            //收藏的key是一个用户对应多个新闻
            String collectionsKey = Entity.getMyCollectKey(user.getId());
            for(ViewObject vo : vos){
                News news = (News)vo.get("news");
                if(jedisUtil.sismember(collectionsKey, String.valueOf(news.getId()))){
                    vo.set("collected", 1);
                }

                //然而举报的key是一个新闻对应多个用户
                String reportedKey = Entity.getReportNewsKey(news.getId());
                if(jedisUtil.sismember(reportedKey, String.valueOf(user.getId()))){
                    vo.set("reported", 1);
                }
            }
        }catch (Exception e){
            e.getStackTrace();
        }
    }
}
