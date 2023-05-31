package com.vwo.mobile.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Metric implements IEvent {
    private final String key;
    private final JSONArray value;

    public Metric(String key, JSONArray value) {
        this.key = key;
        this.value = value;
    }

    public JSONObject toJson() throws JSONException {
        JSONObject object = new JSONObject();
        object.put(key, value);
        return object;
    }
}
