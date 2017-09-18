package com.vwo.mobile.models;

import android.text.TextUtils;

import com.vwo.mobile.utils.VWOLog;

import java.io.Serializable;
import java.util.Locale;

/**
 * Created by aman on 16/09/17.
 */

public abstract class Entry implements Serializable {
    public static final String TYPE_GOAL = "goal";
    public static final String TYPE_CAMPAIGN = "campaign";

    private String url;
    private int retryCount;
    private Exception exception;

    public Entry(String url) {
        this.url = url;
        retryCount = 0;
        exception = null;
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

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public abstract String getKey();

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
}
