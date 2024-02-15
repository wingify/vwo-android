package com.vwo.mobile.models;

import com.vwo.mobile.utils.VWOUrlBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

public class NonData360DimensionPostData implements IEvent {

    public static final String CHARSET_UTF_8 = "UTF-8";

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
            String encoded = URLEncoder.encode(uJson.toString(), CHARSET_UTF_8);
            tagsJson.put(TAGS, encoded);
            return tagsJson;
        } catch (UnsupportedEncodingException exception) {
            return new JSONObject();
        } catch (Exception ex) {
            // for unknown surprises that may occur
            return new JSONObject();
        }
    }
}
