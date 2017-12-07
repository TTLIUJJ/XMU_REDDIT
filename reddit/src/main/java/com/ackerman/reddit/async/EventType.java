package com.ackerman.reddit.async;

public enum EventType {
    REGISTER(0),
    LOGIN(1),
    COMMENT(2),
    NEWS(3),
    CONVERSATION(4),
    SUBSCRIPTION_PUSH(5),
    REPORTED_NEWS(6);

    private int value;

    EventType(int value){
        this.value = value;
    }

    public int getType(){
        return value;
    }
}
