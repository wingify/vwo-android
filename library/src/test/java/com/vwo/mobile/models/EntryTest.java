package com.vwo.mobile.models;

import android.os.Build;

import com.vwo.mobile.BuildConfig;
import com.vwo.mobile.utils.Parceler;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

/**
 * Created by aman on Thu 09/11/17 11:18.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.JELLY_BEAN)
public class EntryTest {

    @Test
    public void parcelTest() {
        String campaignUrl = "www.wingify.com/campaign";
        String goalUrl = "www.wingify.com/goal";
        int campaignId = 100;
        int variationId = 150;
        int goalId = 200;

        CampaignEntry campaignEntry = new CampaignEntry(campaignUrl, campaignId, variationId);
        GoalEntry goalEntry = new GoalEntry(goalUrl, campaignId, variationId, goalId);

        byte[] campaignParcel = Parceler.marshall(campaignEntry);
        byte[] goalParcel = Parceler.marshall(goalEntry);

        CampaignEntry entryCampaign = (CampaignEntry) Parceler.unmarshall(campaignParcel, Entry.CREATOR);
        GoalEntry entryGoal = (GoalEntry) Parceler.unmarshall(goalParcel, Entry.CREATOR);

        Assert.assertEquals(campaignId, entryCampaign.getCampaignId());
        Assert.assertEquals(variationId, entryCampaign.getVariationId());
        Assert.assertEquals(campaignUrl, entryCampaign.getUrl());


        Assert.assertEquals(campaignId, entryGoal.getCampaignId());
        Assert.assertEquals(variationId, entryGoal.getVariationId());
        Assert.assertEquals(goalId, entryGoal.getGoalId());
        Assert.assertEquals(goalUrl, entryGoal.getUrl());
    }
}
