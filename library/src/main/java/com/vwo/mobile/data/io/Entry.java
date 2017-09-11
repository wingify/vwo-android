package com.vwo.mobile.data.io;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by aman on 11/09/17.
 */

public class Entry implements Serializable {
    private String url;
    private String id;

    private Entry(@NonNull String url, @NonNull String id) {
        this.url = url;
        this.id = id;
    }

    private Entry(@NonNull String url) {
        this(url, UUID.randomUUID().toString());
    }

    /**
     * @return a unique uuid for the entry to uniquely identify the entry
     */
    @NonNull
    public String getId() {
        return this.id;
    }

    @NonNull
    public String getUrl() {
        return this.url;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public void setUrl(@NonNull String url) {
        this.url = url;
    }
}
