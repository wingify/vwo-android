package com.vwo.mobile.models;

import android.os.Parcel;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;

import java.util.Locale;

/**
 * Created by aman on 16/09/17.
 */

@Keep
public class GoalEntry extends Entry {
    private long campaignId;
    private int goalId;
    private int variationId;

    public GoalEntry(String url, long campaignId, int variationId,int goalId) {
        super(url);
        this.campaignId = campaignId;
        this.variationId = variationId;
        this.goalId = goalId;
    }

    @Override
    public String getKey() {
        return String.format(Locale.ENGLISH, "%s_%d_%d_%d", TYPE_GOAL,
                campaignId, variationId, goalId);
    }

    @NonNull
    @Override
    public String getClassName() {
        return GoalEntry.class.getName();
    }

    public int getGoalId() {
        return goalId;
    }

    public void setGoalId(int goalId) {
        this.goalId = goalId;
    }

    public long getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(long campaignId) {
        this.campaignId = campaignId;
    }

    public int getVariationId() {
        return variationId;
    }

    public void setVariationId(int variationId) {
        this.variationId = variationId;
    }

    @Override
    public String toString() {
        return String.format(Locale.ENGLISH,
                "%sCampaignId: %d\nVariationId: %d\nGoalId: %d\n", super.toString(),
                this.campaignId, this.variationId, this.goalId);
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(this.campaignId);
        dest.writeInt(this.goalId);
        dest.writeInt(this.variationId);
    }

     public GoalEntry(Parcel in) {
        super(in);
        this.campaignId = in.readLong();
        this.goalId = in.readInt();
        this.variationId = in.readInt();
    }

    public static final Creator<GoalEntry> CREATOR = new Creator<GoalEntry>() {
        @Override
        public GoalEntry createFromParcel(Parcel source) {
            return new GoalEntry(source);
        }

        @Override
        public GoalEntry[] newArray(int size) {
            return new GoalEntry[size];
        }
    };
}
