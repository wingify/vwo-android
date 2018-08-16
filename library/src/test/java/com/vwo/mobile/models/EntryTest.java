package com.vwo.mobile.models;

import com.vwo.mobile.utils.Serializer;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.IOException;

/**
 * Created by aman on Thu 09/11/17 11:18.
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 22)
public class EntryTest {

    @Test
    public void parcelTest() throws IOException, ClassNotFoundException {
        String campaignUrl = "www.wingify.com/campaign";
        String goalUrl = "www.wingify.com/goal";
        int campaignId = 100;
        int variationId = 150;
        int goalId = 200;

        CampaignEntry campaignEntry = new CampaignEntry(campaignUrl, campaignId, variationId);
        GoalEntry goalEntry = new GoalEntry(goalUrl, campaignId, variationId, goalId);

        byte[] campaignParcel = Serializer.marshall(campaignEntry);
        byte[] goalParcel = Serializer.marshall(goalEntry);

        CampaignEntry entryCampaign = (CampaignEntry) Serializer.unmarshall(campaignParcel, Entry.class);
        GoalEntry entryGoal = (GoalEntry) Serializer.unmarshall(goalParcel, Entry.class);

        Assert.assertEquals(campaignId, entryCampaign.getCampaignId());
        Assert.assertEquals(variationId, entryCampaign.getVariationId());
        Assert.assertEquals(campaignUrl, entryCampaign.getUrl());


        Assert.assertEquals(campaignId, entryGoal.getCampaignId());
        Assert.assertEquals(variationId, entryGoal.getVariationId());
        Assert.assertEquals(goalId, entryGoal.getGoalId());
        Assert.assertEquals(goalUrl, entryGoal.getUrl());
    }
}
