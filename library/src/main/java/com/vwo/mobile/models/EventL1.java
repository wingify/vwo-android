package com.vwo.mobile.models;

import static com.vwo.mobile.constants.AppConstants.KEY_DATA;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Level 1 event model.
 *
 * @author swapnilchaudhari
 */
public class EventL1 implements IEvent{
    private final EventL2 data;

    public EventL1(EventL2 data) {
        this.data = data;
    }


    public EventL2 getData() {
        return data;
    }

    public JSONObject toJson() throws JSONException {
        return new JSONObject()
                .put(KEY_DATA,data.toJson());
    }
}