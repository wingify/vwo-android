package com.vwo.mobile.models;

import android.os.Parcel;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;

import java.util.Locale;


/**
 * Created by aman on 17/09/17.
 */

@Keep
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

    @NonNull
    @Override
    public String getClassName() {
        return CampaignEntry.class.getName();
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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(this.campaignId);
        dest.writeInt(this.variationId);
    }

    protected CampaignEntry(Parcel in) {
        super(in);
        this.campaignId = in.readLong();
        this.variationId = in.readInt();
    }

    public static final Creator<CampaignEntry> CREATOR = new Creator<CampaignEntry>() {
        @Override
        public CampaignEntry createFromParcel(Parcel source) {
            return new CampaignEntry(source);
        }

        @Override
        public CampaignEntry[] newArray(int size) {
            return new CampaignEntry[size];
        }
    };
}
