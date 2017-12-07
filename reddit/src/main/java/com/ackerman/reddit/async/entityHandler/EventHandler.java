package com.ackerman.reddit.async.entityHandler;

import com.ackerman.reddit.async.EventModel;
import com.ackerman.reddit.async.EventType;

public interface EventHandler {
    void processEvent(EventModel eventModel);
    EventType getEventType();
}
