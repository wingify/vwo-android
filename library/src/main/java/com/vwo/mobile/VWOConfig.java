package com.vwo.mobile;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.vwo.mobile.listeners.ActivityLifecycleListener;
import com.vwo.mobile.utils.VWOLog;
import com.vwo.mobile.utils.VWOUtils;

import java.security.InvalidKeyException;
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
    private ActivityLifecycleListener activityLifecycleListener;

    // Timeout in case data is fetched synchronously
    private long timeout;

    // Is the VWO api key
    private String apiKey;

    private VWOConfig(Map<String, String> customSegmentationMapping, @Nullable String apiKey) {
        this.customSegmentationMapping = customSegmentationMapping;
        if (apiKey != null) {
            setApiKey(apiKey);
        }
    }

    private VWOConfig(Map<String, String> customSegmentationMapping, @Nullable String apiKey,
                      @NonNull ActivityLifecycleListener listener) {
        this.customSegmentationMapping = customSegmentationMapping;
        if (apiKey != null) {
            setApiKey(apiKey);
        }
        this.activityLifecycleListener = listener;
    }

    public Map<String, String> getCustomSegmentationMapping() {
        return customSegmentationMapping;
    }

    void setCustomSegmentationMapping(Map<String, String> customSegmentationMapping) {
        this.customSegmentationMapping = customSegmentationMapping;
    }

    void setApiKey(String apiKey) {
        if(!VWOUtils.isValidVwoAppKey(apiKey)) {
            VWOLog.e(VWOLog.CONFIG_LOGS, new InvalidKeyException("Invalid api key"), false, false);
            return;
        }
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

    public ActivityLifecycleListener getActivityLifecycleListener() {
        return this.activityLifecycleListener;
    }

    public long getTimeout() {
        return this.timeout;
    }

    void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public String getAccountId() {
        return mAccountId;
    }

    public void setActivityLifecycleListener(@NonNull ActivityLifecycleListener listener) {
        this.activityLifecycleListener = listener;
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
        private ActivityLifecycleListener lifecycleListener;

        public VWOConfig build() {
            if(lifecycleListener != null) {
                return new VWOConfig(customSegmentationMapping, apiKey, lifecycleListener);
            }
            return new VWOConfig(customSegmentationMapping, apiKey);
        }

        Builder apiKey(@NonNull String apiKey) {
            if (TextUtils.isEmpty(apiKey)) {
                throw new NullPointerException("Api key cannot be null");
            }
            this.apiKey = apiKey;
            return this;
        }

        /**
         * @param customSegmentationMapping is the key value pair mapping for custom segments based on
         *                                  which it is decided that given user will be a part of
         *                                  campaign or not.
         *
         * @return the current {@link Builder} object.
         */
        public Builder setCustomSegmentationMapping(@NonNull Map<String, String> customSegmentationMapping) {
            if (customSegmentationMapping == null) {
                throw new IllegalArgumentException("Mapping cannot be null");
            }
            this.customSegmentationMapping = customSegmentationMapping;
            return this;
        }

        public Builder setLifecycleListener(@NonNull ActivityLifecycleListener listener) {
            if (listener == null) {
                throw new IllegalArgumentException("Listener cannot be null");
            }
            this.lifecycleListener = listener;
            return this;
        }
    }
}
