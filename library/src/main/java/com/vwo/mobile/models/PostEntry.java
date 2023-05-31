package com.vwo.mobile.models;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import com.vwo.mobile.network.NetworkRequest;
import com.vwo.mobile.utils.NetworkUtils;

import java.util.Locale;
import java.util.Map;

/**
 * Created by Swapnil on 10/04/23.
 */
@Keep
public abstract class PostEntry extends Entry {

    private String requestBody;
    private boolean isEventArchEnabled=false;

    public PostEntry(@NonNull String url) {
        super(url);
    }

    public PostEntry(@NonNull String url, String requestBody, boolean isEventArchEnabled) {
        super(url);
        this.requestBody = requestBody;
        this.isEventArchEnabled = isEventArchEnabled;
    }

    @Override
    public String getRequestType() {

        if (isEventArchEnabled)
            return NetworkRequest.POST;
        else
            return super.getRequestType();
    }

    @Override
    public Map<String, String> getHeaders() {
        if (isEventArchEnabled)
            return NetworkUtils.Headers.getPostHeaders();
        else
            return super.getHeaders();
    }

    public String getRequestBody() {
        return requestBody;
    }

    @NonNull
    @Override
    public String toString() {
        return String.format(Locale.ENGLISH, "%s requestBody:%s ", super.toString(), requestBody);
    }
}
