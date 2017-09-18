package com.vwo.mobile.models;

import android.support.annotation.NonNull;

import java.util.Locale;


/**
 * Created by aman on 17/09/17.
 */

public class CampaignEntry extends Entry {
    private long campaignId;
    private int variationId;

    public CampaignEntry(@NonNull String url, long campaignId, int variationId) {
        super(url);
        this.campaignId = campaignId;
        this.variationId = variationId;
    }

    @Override
    public String getKey() {
        return String.format(Locale.ENGLISH, "%s_%d_%d", TYPE_CAMPAIGN,
                campaignId, variationId);
    }

    public int getVariationId() {
        return variationId;
    }

    public void setVariationId(int variationId) {
        this.variationId = variationId;
    }

    public long getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(long campaignId) {
        this.campaignId = campaignId;
    }

    @Override
    public String toString() {
        String data = super.toString();
        return String.format(Locale.ENGLISH,
                "%sCampaignId: %d\nVariationId: %d\n", data, this.campaignId, this.variationId);
    }
}
