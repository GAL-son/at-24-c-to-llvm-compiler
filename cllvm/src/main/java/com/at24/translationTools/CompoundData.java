package com.at24.translationTools;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CompoundData {
    Map<String, Object> data = new HashMap<>();

    public void put(String key, Object value) {
        data.put(key, value);
    }

    public Object get(String key) {
        return data.getOrDefault(key, null);
    }

    public Set<String> getKeys() {
        return data.keySet();
    }

    public void addData(CompoundData data) {
        if(data == null) {
            return;
        }
        for (String keyString : data.getKeys()) {
            this.data.put(keyString, data.get(keyString));
        }
    }
}
