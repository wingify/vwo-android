package com.vwo.mobile.models;

import org.json.JSONException;
import org.json.JSONObject;

public interface IEvent {
    JSONObject toJson() throws JSONException;
}
