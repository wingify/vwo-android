package com.vwo.mobile.v3;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by { Nabin Niroula } on 21/03/2023 for android-sdk.
 **/
public class ServerResponseTest {

    private static final String VALID_JSON_VALID_VERSION_3 = "{\"api-version\":3,\"collectionPrefix\":\"eu01\",\"groups\":{\"1\":{\"campaigns\":[21,22],\"name\":\"Group 1\"},\"2\":{\"campaigns\":[25,24],\"name\":\"HimanshuGroup 1\"}},\"campaignGroups\":{\"21\":1,\"22\":1},\"campaigns\":[{\"name\":\"Campaign 5\",\"segment_object\":{\"event\":null,\"type\":\"custom\",\"segmentSettings\":{},\"partialSegments\":[{\"operator\":11,\"rOperandValue\":\"jhg\",\"type\":\"6\"},{\"operator\":11,\"rOperandValue\":\"jh\",\"type\":\"11\",\"prevLogicalOperator\":\"AND\"}]},\"type\":\"VISUAL_AB\",\"goals\":[{\"identifier\":\"kjh\",\"id\":1,\"type\":\"CUSTOM_GOAL\"}],\"pgre\":true,\"clickmap\":1,\"pc_traffic\":100,\"id\":5,\"test_key\":\"k\",\"count_goal_once\":1,\"track_user_on_launch\":false,\"status\":\"RUNNING\",\"variations\":{\"changes\":{},\"name\":\"Control\",\"id\":\"1\",\"weight\":50},\"version\":4,\"_t\":1675079440},{\"id\":7,\"name\":\"Campaign 7\",\"version\":4,\"type\":\"VISUAL_AB\",\"goals\":[{\"identifier\":\"Goal1\",\"id\":1,\"type\":\"CUSTOM_GOAL\"}],\"test_key\":\"SwapnilCampaign\",\"clickmap\":1,\"pc_traffic\":100,\"config_segment_dsl\":{},\"variations\":{\"changes\":{},\"name\":\"Variation-1\",\"id\":\"2\",\"weight\":50},\"count_goal_once\":1,\"track_user_on_launch\":true,\"status\":\"RUNNING\",\"pgre\":true,\"segment_dsl\":{},\"_t\":1678861982},{\"status\":\"PAUSED\",\"variations\":{\"id\":\"1\"},\"version\":2,\"id\":30}]}";

    private static final String VALID_JSON_VALID_VERSION_2 = "[{\"isEventMigrated\":true,\"_t\":1678716991,\"type\":\"VISUAL_AB\",\"test_key\":\"test\",\"variations\":{\"id\":\"2\",\"changes\":{},\"name\":\"Variation-1\",\"weight\":50},\"pc_traffic\":100,\"count_goal_once\":1,\"id\":502,\"track_user_on_launch\":false,\"pgre\":true,\"goals\":[{\"id\":356,\"type\":\"CUSTOM_GOAL\",\"identifier\":\"test\"}],\"name\":\"Campaign prospect\",\"version\":4,\"clickmap\":1,\"status\":\"RUNNING\"},{\"type\":\"groups\",\"groups\":{\"1\":{\"campaigns\":[21,22],\"name\":\"Group 1\"},\"2\":{\"campaigns\":[25,24],\"name\":\"HimanshuGroup 1\"}},\"campaignGroups\":{\"21\":1,\"22\":1}}]";

    private static final String INVALID_JSON = "{\"api-version\":3,\"campaigns\":[{\"name\":\"Campaign 5\",\"segment_object\":{\"event\":null,\"type\":\"custom\",\"segmentSettings\":{},\"partialSegments\":[{\"operator\":11,\"rOperandValue\":\"jhg\",\"type\":\"6\"},{\"operator\":11,\"rOperandValue\":\"jh\",\"type\":\"11\",\"prevLogicalOperator\":\"AND\"}]},\"type\":\"VISUAL_AB\",\"goals\":[{\"identifier\":\"kjh\",\"id\":1,\"type\":\"CUSTOM_GOAL\"}],\"pgre\":true,\"clic";

    private static final String VALID_JSON_INVALID_VERSION_3 = "{\"api-version\":2,\"campaigns\":[{\"name\":\"Campaign 5\",\"segment_object\":{\"event\":null,\"type\":\"custom\",\"segmentSettings\":{},\"partialSegments\":[{\"operator\":11,\"rOperandValue\":\"jhg\",\"type\":\"6\"},{\"operator\":11,\"rOperandValue\":\"jh\",\"type\":\"11\",\"prevLogicalOperator\":\"AND\"}]},\"type\":\"VISUAL_AB\",\"goals\":[{\"identifier\":\"kjh\",\"id\":1,\"type\":\"CUSTOM_GOAL\"}],\"pgre\":true,\"clickmap\":1,\"pc_traffic\":100,\"id\":5,\"test_key\":\"k\",\"count_goal_once\":1,\"track_user_on_launch\":false,\"status\":\"RUNNING\",\"variations\":{\"changes\":{},\"name\":\"Control\",\"id\":\"1\",\"weight\":50},\"version\":4,\"_t\":1675079440},{\"id\":7,\"name\":\"Campaign 7\",\"version\":4,\"type\":\"VISUAL_AB\",\"goals\":[{\"identifier\":\"Goal1\",\"id\":1,\"type\":\"CUSTOM_GOAL\"}],\"test_key\":\"SwapnilCampaign\",\"clickmap\":1,\"pc_traffic\":100,\"config_segment_dsl\":{},\"variations\":{\"changes\":{},\"name\":\"Variation-1\",\"id\":\"2\",\"weight\":50},\"count_goal_once\":1,\"track_user_on_launch\":true,\"status\":\"RUNNING\",\"pgre\":true,\"segment_dsl\":{},\"_t\":1678861982},{\"status\":\"PAUSED\",\"variations\":{\"id\":\"1\"},\"version\":2,\"id\":30}]}";

    @Test
    public void isValidJson_jsonIsValidAndVersionIsValidToo_ReturnsTrue() {
        ServerResponse serverResponse = new ServerResponse(VALID_JSON_VALID_VERSION_3);
        Assert.assertTrue(serverResponse.isJsonValid());
    }

    @Test
    public void isValidJson_jsonIsInvalid_ReturnsFalse() {
        ServerResponse serverResponse = new ServerResponse(INVALID_JSON);
        Assert.assertFalse(serverResponse.isJsonValid());
    }

    @Test
    public void isValidJson_jsonIsNull_ReturnsFalse() {
        ServerResponse serverResponse = new ServerResponse(null);
        Assert.assertFalse(serverResponse.isJsonValid());
    }

    @Test
    public void isNewStandardApi_jsonIsValidAndVersionIsValid_ReturnsTrue() {
        ServerResponse serverResponse = new ServerResponse(VALID_JSON_VALID_VERSION_3);
        Assert.assertTrue(serverResponse.isNewStandardApi());
    }

    @Test
    public void isNewStandardApi_jsonIsValidButVersionIsInvalid_ReturnsFalse() {
        ServerResponse serverResponse = new ServerResponse(VALID_JSON_INVALID_VERSION_3);
        Assert.assertFalse(serverResponse.isNewStandardApi());
    }

    @Test
    public void isLegacyApi_jsonIsValidAndVersionIsValid_ReturnsTrue() {
        ServerResponse serverResponse = new ServerResponse(VALID_JSON_VALID_VERSION_2);
        Assert.assertTrue(serverResponse.isLegacyApi());
    }

}
