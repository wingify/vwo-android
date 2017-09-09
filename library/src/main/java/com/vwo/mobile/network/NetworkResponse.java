package com.vwo.mobile.network;

import android.support.annotation.Nullable;

import com.vwo.mobile.utils.NetworkUtils;
import com.vwo.mobile.utils.VWOLog;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Created by aman on 05/09/17.
 */

public class NetworkResponse {
    @Nullable
    private byte[] body;
    private int responseCode;
    private Map<String, String> headers;
    private boolean successful;
    private Exception exception;

    protected NetworkResponse(Builder builder) {
        this.body = builder.body;
        this.responseCode = builder.responseCode;
        this.headers = builder.headers;
        this.successful = builder.successful;
        this.exception = builder.exception;
    }

    @Nullable
    public byte[] getBody() {
        return this.body;
    }

    public int getResponseCode() {
        return responseCode;
    }

    @Nullable
    public Map<String, String> getHeaders() {
        return headers;
    }

    public boolean isSuccessful() {
        return successful;
    }

    @Nullable
    public Exception getException() {
        return exception;
    }

    public static class Builder {
        @Nullable
        private byte[] body;
        private int responseCode;
        private Map<String, String> headers;
        private boolean successful;
        private Exception exception;

        public Builder(int responseCode, @Nullable byte[] body, boolean successful) {
            this.responseCode = responseCode;
            this.body = body;
            this.successful = successful;
        }

        public Builder(int responseCode, boolean successful) {
            this(responseCode, null, successful);
        }

        public Builder responseCode(int responseCode) {
            this.responseCode = responseCode;
            return this;
        }

        public Builder headers(Map<String, String> headers) {
            this.headers = headers;
            return this;
        }

        public Builder body(byte[] body) {
            this.body = body;
            return this;
        }

        public Builder from(NetworkResponse response) {
            body = response.body;
            responseCode = response.responseCode;
            headers = response.headers;
            return this;
        }

        public Builder setSuccessful(boolean successful) {
            this.successful = successful;
            return this;
        }

        public Builder setException(Exception exception) {
            this.exception = exception;
            return this;
        }

        public NetworkResponse build() {
            return new NetworkResponse(this);
        }
    }
}
