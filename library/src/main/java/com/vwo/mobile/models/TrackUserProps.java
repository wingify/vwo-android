package com.vwo.mobile.models;

import static com.vwo.mobile.constants.AppConstants.*;

import org.json.JSONException;
import org.json.JSONObject;

public class TrackUserProps extends EventProps {
    private final String sdkName;
    private final String sdkVersion;
    private final long id;
    private final long variation;
    private final long isFirst = 1;

    public TrackUserProps(String sdkName, String sdkVersion, long id, long variation) {
        this.sdkName = sdkName;
        this.sdkVersion = sdkVersion;
        this.id = id;
        this.variation = variation;
    }

    // Getter Methods

    public String getSdkName() {
        return sdkName;
    }

    public String getSdkVersion() {
        return sdkVersion;
    }

    public float getId() {
        return id;
    }

    public float getVariation() {
        return variation;
    }

    public float getIsFirst() {
        return isFirst;
    }

    @Override
    public JSONObject toJson() throws JSONException {

        return new JSONObject()
                .put(KEY_SDK_NAME, sdkName)
                .put(KEY_SDK_VERSION, sdkVersion)
                .put(KEY_ID, id)
                .put(KEY_VARIATION, variation)
                .put(KEY_IS_First, isFirst);
    }
}