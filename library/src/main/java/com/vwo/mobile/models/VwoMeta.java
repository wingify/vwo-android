package com.vwo.mobile.models;

import static com.vwo.mobile.constants.AppConstants.KEY_METRIC;

import org.json.JSONException;
import org.json.JSONObject;

public class VwoMeta implements IEvent{
    Metric metric;
    String key;
    Double value;

    public VwoMeta(Metric metric, String key, Double value) {
        this.metric = metric;
        this.value = value;
        this.key = key;
    }

    // Getter Methods

    public Metric getMetric() {
        return metric;
    }

    public JSONObject toJson() throws JSONException {
        JSONObject object = new JSONObject();
        object.put(KEY_METRIC, metric.toJson());
        if (key != null && value != null)
            object.put(key, value);
        return object;
    }
}
