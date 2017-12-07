package com.ackerman.reddit.model;

import java.util.Date;

public class ScoreModel {
    private double score;
    private int likeCount;
    private Date expiredDate;

    public ScoreModel(){}

    public ScoreModel(double score, int likeCount, Date expiredDate){
        this.score = score;
        this.likeCount = likeCount;
        this.expiredDate = expiredDate;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public Date getExpiredDate() {
        return expiredDate;
    }

    public void setExpiredDate(Date expiredDate) {
        this.expiredDate = expiredDate;
    }


}
