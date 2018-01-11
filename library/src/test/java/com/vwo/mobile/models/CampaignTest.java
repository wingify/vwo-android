package com.vwo.mobile.models;

import com.vwo.mobile.BuildConfig;
import com.vwo.mobile.TestUtils;
import com.vwo.mobile.VWO;
import com.vwo.mobile.mock.VWOMock;

import junit.framework.Assert;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by aman on Fri 22/12/17 15:30.
 */

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 22)
public class CampaignTest {

    @Test
    public void testCampaignData() throws JSONException, IOException {
        VWO vwo = new VWOMock().getVWOMockObject();
        String data = TestUtils.readJsonFile(getClass(), "com/vwo/mobile/models/campaign.json");

        JSONObject object = new JSONObject(data);
        Campaign campaign = new Campaign(vwo, object);
        Campaign campaign2 = new Campaign(vwo, object);

        Assert.assertEquals(campaign.getId(), 14L);
        Assert.assertEquals(campaign.getGoals().size(), 1);
        Assert.assertTrue(campaign.equals(campaign2));

        Set<Campaign> campaigns = new HashSet<>();
        campaigns.add(campaign);
        Assert.assertTrue(campaigns.contains(campaign));
        Assert.assertTrue(campaigns.contains(campaign2));
    }
}
