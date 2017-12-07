package com.ackerman.reddit.utils;

import com.ackerman.reddit.model.ScoreModel;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.*;

public class RedditUtil {
    private static final Logger logger = LoggerFactory.getLogger("RedditUtil");

    private static long oneHourMilliSeconds = 3600000;
    private static long oneDayMilliSeconds = 86400000;
    private static long oneWeekMilliSeconds = 604800000;

    public static String getJSONString(int code){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", code);
        return jsonObject.toJSONString();
    }

    public static String getJSONString(int code, String msg){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", code);
        jsonObject.put("msg", msg);
        return jsonObject.toJSONString();
    }

    public static String getJSONString(int code, Map<String, Object> maps){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", code);
        for(Map.Entry<String, Object> entry: maps.entrySet()){
            jsonObject.put(entry.getKey(), entry.getValue());
        }
        return jsonObject.toJSONString();
    }

    public static <T> T getObject(String key, Class<T> c){
        if(key != null){
            return JSONObject.parseObject(key, c);
        }
        return null;
    }

    public static String MD5(String key){
        char []hexDigits = {
               '0', '1', '2', '3','4', '5', '6','7', '8', '9','A', 'B', 'C', 'D', 'E', 'F'
        };

        try{
            byte []bitInput = key.getBytes();
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(bitInput);
            byte []md = messageDigest.digest();
            char []chars = new char[md.length * 2];

            int k = 0;
            for(int i = 0; i < md.length; ++i){
                byte b = md[i];
                chars[k++] = hexDigits[b >> 4 & 0xF];
                chars[k++] = hexDigits[b & 0xF];
            }
            return new String(chars);

        }catch (Exception e){
            logger.info("MD5生成失败 " + e.getMessage());
            return null;
        }
    }

    public static boolean calculateScore(ScoreModel scoreModel){
        /**
         * scoreModel = p / (h+2) * (h+2)
         * p: likeCount
         * h: alive hours
         */
        //进入popular列表的新闻，只限一周内
        Date currentDate = new Date();
        if(currentDate.after(scoreModel.getExpiredDate())){
            logger.info("过期新闻");
            return false;
        }

        long aliveMilliSeconds = currentDate.getTime() + oneWeekMilliSeconds - scoreModel.getExpiredDate().getTime();
        int h = (int)(aliveMilliSeconds / 3600 / 1000);
        int p = scoreModel.getLikeCount();
        int t = (h+2) * (h+2);
        double score = ((double) p) / t;
        scoreModel.setScore(score);

        return true;
    }

    public static Map<String, Integer> findMinMax(Set<String> keySet){
        Map<String, Integer> map = new HashMap<>();
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;

        for(String i: keySet){
            int id = Integer.valueOf(i);
            if(id > max){
                max = id;
            }
            if(id < min){
                min = id;
            }
        }
        map.put("min", min);
        map.put("max", max);

        return map;
    }

    //小于1小时:  于xx分钟前发送
    //小于24小时: 于今天/昨天 HH:mm 发送
    //       ：  于yyyy-MM-dd HH:mm投递
    public static String parseShowTime(Date date){
        Date cur = new Date();
        long aliveTime = (cur.getTime() - date.getTime());
        String ret;

        if(aliveTime < oneHourMilliSeconds){
            ret = String.valueOf(aliveTime/1000/60);
            if(ret.equals("0")){
                ret = "刚刚";
            }
            else{
                ret += "分钟之前";
            }
        }
        else if(aliveTime < oneDayMilliSeconds){
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
            if(sdf2.format(date).equals(sdf2.format(cur))) {
                ret = "今天" + sdf.format(date);
            }
            else{
                ret = "昨天" + sdf.format(date);
            }
        }
        else if(aliveTime < oneDayMilliSeconds * 2){
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            ret = "昨天" + sdf.format(date);
        }
        else{
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            ret = sdf.format(date);
        }

        return ret;
    }

    public static String setConversationId(int id1, int id2){
        return id1 < id2 ? String.format("%d_%d", id1, id2) : String.format("%d_%d", id2, id1);
    }
}
