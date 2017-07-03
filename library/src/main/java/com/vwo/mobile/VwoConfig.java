package com.vwo.mobile;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.Map;

/**
 * Created by aman on 22/06/17.
 */

public class VwoConfig {
    // This variable
    private Map<String, String> customSegmentationMapping;
    private String mAppKey;
    private String mAccountId;

    // Should fetch data synchronously or asynchronously from server
    private boolean sync;

    // Is the VWO api key
    private String apiKey;

    private VwoConfig(Map<String, String> customSegmentationMapping, @Nullable String apiKey) {
        this.customSegmentationMapping = customSegmentationMapping;
        if(apiKey != null) {
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

    /**
     *
     * @param customSegmentKeys is the keymap for custom segmentation variables
     */
    void setCustomSegmentKeys(Map<String, String> customSegmentKeys) {
        this.customSegmentationMapping = customSegmentKeys;
    }

    /**
     * Function to add custom key value pair to segment
     *
     * @param key {@link String} is the key for custom segment
     * @param value {@link String} is the value of custom segment.
     */
    void addCustomSegment(String key, String value) {
        this.customSegmentationMapping.put(key, value);
    }

    /**
     * @param customSegments add multiple custom segment key value pairs
     */
    void addCustomSegments(Map<String, String> customSegments) {
        this.customSegmentationMapping.putAll(customSegments);
    }

    public String getValueForCustomSegment(String key) {
        if(this.customSegmentationMapping != null && customSegmentationMapping.size() > 0) {
            return this.customSegmentationMapping.get(key);
        }

        return null;
    }

    public static class Builder {
        // This variable
        private Map<String, String> customSegmentationMapping;
        private String apiKey = null;

        public VwoConfig build() {
            return new VwoConfig(customSegmentationMapping, apiKey);
        }

        Builder apiKey(String apiKey) {
            if (TextUtils.isEmpty(apiKey)) {
                throw new NullPointerException("Api key cannot be null");
            }
            this.apiKey = apiKey;
            return this;
        }

        /**
         *
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
