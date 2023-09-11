package com.vwo.mobile.models;

import androidx.annotation.NonNull;

import com.vwo.mobile.network.NetworkRequest;

import java.util.HashMap;
import java.util.Locale;

public class MultiCustomDimensionEntry extends PostEntry {

    @NonNull
    HashMap<String, Object> dimensions;

    public MultiCustomDimensionEntry(@NonNull String url, @NonNull HashMap<String, Object> dimensions, String requestBody, boolean isEventArchEnabled) {
        super(url, requestBody, isEventArchEnabled);
        this.dimensions = dimensions;
    }

    @Override
    public String getKey() {
        return String.format(Locale.ENGLISH, "%s_%s", TYPE_CUSTOM_DIMENSION, dimensions);
    }

    @NonNull
    @Override
    public String toString() {
        return String.format(Locale.ENGLISH, "%s \nCustomDimension: %s\n", super.toString(), dimensions);
    }

    @Override
    public String getRequestType() {
        return NetworkRequest.POST;
    }
}