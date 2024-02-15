package com.vwo.mobile.models;

import static com.vwo.mobile.constants.AppConstants.*;
import static org.junit.Assert.assertEquals;

import com.vwo.mobile.TestUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.IOException;

/**
 * Created by Swapnil C. on Thu 12/04/2023.
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 22)
public class TrackGoalEventParsingTest {
    private JSONObject actualJson;
    private JSONObject expectedJson;

    @Before
    public void setup() throws JSONException, IOException {
        String data = TestUtils.readJsonFile(getClass(), "com/vwo/mobile/models/TrackGoalBody.json");

        String sdkName = "vwo-node-sdk";
        String sdkVersion = "1.22.1";
        boolean isCustomEvent = true;
        Metric metric = new Metric("id_10", "g_210");
        String key = "<revenue>";
        double value = 200;
        VwoMeta meta = new VwoMeta(metric, key, value);
        GoalProps goalProps = new GoalProps(sdkName, sdkVersion, isCustomEvent, meta);
        String name = "goalIdentifier";
        long time = 1632819037323L;//System.currentTimeMillis();
        EventL3 eventL3 = new EventL3(goalProps, name, time);

        String msgId = "77E55027A98E5B4DB70F451F8B1DD86B-1632819038";
        String visId = "77E55027A98E5B4DB70F451F8B1DD86B";
        long sessionId = 1632819038;//System.currentTimeMillis()/1000;,
        EventL2 eventL2 = new EventL2(msgId, visId, sessionId, eventL3, null);

        EventL1 eventL1 = new EventL1(eventL2);
        actualJson = eventL1.toJson();
        expectedJson = new JSONObject(data);
    }
    @Test
    public void testEventL2Parsing() throws JSONException {

        //assertEquals(expected.get(KEY_DATA),parsedJson.get(KEY_DATA));
        JSONObject expectedL2 = expectedJson.getJSONObject(KEY_DATA);
        JSONObject actualL2 = actualJson.getJSONObject(KEY_DATA);
        assertEquals(expectedL2.get(KEY_MSG_ID),actualL2.get(KEY_MSG_ID));
        assertEquals(expectedL2.get(KEY_UUID),actualL2.get(KEY_UUID));
        assertEquals(expectedL2.getLong(KEY_SESSION_ID),actualL2.getLong(KEY_SESSION_ID));

        /*JSONObject expectedOuterVProps = expectedL2.getJSONObject(KEY_VISITOR).getJSONObject(KEY_PROPS);
        JSONObject actualOuterVProps = actualL2.getJSONObject(KEY_VISITOR).getJSONObject(KEY_PROPS);
        assertEquals(expectedOuterVProps.get(KEY_ENV), actualOuterVProps.get(KEY_ENV));*/
//        "id": 10,   //id of the campaign
//                "variation": 1,   //id of the variation
//                "isFirst": 1.

    }

    @Test
    public void testEventL3() throws JSONException {
        JSONObject expectedL2 = expectedJson.getJSONObject(KEY_DATA);
        JSONObject actualL2 = actualJson.getJSONObject(KEY_DATA);

        JSONObject expectedL3 = expectedL2.getJSONObject(KEY_EVENT);
        JSONObject actualL3 = actualL2.getJSONObject(KEY_EVENT);
        assertEquals(expectedL3.get(KEY_NAME),actualL3.get(KEY_NAME));
        assertEquals(expectedL3.getLong(KEY_TIME),actualL3.getLong(KEY_TIME));

        JSONObject expectedProps = expectedL3.getJSONObject(KEY_PROPS);
        JSONObject actualProps = actualL3.getJSONObject(KEY_PROPS);
        assertEquals(expectedProps.get(KEY_SDK_NAME),actualProps.get(KEY_SDK_NAME));
        assertEquals(expectedProps.get(KEY_SDK_VERSION),actualProps.get(KEY_SDK_VERSION));

        /*JSONObject expectedVisitorProps = expectedProps.getJSONObject(KEY_$VISITOR).getJSONObject(KEY_PROPS);
        JSONObject actualVisitorProps = actualProps.getJSONObject(KEY_$VISITOR).getJSONObject(KEY_PROPS);
        assertEquals(expectedVisitorProps.get(KEY_ENV), actualVisitorProps.get(KEY_ENV));*/
    }
}
