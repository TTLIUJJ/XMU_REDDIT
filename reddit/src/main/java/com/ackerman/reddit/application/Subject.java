package com.ackerman.reddit.application;

import com.ackerman.reddit.application.Observer;

public interface Subject {
    void registerObserver(Observer observer);
    void removeObserver(Observer observer);
    void notifyObservers(Object object);
}
