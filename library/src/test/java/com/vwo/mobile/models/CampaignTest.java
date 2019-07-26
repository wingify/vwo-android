package com.vwo.mobile.models;

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
import org.robolectric.shadows.ShadowLog;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by aman on Fri 22/12/17 15:30.
 */

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 22, shadows = {ShadowLog.class})
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
        Assert.assertTrue(campaign.getSegmentType().equals(Campaign.SEGMENT_CUSTOM));
        Assert.assertTrue(campaign.getVersion() == 2);
        Assert.assertTrue(campaign.equals(campaign2));

        Set<Campaign> campaigns = new HashSet<>();
        campaigns.add(campaign);
        Assert.assertTrue(campaigns.contains(campaign));
        Assert.assertTrue(campaigns.contains(campaign2));
    }

    @Test
    public void trackUserManuallyTest() throws IOException, JSONException {
        VWO vwo = new VWOMock().getVWOMockObject();

        String trackUserManually = TestUtils.readJsonFile(getClass(), "com/vwo/mobile/models/track_user_manually.json");

        JSONObject object = new JSONObject(trackUserManually);
        Campaign campaign = new Campaign(vwo, object);

        Assert.assertFalse(campaign.shouldTrackUserAutomatically());
    }

    @Test
    public void predefinedSegmentTest() throws IOException, JSONException {
        VWO vwo = new VWOMock().getVWOMockObject();

        String trackUserManually = TestUtils.readJsonFile(getClass(), "com/vwo/mobile/models/predefined_segment.json");

        JSONObject object = new JSONObject(trackUserManually);
        Campaign campaign = new Campaign(vwo, object);

        Assert.assertTrue(campaign.getSegmentType().equals(Campaign.SEGMENT_PREDEFINED));
    }

    @Test
    public void noSegmentTypeTest() throws IOException, JSONException {
        VWO vwo = new VWOMock().getVWOMockObject();

        String trackUserManually = TestUtils.readJsonFile(getClass(), "com/vwo/mobile/models/no_segment_type.json");

        JSONObject object = new JSONObject(trackUserManually);
        Campaign campaign = new Campaign(vwo, object);

        Assert.assertTrue(campaign.getSegmentType().equals(Campaign.SEGMENT_DEFAULT));
    }
}
