package com.vwo.mobile.models;

import static okhttp3.internal.Util.UTF_8;

import com.vwo.mobile.utils.VWOUrlBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

public class NonData360DimensionPostData implements IEvent {
    public static final String KEY_U = "u";
    public static final String TAGS = VWOUrlBuilder.TAGS;
    private HashMap<String, Object> dimensions;

    public NonData360DimensionPostData(HashMap<String, Object> dimensions) {
        this.dimensions = dimensions;
    }

    @Override
    public JSONObject toJson() throws JSONException {
        if (dimensions == null)
            throw new IllegalArgumentException("Dimensions missing for non data360 push API call.");

        JSONObject attributes = new JSONObject(dimensions);
        JSONObject uJson = new JSONObject();
        uJson.put(KEY_U, attributes);
        JSONObject tagsJson = new JSONObject();
        try {
            String encoded = URLEncoder.encode(uJson.toString(), UTF_8.name());
            tagsJson.put(TAGS, encoded);
            return tagsJson;
        } catch (UnsupportedEncodingException exception) {
            return new JSONObject();
        }
    }
}
