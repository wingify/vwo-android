package com.vwo.mobile.network;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by aman on 05/09/17.
 */

public class VWORequest {
    @NonNull
    private String url;
    @HttpMethod
    @NonNull
    private String method;
    @Nullable
    private Map<String, String> params;
    @Nullable
    private String body;
    private Map<String, String> headers;

    public static final String GET = "GET";
    public static final String PUT = "PUT";
    public static final String POST = "POST";
    public static final String DELETE = "DELETE";

    private VWORequest(Builder builder) {
        this.url = builder.url;
        this.method = builder.method;
        this.params = builder.params;
        this.body = builder.body;
        this.headers = builder.headers;
    }

    public String getUrl() {
        return this.url;
    }

    public String getMethod() {
        return this.method;
    }

    public Map<String, String> getParams() {
        return this.params;
    }

    public String getBody() {
        return this.body;
    }

    public Map<String, String> getHeaders() {
        return this.headers;
    }

    @StringDef({GET, PUT, POST, DELETE})
    @interface HttpMethod {}

    public VWORequest(@NonNull String url, @NonNull @HttpMethod String method) {
        this.url = url;
        this.params = params;
        this.method = method;
    }

    public void setParams(@Nullable Map<String, String> params) {
        this.params = params;
    }

    public void setBody(@Nullable String body) {
        this.body = body;
    }

    public void setHeaders(@Nullable Map<String, String> headers) {
        this.headers = headers;
    }

    public static class Builder {
        private String url;
        private String method;
        private Map<String, String> params;
        private String body;
        private Map<String, String> headers;

        public Builder(@NonNull String url, @NonNull @HttpMethod String method) {
            this.url = url;
            this.method = method;
        }

        public Builder params(Map<String, String> params) {
            this.params = params;
            return this;
        }

        public Builder body(String body) {
            this.body = body;
            return this;
        }

        public Builder headers(Map<String, String> headers) {
            this.headers = headers;
            return this;
        }

        public Builder addHeader(String key, String value) {
            if(this.headers == null) {
                this.headers = new HashMap<>();
            }

            this.headers.put(key, value);
            return this;
        }

        public VWORequest build() {
            return new VWORequest(this);
        }
    }
}
