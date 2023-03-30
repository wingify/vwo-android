package com.vwo.mobile.v3;

import com.vwo.mobile.BuildConfig;

/**
 * Created by { Nabin Niroula } on 21/03/2023 for android-sdk.
 **/
public class MockServerResponse {

    public static void replaceWithApiV3EuModificationResponse(StringBuilder data) {
        if (!BuildConfig.SHOULD_USE_MOCK_DATA_IN_DEBUG) return;
        String collectionPrefix = "  \"collectionPrefix\": \"eu01\",";
        String json = "{" +
                "  \"api-version\": 3," +
                // collectionPrefix +
                "  \"groups\": {" +
                "    \"1\": {" +
                "      \"campaigns\": [" +
                "        7," +
                "        5" +
                "      ]," +
                "      \"name\": \"Group 1\"" +
                "    }," +
                "    \"2\": {" +
                "      \"campaigns\": [" +
                "        8," +
                "        9" +
                "      ]," +
                "      \"name\": \"HimanshuGroup 1\"" +
                "    }" +
                "  }," +
                "  \"campaignGroups\": {" +
                "    \"7\": 1," +
                "    \"5\": 1" +
                "  }," +
                "  \"campaigns\": [" +
                "    {" +
                "      \"name\": \"Campaign 5\"," +
                "      \"version\": 4," +
                "      \"type\": \"VISUAL_AB\"," +
                "      \"goals\": [" +
                "        {" +
                "          \"identifier\": \"kjh\"," +
                "          \"id\": 1," +
                "          \"type\": \"CUSTOM_GOAL\"" +
                "        }" +
                "      ]," +
                "      \"test_key\": \"k\"," +
                "      \"count_goal_once\": 1," +
                "      \"pc_traffic\": 100," +
                "      \"segment_object\": {" +
                "        \"segmentSettings\": {}," +
                "        \"partialSegments\": [" +
                "          {" +
                "            \"rOperandValue\": \"jhg\"," +
                "            \"operator\": 11," +
                "            \"type\": \"6\"" +
                "          }," +
                "          {" +
                "            \"rOperandValue\": \"jh\"," +
                "            \"prevLogicalOperator\": \"AND\"," +
                "            \"operator\": 11," +
                "            \"type\": \"11\"" +
                "          }" +
                "        ]," +
                "        \"event\": null," +
                "        \"type\": \"custom\"" +
                "      }," +
                "      \"variations\": {" +
                "        \"changes\": {}," +
                "        \"name\": \"Control\"," +
                "        \"id\": \"1\"," +
                "        \"weight\": 50" +
                "      }," +
                "      \"_t\": 1675079440," +
                "      \"id\": 5," +
                "      \"status\": \"RUNNING\"," +
                "      \"track_user_on_launch\": false," +
                "      \"clickmap\": 1," +
                "      \"pgre\": true" +
                "    }," +
                "    {" +
                "      \"config_segment_dsl\": {}," +
                "      \"name\": \"Campaign 7\"," +
                "      \"version\": 4," +
                "      \"segment_dsl\": {}," +
                "      \"goals\": [" +
                "        {" +
                "          \"identifier\": \"Goal1\"," +
                "          \"id\": 1," +
                "          \"type\": \"CUSTOM_GOAL\"" +
                "        }" +
                "      ]," +
                "      \"test_key\": \"SwapnilCampaign\"," +
                "      \"count_goal_once\": 1," +
                "      \"pc_traffic\": 100," +
                "      \"_t\": 1678861982," +
                "      \"variations\": {" +
                "        \"changes\": {}," +
                "        \"name\": \"Control\"," +
                "        \"id\": \"1\"," +
                "        \"weight\": 50" +
                "      }," +
                "      \"track_user_on_launch\": true," +
                "      \"id\": 7," +
                "      \"status\": \"RUNNING\"," +
                "      \"pgre\": true," +
                "      \"clickmap\": 1," +
                "      \"type\": \"VISUAL_AB\"" +
                "    }" +
                "  ]" +
                "}";
        data.delete(0, data.length());
        data.append(json);
    }

    public static void replaceWithLegacyApiV2Response(StringBuilder data) {
        if (!BuildConfig.SHOULD_USE_MOCK_DATA_IN_DEBUG) return;
        String json = "[" +
                "  {" +
                "    \"name\": \"Campaign 5\"," +
                "    \"version\": 4," +
                "    \"type\": \"VISUAL_AB\"," +
                "    \"goals\": [" +
                "      {" +
                "        \"identifier\": \"kjh\"," +
                "        \"id\": 1," +
                "        \"type\": \"CUSTOM_GOAL\"" +
                "      }" +
                "    ]," +
                "    \"test_key\": \"k\"," +
                "    \"count_goal_once\": 1," +
                "    \"pc_traffic\": 100," +
                "    \"segment_object\": {" +
                "      \"segmentSettings\": {}," +
                "      \"partialSegments\": [" +
                "        {" +
                "          \"rOperandValue\": \"jhg\"," +
                "          \"operator\": 11," +
                "          \"type\": \"6\"" +
                "        }," +
                "        {" +
                "          \"rOperandValue\": \"jh\"," +
                "          \"prevLogicalOperator\": \"AND\"," +
                "          \"operator\": 11," +
                "          \"type\": \"11\"" +
                "        }" +
                "      ]," +
                "      \"event\": null," +
                "      \"type\": \"custom\"" +
                "    }," +
                "    \"variations\": {" +
                "      \"changes\": {}," +
                "      \"name\": \"Control\"," +
                "      \"id\": \"1\"," +
                "      \"weight\": 50" +
                "    }," +
                "    \"_t\": 1675079440," +
                "    \"id\": 5," +
                "    \"status\": \"RUNNING\"," +
                "    \"track_user_on_launch\": false," +
                "    \"clickmap\": 1," +
                "    \"pgre\": true" +
                "  }," +
                "  {" +
                "    \"config_segment_dsl\": {}," +
                "    \"name\": \"Campaign 7\"," +
                "    \"version\": 4," +
                "    \"segment_dsl\": {}," +
                "    \"goals\": [" +
                "      {" +
                "        \"identifier\": \"Goal1\"," +
                "        \"id\": 1," +
                "        \"type\": \"CUSTOM_GOAL\"" +
                "      }" +
                "    ]," +
                "    \"test_key\": \"SwapnilCampaign\"," +
                "    \"count_goal_once\": 1," +
                "    \"pc_traffic\": 100," +
                "    \"_t\": 1678861982," +
                "    \"variations\": {" +
                "      \"changes\": {}," +
                "      \"name\": \"Control\"," +
                "      \"id\": \"1\"," +
                "      \"weight\": 50" +
                "    }," +
                "    \"track_user_on_launch\": true," +
                "    \"id\": 7," +
                "    \"status\": \"RUNNING\"," +
                "    \"pgre\": true," +
                "    \"clickmap\": 1," +
                "    \"type\": \"VISUAL_AB\"" +
                "  }," +
                "  {" +
                "    \"type\": \"groups\"," +
                "    \"groups\": {" +
                "      \"1\": {" +
                "        \"campaigns\": [" +
                "          21," +
                "          22" +
                "        ]," +
                "        \"name\": \"Group 1\"" +
                "      }," +
                "      \"2\": {" +
                "        \"campaigns\": [" +
                "          25," +
                "          24" +
                "        ]," +
                "        \"name\": \"HimanshuGroup 1\"" +
                "      }" +
                "    }," +
                "    \"campaignGroups\": {" +
                "      \"21\": 1," +
                "      \"22\": 1" +
                "    }" +
                "  }" +
                "]";
        data.delete(0, data.length());
        data.append(json);
    }

}