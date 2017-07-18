package com.vwo.mobile;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.vwo.mobile.analytics.VWOTracker;
import com.vwo.mobile.utils.VWOLog;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by aman on 22/06/17.
 */

public class VWOConfig {
    // This variable
    private Map<String, String> customSegmentationMapping;
    private String mAppKey;
    private String mAccountId;
    @Nullable
    private Object mTracker;

    // Should fetch data synchronously or asynchronously from server
    private boolean sync;

    // Is the VWO api key
    private String apiKey;

    private VWOConfig(Map<String, String> customSegmentationMapping, @Nullable String apiKey,
                      @NonNull Object tracker) {
        this.customSegmentationMapping = customSegmentationMapping;
        this.mTracker = tracker;
        if (apiKey != null) {
            setApiKey(apiKey);
        }
    }

    public Map<String, String> getCustomSegmentationMapping() {
        return customSegmentationMapping;
    }

    void setCustomSegmentationMapping(Map<String, String> customSegmentationMapping) {
        this.customSegmentationMapping = customSegmentationMapping;
    }

    void setApiKey(String apiKey) {
        this.apiKey = apiKey;
        this.mAccountId = apiKey.substring(apiKey.indexOf("-") + 1);
        this.mAppKey = apiKey.substring(0, apiKey.indexOf("-"));

    }

    public String getApiKey() {
        return this.apiKey;
    }

    public String getAppKey() {
        return mAppKey;
    }

    public boolean isSync() {
        return sync;
    }

    void setSync(boolean sync) {
        this.sync = sync;
    }

    public String getAccountId() {
        return mAccountId;
    }

    @Nullable
    public Object getTracker() {
        return this.mTracker;
    }

    public boolean isTrackerPresent() {
        return mTracker != null;
    }

    /**
     * @param customSegmentKeys is the keymap for custom segmentation variables
     */
    void setCustomSegmentKeys(Map<String, String> customSegmentKeys) {
        this.customSegmentationMapping = new HashMap<>(customSegmentKeys);
    }

    /**
     * Function to add custom key value pair to segment
     *
     * @param key   {@link String} is the key for custom segment
     * @param value {@link String} is the value of custom segment.
     */
    void addCustomSegment(String key, String value) {
        if(customSegmentationMapping == null) {
            customSegmentationMapping = new HashMap<>();
        }
        this.customSegmentationMapping.put(key, value);
    }

    void setTracker(Object tracker) {
        if(tracker != null) {
            VWOLog.w(VWOLog.CONFIG_LOGS, "Tracker already set", false);
        }
        this.mTracker = tracker;
    }

    /**
     * @param customSegments add multiple custom segment key value pairs
     */
    void addCustomSegments(Map<String, String> customSegments) {
        if(customSegmentationMapping == null) {
            customSegmentationMapping = new HashMap<>();
        }
        this.customSegmentationMapping.putAll(customSegments);
    }

    public String getValueForCustomSegment(String key) {
        if (this.customSegmentationMapping != null && customSegmentationMapping.size() > 0) {
            return this.customSegmentationMapping.get(key);
        }

        return null;
    }

    public static class Builder {
        // This variable
        private Map<String, String> customSegmentationMapping;
        private String apiKey = null;
        private Object mTracker;

        public VWOConfig build() {
            return new VWOConfig(customSegmentationMapping, apiKey, mTracker);
        }

        Builder apiKey(@NonNull String apiKey) {
            if (TextUtils.isEmpty(apiKey)) {
                throw new NullPointerException("Api key cannot be null");
            }
            this.apiKey = apiKey;
            return this;
        }

        public Builder setTracker(@NonNull Object tracker) {
            this.mTracker = tracker;
            return this;
        }

        /**
         * @param customSegmentationMapping is the key value pair mapping for custom segments
         * @return
         */
        public Builder setCustomSegmentationMapping(@NonNull Map<String, String> customSegmentationMapping) {
            if (customSegmentationMapping == null) {
                throw new IllegalArgumentException("Mapping cannot be null");
            }
            this.customSegmentationMapping = customSegmentationMapping;
            return this;
        }
    }
}
