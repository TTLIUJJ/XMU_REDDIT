package com.ackerman.reddit.async;

import java.util.Date;
import java.util.Map;

public class EventModel {
    private EventType eventType;
    private int entityType;
    private int producerId;
    private Date eventDate;
    private Map<String, Object> extraInfo;

    public EventType getEventType() {
        return eventType;
    }

    public EventModel setEventType(EventType eventType) {
        this.eventType = eventType;
        return this;
    }

    public int getEntityType() {
        return entityType;
    }

    public EventModel setEntityType(int entityType) {
        this.entityType = entityType;
        return this;
    }

    public int getProducerId() {
        return producerId;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public EventModel setEventDate(Date eventDate) {
        this.eventDate = eventDate;
        return this;
    }

    public EventModel setProducerId(int producerId) {
        this.producerId = producerId;
        return this;
    }

    public Map<String, Object> getExtraInfo() {
        return extraInfo;
    }

    public EventModel setExtraInfo(Map<String, Object> extraInfo) {
        this.extraInfo = extraInfo;
        return this;
    }
}
