package com.vwo.mobile.models;

import androidx.annotation.NonNull;

import java.util.Locale;

public class CustomDimensionEntry extends PostEntry {
    private String customDimensionKey;
    private String customDimensionValue;

    public CustomDimensionEntry(String url, String customDimensionKey, String customDimensionValue) {
        super(url);
        this.customDimensionKey = customDimensionKey;
        this.customDimensionValue = customDimensionValue;
    }

    public CustomDimensionEntry(@NonNull String url, String customDimensionKey, String customDimensionValue, String requestBody, boolean isEventArchEnabled) {
        super(url, requestBody, isEventArchEnabled);
        this.customDimensionKey = customDimensionKey;
        this.customDimensionValue = customDimensionValue;
    }

    @Override
    public String getKey() {
        return String.format(Locale.ENGLISH, "%s_%s_%s", TYPE_CUSTOM_DIMENSION,
                             customDimensionKey, customDimensionValue);
    }

    public String getTagKey() {
        return customDimensionKey;
    }

    public void setTagKey(String customDimensionKey) {
        this.customDimensionKey = customDimensionKey;
    }

    public String getTagValue() {
        return customDimensionValue;
    }

    public void setTagValue(String customDimensionValue) {
        this.customDimensionValue = customDimensionValue;
    }

    @Override
    public String toString() {
        return String.format(Locale.ENGLISH, "%sCustomDimensionKey: %s\nCustomDimensionValue: %s\n",
                super.toString(), this.customDimensionKey, this.customDimensionValue);
    }
}
