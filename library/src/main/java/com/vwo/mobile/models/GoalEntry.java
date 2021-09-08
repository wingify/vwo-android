package com.vwo.mobile.models;

import androidx.annotation.Keep;

import java.util.Locale;

/**
 * Created by aman on 16/09/17.
 */

@Keep
public class GoalEntry extends Entry {
    private long campaignId;
    private int goalId;
    private int variationId;

    public GoalEntry(String url, long campaignId, int variationId, int goalId) {
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
}
