package com.vwo.mobile.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.CallSuper;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Patterns;

import com.vwo.mobile.utils.VWOLog;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Created by aman on 16/09/17.
 */

@Keep
public abstract class Entry implements Parcelable {
    public static final String TYPE_GOAL = "goal";
    public static final String TYPE_CAMPAIGN = "campaign";

    private String url;
    private int retryCount;

    public Entry(String url) {
        if(!Pattern.compile(Patterns.WEB_URL.pattern()).matcher(url).matches()) {
            throw new IllegalArgumentException("Invalid url " + url);
        }
        this.url = url;
        retryCount = 0;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void incrementRetryCount() {
        ++retryCount;
    }

    /**
     * A unique key for the given entry.
     * @return
     */
    public abstract String getKey();

    /**
     * This class name is used to generate correct {@link Parcel} of the inherited class.
     *
     * @return the class name by {@link Class#getName()}
     */
    @NonNull
    public abstract String getClassName();

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Entry) {
            String entryUrl = ((Entry) obj).getUrl();
            return !TextUtils.isEmpty(entryUrl) && entryUrl.equals(this.url);
        }
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return String.format(Locale.ENGLISH, "Url: %s\nRetryCount: %d\n", this.url, this.retryCount);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @CallSuper
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getClassName());
        dest.writeString(this.url);
        dest.writeInt(this.retryCount);
    }

    protected Entry(Parcel in) {
        VWOLog.v(VWOLog.QUEUE, "Unmarshalling class " + in.readString());

        this.url = in.readString();
        this.retryCount = in.readInt();
    }

    public static final Creator<Entry> CREATOR = new Creator<Entry>() {
        @Override
        public Entry createFromParcel(Parcel source) {
            String className = source.readString();
            source.setDataPosition(0);
            try {
                Constructor constructor = Class.forName(className).getConstructor(Parcel.class);
                return (Entry) constructor.newInstance(source);
            } catch (ClassNotFoundException exception) {
                VWOLog.e(VWOLog.QUEUE, exception, true, true);
            } catch (NoSuchMethodException exception) {
                VWOLog.e(VWOLog.QUEUE, exception, true, true);
            } catch (IllegalAccessException exception) {
                VWOLog.e(VWOLog.QUEUE, exception, true, true);
            } catch (InstantiationException exception) {
                VWOLog.e(VWOLog.QUEUE, exception, true, true);
            } catch (InvocationTargetException exception) {
                VWOLog.e(VWOLog.QUEUE, exception, true, true);
            }

            return null;
        }

        @Override
        public Entry[] newArray(int size) {
            return new Entry[size];
        }
    };
}
