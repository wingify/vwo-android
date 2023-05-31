package com.vwo.mobile.models;

import static com.vwo.mobile.constants.AppConstants.KEY_$VISITOR;
import static com.vwo.mobile.constants.AppConstants.KEY_CUSTOM_EVENT;
import static com.vwo.mobile.constants.AppConstants.KEY_SDK_NAME;
import static com.vwo.mobile.constants.AppConstants.KEY_SDK_VERSION;

import org.json.JSONException;
import org.json.JSONObject;

public class VisitorSyncPushProps extends EventProps {
    private final String sdkName;
    private final String sdkVersion;
    private final Visitor visitor;
    private final boolean isCustomEvent;

    public VisitorSyncPushProps(String sdkName, String sdkVersion, boolean isCustomEvent, Visitor visitor) {
        this.sdkName = sdkName;
        this.visitor = visitor;
        this.sdkVersion = sdkVersion;
        this.isCustomEvent = isCustomEvent;
    }

    // Getter Methods

    public String getSdkName() {
        return sdkName;
    }

    public Visitor get$visitor() {
        return visitor;
    }

    public String getSdkVersion() {
        return sdkVersion;
    }

    public boolean isCustomEvent() {
        return isCustomEvent;
    }

    @Override
    public JSONObject toJson() throws JSONException {

        return new JSONObject()
                .put(KEY_SDK_NAME, sdkName)
                .put(KEY_$VISITOR, visitor.toJson())
                .put(KEY_SDK_VERSION, sdkVersion)
                .put(KEY_CUSTOM_EVENT, isCustomEvent);
    }
}