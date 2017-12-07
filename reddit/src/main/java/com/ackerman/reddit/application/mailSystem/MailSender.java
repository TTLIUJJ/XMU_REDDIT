package com.ackerman.reddit.application.mailSystem;

import com.alibaba.fastjson.JSONObject;
import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.ui.velocity.VelocityEngineUtils;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;


@Component
public class MailSender implements InitializingBean {

    private static Logger logger = LoggerFactory.getLogger(MailSender.class);

    private static final ArrayBlockingQueue<Map<String, Object>> queue =  new ArrayBlockingQueue<>(100);

    private JavaMailSenderImpl javaMailSender;

    @Autowired
    private VelocityEngine velocityEngine;

    public void sendMail(String to,
                         String subject,
                         String template,
                         Map<String, Object> info){
        try {
            Map<String, Object> mail = new HashMap<>();

            mail.put("to", to);
            mail.put("subject", subject);
            mail.put("template", template);
            mail.put("info", info);

            queue.put(mail);

            logger.info("after queue.put()");

        }catch (Exception e){
            logger.info("sendMail: " + e.getMessage());
        }
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setUsername("jsjskkk222@163.com");
        javaMailSender.setPassword("java123456");
        javaMailSender.setHost("smtp.163.com");
        javaMailSender.setPort(465);
        javaMailSender.setProtocol("smtps");
        javaMailSender.setDefaultEncoding("utf8");

        Properties properties = new Properties();
        properties.put("mail.smtp.ssl.enable", true);

        javaMailSender.setJavaMailProperties(properties);


        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {

                        logger.info("before queue.take()");
                        Map<String, Object> mail = queue.take();
                        logger.info("after queue.take()");

                        logger.info(JSONObject.toJSONString(mail));

                        String to = (String) mail.get("to");
                        String subject = (String) mail.get("subject");
                        String template = (String) mail.get("template");
                        Map<String, Object> info = (Map<String, Object>) mail.get("info");

                        String nickName = MimeUtility.encodeText("XMU_Rddit管理员1号");
                        InternetAddress from = new InternetAddress(nickName + "<jsjskkk222@163.com>");
                        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
                        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);

                        String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, template, "UTF-8", info);
                        mimeMessageHelper.setTo(to);
                        mimeMessageHelper.setFrom(from);
                        mimeMessageHelper.setSubject(subject);
                        mimeMessageHelper.setText(text, true);
//            logger.info(text);
                        javaMailSender.send(mimeMessage);
                    } catch (Exception e) {
                        logger.info("发送邮件异常: " + e.getMessage());
                        e.getStackTrace();
                    }
                    logger.info("a round try");
                }
            }
        });

        thread.start();
        logger.info("after thread.start()");
    }

}
