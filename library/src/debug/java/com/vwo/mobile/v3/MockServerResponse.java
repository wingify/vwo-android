package com.vwo.mobile.v3;

import com.vwo.mobile.BuildConfig;

/**
 * Created by { Nabin Niroula } on 21/03/2023 for android-sdk.
 **/
public class MockServerResponse {

    public static void replaceWithApiV3EuModificationResponse(StringBuilder data) {
        if (!BuildConfig.SHOULD_USE_MOCK_DATA_IN_DEBUG) return;
        String json = "{\n" +
                "  \"api-version\": 3,\n" +
                "  \"campaigns\": [\n" +
                "    {\n" +
                "      \"pc_traffic\": 100,\n" +
                "      \"version\": 4,\n" +
                "      \"type\": \"VISUAL_AB\",\n" +
                "      \"_t\": 1682681997,\n" +
                "      \"status\": \"RUNNING\",\n" +
                "      \"id\": 31,\n" +
                "      \"pgre\": true,\n" +
                "      \"variations\": {\n" +
                "        \"id\": \"2\",\n" +
                "        \"changes\": {},\n" +
                "        \"weight\": 50,\n" +
                "        \"name\": \"Variation-1\"\n" +
                "      },\n" +
                "      \"name\": \"Campaign 31\",\n" +
                "      \"track_user_on_launch\": false,\n" +
                "      \"clickmap\": 1,\n" +
                "      \"test_key\": \"demo_one\",\n" +
                "      \"goals\": [\n" +
                "        {\n" +
                "          \"id\": 1,\n" +
                "          \"identifier\": \"goal_demo_one\",\n" +
                "          \"type\": \"CUSTOM_GOAL\"\n" +
                "        }\n" +
                "      ],\n" +
                "      \"count_goal_once\": 1\n" +
                "    },\n" +
                "    {\n" +
                "      \"pc_traffic\": 100,\n" +
                "      \"version\": 4,\n" +
                "      \"type\": \"VISUAL_AB\",\n" +
                "      \"_t\": 1682682025,\n" +
                "      \"status\": \"RUNNING\",\n" +
                "      \"id\": 32,\n" +
                "      \"pgre\": true,\n" +
                "      \"variations\": {\n" +
                "        \"id\": \"0\",\n" +
                "        \"changes\": {},\n" +
                "        \"weight\": 50,\n" +
                "        \"name\": \"Control\"\n" +
                "      },\n" +
                "      \"name\": \"Campaign 32\",\n" +
                "      \"track_user_on_launch\": false,\n" +
                "      \"clickmap\": 1,\n" +
                "      \"test_key\": \"demo_two\",\n" +
                "      \"goals\": [\n" +
                "        {\n" +
                "          \"id\": 1,\n" +
                "          \"identifier\": \"goal_two\",\n" +
                "          \"type\": \"CUSTOM_GOAL\"\n" +
                "        }\n" +
                "      ],\n" +
                "      \"count_goal_once\": 1\n" +
                "    },\n" +
                "    {\n" +
                "      \"pc_traffic\": 100,\n" +
                "      \"version\": 4,\n" +
                "      \"type\": \"VISUAL_AB\",\n" +
                "      \"_t\": 1682682100,\n" +
                "      \"status\": \"RUNNING\",\n" +
                "      \"id\": 33,\n" +
                "      \"pgre\": true,\n" +
                "      \"variations\": {\n" +
                "        \"id\": \"1\",\n" +
                "        \"changes\": {},\n" +
                "        \"weight\": 50,\n" +
                "        \"name\": \"Control\"\n" +
                "      },\n" +
                "      \"name\": \"Campaign 33\",\n" +
                "      \"track_user_on_launch\": false,\n" +
                "      \"clickmap\": 1,\n" +
                "      \"test_key\": \"demo_three\",\n" +
                "      \"goals\": [\n" +
                "        {\n" +
                "          \"id\": 1,\n" +
                "          \"identifier\": \"goal_three\",\n" +
                "          \"type\": \"CUSTOM_GOAL\"\n" +
                "        }\n" +
                "      ],\n" +
                "      \"count_goal_once\": 1\n" +
                "    }\n" +
                "  ],\n" +
                "  \"groups\": {\n" +
                "    \"1\": {\n" +
                "      \"et\": 2,\n" +
                "      \"campaigns\": [\n" +
                "        22,\n" +
                "        25,\n" +
                "        24\n" +
                "      ],\n" +
                "      \"p\": [\n" +
                "        25,\n" +
                "        24\n" +
                "      ],\n" +
                "      \"wt\": {\n" +
                "        \"22\": 50\n" +
                "      },\n" +
                "      \"name\": \"Group 1\"\n" +
                "    },\n" +
                "    \"2\": {\n" +
                "      \"campaigns\": [\n" +
                "        27,\n" +
                "        26,\n" +
                "        28\n" +
                "      ],\n" +
                "      \"name\": \"Group 2\"\n" +
                "    },\n" +
                "    \"3\": {\n" +
                "      \"et\": 1,\n" +
                "      \"campaigns\": [\n" +
                "        33,\n" +
                "        31,\n" +
                "        32\n" +
                "      ],\n" +
                "      \"p\": [\n" +
                "        27,\n" +
                "        28,\n" +
                "        11,\n" +
                "        222,\n" +
                "        31,\n" +
                "        32\n" +
                "      ],\n" +
                "      \"wt\": {\n" +
                "        \"31\": 30,\n" +
                "        \"33\": 70\n" +
                "      },\n" +
                "      \"name\": \"Group 3\"\n" +
                "    }\n" +
                "  },\n" +
                "  \"campaignGroups\": {\n" +
                "    \"22\": 1,\n" +
                "    \"24\": 1,\n" +
                "    \"25\": 1,\n" +
                "    \"26\": 2,\n" +
                "    \"27\": 2,\n" +
                "    \"28\": 2,\n" +
                "    \"31\": 3,\n" +
                "    \"32\": 3,\n" +
                "    \"33\": 3\n" +
                "  }\n" +
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