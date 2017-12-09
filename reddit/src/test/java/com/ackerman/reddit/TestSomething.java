//package com.ackerman.reddit;
//
//import com.ackerman.reddit.model.*;
//import com.ackerman.reddit.service.*;
//import com.ackerman.reddit.utils.JedisUtil;
//import com.ackerman.reddit.utils.RedditUtil;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.SpringApplicationConfiguration;
//import org.springframework.test.context.jdbc.Sql;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//import org.springframework.util.Assert;
//import redis.clients.jedis.Jedis;
//
//import java.util.Date;
//import java.util.List;
//import java.util.Random;
//import java.util.UUID;
//
//
//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringApplicationConfiguration(classes = RedditApplication.class)
//public class TestSomething {
//
////
////    @Test
////    public void redis(){
////        Thread thread1 = new Thread(new Runnable() {
////            @Override
////            public void run() {
////                Jedis jedis = new Jedis();
////                while (true){
////                    System.out.println(jedis.brpop(0, "lz"));
//////                    System.out.println(jedis.lpush("ll", "shit"));
////                }
////            }
////        });
////        thread1.run();
////
////        Thread thread2 = new Thread(new Runnable() {
////            @Override
////            public void run() {
////                Jedis jedis = new Jedis();
////                while (true){
////                    System.out.println(jedis.lpush("lz", "xx"));
////                }
////            }
////        });
////        thread2.run();
////    }
//}
