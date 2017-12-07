package com.ackerman.reddit.utils;

public class Entity {
    public static final int ENTITY_NEWS = 1;
    public static final int ENTITY_REGISTER = 2;
    public static final int ENTITY_SUBSCRIPTION_PUSH = 3;
    public static final int ENTITY_COMMENT = 4;
    public static final int ENTITY_COLLECT = 5;
    public static final int ENTITY_MESSAGE = 6;
    public static final int ENTITY_REPORT = 7;

    public static final String SPLIT = "-";
    public static final String ATTITUDE_LIKE = "LIKE";
    public static final String ATTITUDE_DISLIKE = "DISLIKE";
    public static final String REPORT_NEWS = "REPORT_NEWS";

    public static String getEventQueueKey(){
        return  "EVENT";
    }

    public static String getScoreHashKey(){
        return "NEWS_SCORE";
    }

    public static String getFansHashKey(){
        return "FANS_SET";
    }

    public static String getMyIdolKey(int userId){
        return "MY_IDOL"+ String.valueOf(userId);
    }

    public static String getMyFansKey(int userId){
        return "MY_FANS" + String.valueOf(userId);
    }

    public static String getMyCollectKey(int userId){
        return "COLLECTIONS" + String.valueOf(userId);
    }

    public static String getReportNewsKey(int newsId){
        return "REPORT_NEWS" + String.valueOf(newsId);
    }

    public static String getAttitudeKey(String biz, int entityType, int entityId){
        return biz + SPLIT + String.valueOf(entityType) + SPLIT + String.valueOf(entityId);
    }
}