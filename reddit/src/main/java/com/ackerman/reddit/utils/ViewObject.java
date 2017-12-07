package com.ackerman.reddit.utils;

import java.util.HashMap;
import java.util.Map;

public class ViewObject {
    private Map<String, Object> maps;

    public ViewObject(){
        maps = new HashMap<>();
    }

    public void set(String key, Object value){
        maps.put(key, value);
    }

    public Object get(String key){
        return maps.get(key);
    }
}
