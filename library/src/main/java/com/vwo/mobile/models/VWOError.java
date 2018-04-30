package com.vwo.mobile.models;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.vwo.mobile.logging.LogUtils;
import com.vwo.mobile.utils.VWOUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Map;
import java.util.UUID;

/**
 * Created by aman on Wed 17/01/18 15:35.
 */

public class VWOError extends Entry {
    private static final String ID = "id";
    private static final String EXTRAS = "extras";
    private static final String DEVICE_INFO_EXTRAS = "device_info_extras";
    private static final String STACKTRACE = "stacktrace";
    private static final String VERSION = "version";
    private static final String VERSION_CODE = "version_code";
    private static final String MESSAGE = "message";
    private static final String TIMESTAMP = "timestamp";
    private static final String ANDROID_VERSION = "android_version";

    public static final String MANUFACTURER = "manufacturer";
    public static final String BRAND = "brand";
    public static final String MODEL = "model";

    public static final String PACKAGE_NAME = "package_name";
    public static final String VWO_SDK_VERSION = "sdk_version";
    public static final String VWO_SDK_VERSION_CODE = "sdk_version_code";

    public static final String EXTERNAL_STORAGE_SIZE = "total_external_storage";
    public static final String INTERNAL_STORAGE_SIZE = "total_internal_storage";
    public static final String AVAILABLE_EXTERNAL_STORAGE = "available_external_storage";
    public static final String AVAILABLE_INTERNAL_STORAGE = "available_internal_storage";

    public static final String AVAILABLE_MEMORY = "available_memory";
    public static final String TOTAL_MEMORY = "total_memory";
    public static final String IS_MEMORY_LOW = "is_memory_low";


    private Builder builder;

    private VWOError(String url, Builder builder) {
        super(url);
        this.builder = builder;
    }

    public JSONObject getErrorAsJSON() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(ID, builder.id);
        jsonObject.put(STACKTRACE, builder.stackTrace);
        jsonObject.put(VERSION, builder.versionName);
        jsonObject.put(VERSION_CODE, builder.versionCode);
        jsonObject.put(MESSAGE, builder.message);
        jsonObject.put(TIMESTAMP, builder.timestamp);
        jsonObject.put(ANDROID_VERSION, builder.androidVersion);
        if (builder.extras != null && builder.extras.size() != 0) {
            jsonObject.put(EXTRAS, new JSONObject(builder.extras));
        }
        if (builder.deviceInfoExtras != null && builder.deviceInfoExtras.size() != 0) {
            jsonObject.put(DEVICE_INFO_EXTRAS, new JSONObject(builder.deviceInfoExtras));
        }
        return jsonObject;
    }

    /**
     * A unique key for the given entry.
     *
     * @return
     */
    @Override
    public String getKey() {
        return builder.id;
    }


    public static class Builder implements Serializable {
        private String stackTrace;
        private String versionName;
        private int versionCode;
        private String message;
        private String url;
        private long timestamp;
        private String androidVersion;
        private Map<String, String> extras;
        private String id;
        private Map<String, String> deviceInfoExtras;


        public Builder(@Nullable String url, long timeStamp) {
            this.url = url;
            this.timestamp = timeStamp;
            this.androidVersion = VWOUtils.androidVersion();
            this.id = UUID.randomUUID().toString();
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder exception(@NonNull Throwable throwable) {
            this.stackTrace = LogUtils.getStackTrace(throwable);
            return this;
        }

        public Builder version(@NonNull String versionName) {
            this.versionName = versionName;
            return this;
        }

        public Builder versionCode(int versionCode) {
            this.versionCode = versionCode;
            return this;
        }

        public Builder extras(Map<String, String> extras) {
            this.extras = extras;
            return this;
        }

        public Builder deviceInfoExtras(Map<String, String> extras) {
            this.deviceInfoExtras = extras;
            return this;
        }

        public VWOError build() {
            return new VWOError(url, this);
        }

    }
}
