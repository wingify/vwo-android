package com.vwo.mobile.models;

import org.json.JSONException;
import org.json.JSONObject;

public class VisitorProps implements IEvent {
    private final String key;
    private final String value;

    public VisitorProps(String key, String value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public JSONObject toJson() throws JSONException {
        JSONObject jsonObject = new JSONObject();

        if (key != null && value != null)
            jsonObject.put(key, value);

        return jsonObject;
    }
}