package com.vwo.mobile.network;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.vwo.mobile.utils.NetworkUtils;
import com.vwo.mobile.utils.VWOLog;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.Map;

/**
 * Created by aman on 09/09/17.
 */

public class NetworkStringRequest extends NetworkRequest<String> {
    private Map<String, String> headers;
    private Map<String, String> params;

    public NetworkStringRequest(@NonNull String url, @NonNull String method) throws MalformedURLException {
        super(url, method);
    }

    public NetworkStringRequest(@NonNull String url, @NonNull String method,
                                @NonNull Map<String, String> headers,
                                @NonNull Map<String, String> params) throws MalformedURLException {
        super(url, method);
        this.headers = headers;
        this.params = params;
    }

    @Override
    @Nullable
    public String getResponse(NetworkResponse response) {
        try {
            if(response.getBody() != null) {
                return new String(response.getBody(), NetworkUtils.Headers.parseCharset(response.getHeaders()));
            } else {
                VWOLog.e(VWOLog.DOWNLOAD_DATA_LOGS, "Response body is empty", true, false);
            }
        } catch (UnsupportedEncodingException exception) {
            VWOLog.e(VWOLog.DOWNLOAD_DATA_LOGS, exception, true, false);
        }
        return null;
    }

    @Override
    protected Map<String, String> getHeaders() {
        return this.headers != null ? this.headers : super.getHeaders();
    }

    @Override
    public Map<String, String> getParams() {
        return this.params != null ? this.params : super.getParams();
    }
}
