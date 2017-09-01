package com.vwo.mobile.models;

import android.support.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by abhishek on 17/09/15 at 12:03 PM.
 */
public class Variation {

    public static final int EMPTY_VARIATION = -99;
    public static final String JSON_CONTENT = "changes";
    public static final String ID = "id";
    public static final String WEIGHT = "weight";
    public static final String NAME = "name";

    private int mId;
    private String mName;
    private JSONObject mServeObject;
    private double mWeight;
    private Map<String, Object> mKeysObjects;

    public Variation() {
        this.mId = EMPTY_VARIATION;
        this.mName = "";
        mServeObject = new JSONObject();
        mWeight = 0.0;
    }

    public Variation(JSONObject variation) {
        try {
            this.mId = variation.getInt(ID);
            this.mName = variation.getString(NAME);
            this.mWeight = variation.getDouble(WEIGHT);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // JSON CONTENT can also be null. Handling this case separately.
        try {
            this.mServeObject = variation.getJSONObject(JSON_CONTENT);
        } catch (JSONException e) {
            this.mServeObject = new JSONObject();
        }
        generateKeyPairs();
    }

    public int getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public JSONObject getServeObject() {
        return mServeObject;
    }

    public double getWeight() {
        return mWeight;
    }

    private void generateKeyPairs() {
        mKeysObjects = new HashMap<>();
        Iterator<?> keys = mServeObject.keys();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            try {
                if(!mServeObject.isNull(key)) {
                    mKeysObjects.put(key, mServeObject.get(key));
                } else {
                    mKeysObjects.put(key, null);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Nullable
    public Object getKey(String key) {
        if (mKeysObjects.containsKey(key)) {
            if (mKeysObjects.get(key) != null) {
                return mKeysObjects.get(key);
            }
        }
        return null;
    }

    public boolean hasKey(String key) {
        return mKeysObjects != null && mKeysObjects.containsKey(key);
    }

    public JSONObject getVariationAsJsonObject() throws JSONException {

        if (mId == EMPTY_VARIATION) {
            return new JSONObject();
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(ID, mId);
        jsonObject.put(WEIGHT, mWeight);
        jsonObject.put(NAME, mName);
        jsonObject.put(JSON_CONTENT, mServeObject);
        return jsonObject;
    }

}
