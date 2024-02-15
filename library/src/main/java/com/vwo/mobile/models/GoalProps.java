package com.vwo.mobile.models;

import static com.vwo.mobile.constants.AppConstants.KEY_CUSTOM_EVENT;
import static com.vwo.mobile.constants.AppConstants.KEY_META;
import static com.vwo.mobile.constants.AppConstants.KEY_SDK_NAME;
import static com.vwo.mobile.constants.AppConstants.KEY_SDK_VERSION;

import org.json.JSONException;
import org.json.JSONObject;

public class GoalProps extends EventProps {
    private final String sdkName;
    private final String sdkVersion;
    private final boolean isCustomEvent;
    private final VwoMeta meta;

    public GoalProps(String sdkName, String sdkVersion, boolean isCustomEvent, VwoMeta meta) {
        this.sdkName = sdkName;
        this.sdkVersion = sdkVersion;
        this.isCustomEvent = isCustomEvent;
        this.meta = meta;
    }

// Getter Methods

    public String getSdkName() {
        return sdkName;
    }

    public String getSdkVersion() {
        return sdkVersion;
    }

    public boolean getIsCustomEvent() {
        return isCustomEvent;
    }

    public VwoMeta getVwoMeta() {
        return meta;
    }

    @Override
    public JSONObject toJson() throws JSONException {

        return new JSONObject()
                .put(KEY_SDK_NAME, sdkName)
                .put(KEY_SDK_VERSION, sdkVersion)
                .put(KEY_CUSTOM_EVENT, isCustomEvent)
                .put(KEY_META, meta.toJson());
    }
}
