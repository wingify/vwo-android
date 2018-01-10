package com.vwo.mobile.models;

import com.vwo.mobile.BuildConfig;
import com.vwo.mobile.VWO;
import com.vwo.mobile.mock.VWOMock;

import junit.framework.Assert;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by aman on Fri 22/12/17 15:30.
 */

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 22)
public class CampaignTest {

    @Test
    public void testCampaignData() throws JSONException {
        VWO vwo = new VWOMock().getVWOMockObject();
        String campaignJson = "{\n" +
                "    \"clickmap\": 1,\n" +
                "    \"count_goal_once\": 1,\n" +
                "    \"goals\": [\n" +
                "      {\n" +
                "        \"id\": 349,\n" +
                "        \"identifier\": \"goal\",\n" +
                "        \"type\": \"REVENUE_TRACKING\"\n" +
                "      }\n" +
                "    ],\n" +
                "    \"id\": 14,\n" +
                "    \"name\": \"Campaign 14\",\n" +
                "    \"pc_traffic\": 100,\n" +
                "    \"segment_object\": {\n" +
                "      \"partialSegments\": [\n" +
                "        {\n" +
                "          \"lBracket\": true,\n" +
                "          \"operator\": 5,\n" +
                "          \"rOperandValue\": \"[0-9]*\",\n" +
                "          \"type\": \"6\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"operator\": 15,\n" +
                "          \"prevLogicalOperator\": \"AND\",\n" +
                "          \"rBracket\": true,\n" +
                "          \"rOperandValue\": \"17\",\n" +
                "          \"type\": \"1\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"lBracket\": false,\n" +
                "          \"lOperandValue\": \"userType\",\n" +
                "          \"operator\": 11,\n" +
                "          \"prevLogicalOperator\": \"OR\",\n" +
                "          \"rOperandValue\": \"paid\",\n" +
                "          \"type\": \"7\"\n" +
                "        }\n" +
                "      ],\n" +
                "      \"type\": \"custom\"\n" +
                "    },\n" +
                "    \"status\": \"RUNNING\",\n" +
                "    \"track_user_on_launch\": true,\n" +
                "    \"type\": \"VISUAL_AB\",\n" +
                "    \"variations\": {\n" +
                "      \"changes\": {\n" +
                "        \"layout\": \"grid\",\n" +
                "        \"socialMedia\": true\n" +
                "      },\n" +
                "      \"id\": \"2\",\n" +
                "      \"name\": \"Variation-1\",\n" +
                "      \"weight\": 100\n" +
                "    },\n" +
                "    \"version\": 2\n" +
                "  },\n" +
                "  {\n" +
                "    \"id\": 6,\n" +
                "    \"status\": \"PAUSED\",\n" +
                "    \"variations\": {\n" +
                "      \"id\": \"3\"\n" +
                "    },\n" +
                "    \"version\": 2\n" +
                "  }";

        JSONObject object = new JSONObject(campaignJson);
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
