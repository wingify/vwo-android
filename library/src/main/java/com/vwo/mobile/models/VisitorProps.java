package com.vwo.mobile.models;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class VisitorProps implements IEvent {
    private final String key;
    private final String value;
    private HashMap<String, Object> dimensions;

    public VisitorProps(String key, String value) {
        this.key = key;
        this.value = value;
        this.dimensions = null;
    }

    public VisitorProps(@NonNull HashMap<String, Object> dimensions) {
        this.key = null;
        this.value = null;
        this.dimensions = dimensions;
    }

    @Override
    public JSONObject toJson() throws JSONException {

        if (dimensions != null)
            return new JSONObject(dimensions);

        JSONObject jsonObject = new JSONObject();
        if (key != null && value != null)
            jsonObject.put(key, value);

        return jsonObject;
    }
}