package com.vwo.mobile;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.vwo.mobile.listeners.ActivityLifecycleListener;
import com.vwo.mobile.events.VWOStatusListener;
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
    private boolean optOut;

    // Timeout in case data is fetched synchronously
    private Long timeout;

    // Is the VWO api key
    private String apiKey;
    private boolean previewEnabled;
    private VWOStatusListener statusListener;
    private String userID;

    private VWOConfig(Builder builder) {
        this.customSegmentationMapping = builder.customSegmentationMapping;
        if (builder.apiKey != null) {
            setApiKey(builder.apiKey);
        }
        this.optOut = builder.optOut;
        this.activityLifecycleListener = builder.lifecycleListener;
        this.previewEnabled = builder.previewEnabled;
        this.statusListener = builder.statusListener;
        this.userID = builder.userID;
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

    @Nullable
    public String getUserID() {
        return this.userID;
    }

    @Nullable
    public Long getTimeout() {
        return this.timeout;
    }

    void setTimeout(@Nullable Long timeout) {
        this.timeout = timeout;
    }

    public String getAccountId() {
        return mAccountId;
    }

    public void setActivityLifecycleListener(@NonNull ActivityLifecycleListener listener) {
        this.activityLifecycleListener = listener;
    }

    /**
     * @return whether user is opted out of VWO SDK or not
     */
    boolean isOptOut() {
        return this.optOut;
    }

    boolean isPreviewEnabled() {
        return this.previewEnabled;
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

    void setOptOut(boolean optOut) {
        this.optOut = optOut;
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

    void setStatusListener(@NonNull VWOStatusListener statusListener) {
        this.statusListener = statusListener;
    }

    @Nullable
    public VWOStatusListener getStatusListener() {
        return statusListener;
    }

    public static class Builder {
        // This variable
        private Map<String, String> customSegmentationMapping;
        private boolean optOut;
        private String apiKey = null;
        private ActivityLifecycleListener lifecycleListener;
        private boolean previewEnabled = true;
        private VWOStatusListener statusListener;
        private String userID;

        /**
         * Generate the Configuration for the VWO SDK which can be passed to
         * {@link Initializer#config(VWOConfig)} during SDK's initialization.
         */
        public Builder(){}

        @NonNull
        public VWOConfig build() {
            return new VWOConfig(this);
        }

        /**
         * Function to set whether you want to use VWO or not. Set {@link Boolean#TRUE} to opt out
         * of VWO SDK else false. it defaults to {@link Boolean#FALSE}.
         *
         * @param optOut set {@link Boolean#TRUE} to opt out of the VWO SDK otherwise {@link Boolean#FALSE}.
         *               it defaults to {@link Boolean#FALSE}.
         * @return the {@link Builder} object
         */
        @NonNull
        public Builder setOptOut(boolean optOut) {
            this.optOut = optOut;
            return this;
        }

        /**
         * Function Set the unique ID for the user to serve same variations(subject to few conditions) across devices for users with same ID.
         *
         * Note: user id is case sensitive. And this id is not stored anywhere persistently.
         *
         * @param userID is the unique user id.
         * @return the {@link Builder} object {@userId}.
         */
        @NonNull
        public Builder userID(@NonNull String userID) {
            this.userID = VWOUtils.toMD5Hash(userID);
            return this;
        }

        /**
         * Set the api key in the builder
         *
         * @param apiKey is the VWO Api key
         * @return the {@link Builder} object
         */
        @NonNull
        Builder apiKey(@NonNull String apiKey) {
            if (TextUtils.isEmpty(apiKey)) {
                throw new NullPointerException("Api key cannot be null");
            }
            this.apiKey = apiKey;
            return this;
        }

        /**
         * Disable the preview mode.
         *
         * @return the {@link Builder} object
         */
        @NonNull
        public Builder disablePreview() {
            this.previewEnabled = false;
            return this;
        }

        /**
         * @param customSegmentationMapping is the key value pair mapping for custom segments based on
         *                                  which it is decided that given user will be a part of
         *                                  campaign or not.
         *
         * @return the current {@link Builder} object.
         */
        @NonNull
        public Builder setCustomVariables(@NonNull Map<String, String> customSegmentationMapping) {
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

        Builder setVWOStatusListener(@NonNull VWOStatusListener vwoStatusListener) {
            this.statusListener = vwoStatusListener;
            return this;
        }
    }
}