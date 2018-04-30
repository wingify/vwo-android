package com.vwo.mobile.utils;

import com.vwo.mobile.models.CampaignEntry;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.IOException;

/**
 * Created by aman on Tue 24/04/18 16:09.
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 22)
public class SerializerTest {

    @Test
    public void serializeDeserializeTest() throws IOException, ClassNotFoundException {
        String campaignUrl = "www.wingify.com/campaign";
        int campaignId = 100;
        int variationId = 150;

        CampaignEntry campaignEntry = new CampaignEntry(campaignUrl, campaignId, variationId);

        byte[] data = Serializer.marshall(campaignEntry);
        CampaignEntry deserializeCampaign = Serializer.unmarshall(data, CampaignEntry.class);

        Assert.assertEquals(campaignEntry.getUrl(), deserializeCampaign.getUrl());
    }
}
