package com.vwo.mobile.models;

import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.vwo.mobile.logging.LogUtils;
import com.vwo.mobile.utils.Parceler;
import com.vwo.mobile.utils.VWOUtils;

import org.json.JSONException;
import org.json.JSONObject;

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
            jsonObject.put(EXTRAS, LogUtils.getJsonFromStringMap(builder.extras));
        }
        if (builder.deviceInfoExtras != null && builder.deviceInfoExtras.size() != 0) {
            jsonObject.put(DEVICE_INFO_EXTRAS, LogUtils.getJsonFromStringMap(builder.deviceInfoExtras));
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

    /**
     * This class name is used to generate correct {@link Parcel} of the inherited class.
     *
     * @return the class name by {@link Class#getName()}
     */
    @NonNull
    @Override
    public String getClassName() {
        return VWOError.class.getName();
    }

    public static class Builder implements android.os.Parcelable {
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

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.stackTrace);
            dest.writeString(this.versionName);
            dest.writeInt(this.versionCode);
            dest.writeString(this.message);
            dest.writeString(this.url);
            dest.writeLong(this.timestamp);
            dest.writeString(this.androidVersion);
            Parceler.writeStringMapToParcel(extras, dest);
            Parceler.writeStringMapToParcel(deviceInfoExtras, dest);
            dest.writeString(this.id);
        }

        protected Builder(Parcel in) {
            this.stackTrace = in.readString();
            this.versionName = in.readString();
            this.versionCode = in.readInt();
            this.message = in.readString();
            this.url = in.readString();
            this.timestamp = in.readLong();
            this.androidVersion = in.readString();
            this.extras = Parceler.readStringMapFromParcel(in);
            this.deviceInfoExtras = Parceler.readStringMapFromParcel(in);
            this.id = in.readString();
        }

        public static final Creator<Builder> CREATOR = new Creator<Builder>() {
            @Override
            public Builder createFromParcel(Parcel source) {
                return new Builder(source);
            }

            @Override
            public Builder[] newArray(int size) {
                return new Builder[size];
            }
        };
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(this.builder, flags);
    }

    public VWOError(Parcel in) {
        super(in);
        this.builder = in.readParcelable(Builder.class.getClassLoader());
    }

    public static final Creator<VWOError> CREATOR = new Creator<VWOError>() {
        @Override
        public VWOError createFromParcel(Parcel source) {
            return new VWOError(source);
        }

        @Override
        public VWOError[] newArray(int size) {
            return new VWOError[size];
        }
    };
}
