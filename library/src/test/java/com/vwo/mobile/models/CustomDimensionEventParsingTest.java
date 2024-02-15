package com.vwo.mobile.models;

import static com.vwo.mobile.constants.AppConstants.KEY_$VISITOR;
import static com.vwo.mobile.constants.AppConstants.KEY_CUSTOM_EVENT;
import static com.vwo.mobile.constants.AppConstants.KEY_DATA;
import static com.vwo.mobile.constants.AppConstants.KEY_EVENT;
import static com.vwo.mobile.constants.AppConstants.KEY_MSG_ID;
import static com.vwo.mobile.constants.AppConstants.KEY_NAME;
import static com.vwo.mobile.constants.AppConstants.KEY_PROPS;
import static com.vwo.mobile.constants.AppConstants.KEY_SDK_NAME;
import static com.vwo.mobile.constants.AppConstants.KEY_SDK_VERSION;
import static com.vwo.mobile.constants.AppConstants.KEY_SESSION_ID;
import static com.vwo.mobile.constants.AppConstants.KEY_TIME;
import static com.vwo.mobile.constants.AppConstants.KEY_UUID;
import static com.vwo.mobile.constants.AppConstants.KEY_VISITOR;
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
 * Created by Swapnil C. on Fri 13/04/2023.
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 22)
public class CustomDimensionEventParsingTest {
    public static final String DIMENSION_KEY = "abc";
    public static final String DIMENSION_VALUE = "Swapnil";
    private JSONObject actualJson;
    private JSONObject expectedJson;

    @Before
    public void setup() throws JSONException, IOException {
        String data = TestUtils.readJsonFile(getClass(), "com/vwo/mobile/models/customDimensionEvent.json");

        String sdkName = "vwo-node-sdk";
        String sdkVersion = "1.22.1";
        VisitorProps visitorProps = new VisitorProps(DIMENSION_KEY, DIMENSION_VALUE);
        Visitor visitor = new Visitor(visitorProps);
        VisitorSyncPushProps visitorSyncPushProps = new VisitorSyncPushProps(sdkName, sdkVersion, true, visitor);
        String name = "vwo_syncVisitorProp";
        long time = 1632819037323L;//System.currentTimeMillis();
        EventL3 eventL3 = new EventL3(visitorSyncPushProps, name, time);

        String msgId = "77E55027A98E5B4DB70F451F8B1DD86B-1632819038";
        String visId = "77E55027A98E5B4DB70F451F8B1DD86B";
        long sessionId = 1632819038;//System.currentTimeMillis()/1000;,
        EventL2 eventL2 = new EventL2(msgId, visId, sessionId, eventL3, visitor);

        EventL1 eventL1 = new EventL1(eventL2);
        actualJson = eventL1.toJson();
        expectedJson = new JSONObject(data);
    }

    @Test
    public void testEventL2Parsing() throws JSONException {

        JSONObject expectedL2 = expectedJson.getJSONObject(KEY_DATA);
        JSONObject actualL2 = actualJson.getJSONObject(KEY_DATA);
        assertEquals(expectedL2.get(KEY_MSG_ID), actualL2.get(KEY_MSG_ID));
        assertEquals(expectedL2.get(KEY_UUID), actualL2.get(KEY_UUID));
        assertEquals(expectedL2.getLong(KEY_SESSION_ID), actualL2.getLong(KEY_SESSION_ID));

        JSONObject expectedOuterVProps = expectedL2.getJSONObject(KEY_VISITOR).getJSONObject(KEY_PROPS);
        JSONObject actualOuterVProps = actualL2.getJSONObject(KEY_VISITOR).getJSONObject(KEY_PROPS);
        assertEquals(expectedOuterVProps.get(DIMENSION_KEY), actualOuterVProps.get(DIMENSION_KEY));
    }

    @Test
    public void testEventL3() throws JSONException {
        JSONObject expectedL2 = expectedJson.getJSONObject(KEY_DATA);
        JSONObject actualL2 = actualJson.getJSONObject(KEY_DATA);

        JSONObject expectedL3 = expectedL2.getJSONObject(KEY_EVENT);
        JSONObject actualL3 = actualL2.getJSONObject(KEY_EVENT);
        assertEquals(expectedL3.get(KEY_NAME), actualL3.get(KEY_NAME));
        assertEquals(expectedL3.getLong(KEY_TIME), actualL3.getLong(KEY_TIME));

        JSONObject expectedProps = expectedL3.getJSONObject(KEY_PROPS);
        JSONObject actualProps = actualL3.getJSONObject(KEY_PROPS);
        assertEquals(expectedProps.get(KEY_SDK_NAME), actualProps.get(KEY_SDK_NAME));
        assertEquals(expectedProps.get(KEY_SDK_VERSION), actualProps.get(KEY_SDK_VERSION));
        assertEquals(expectedProps.getBoolean(KEY_CUSTOM_EVENT), actualProps.getBoolean(KEY_CUSTOM_EVENT));

        JSONObject expectedVisitorProps = expectedProps.getJSONObject(KEY_$VISITOR).getJSONObject(KEY_PROPS);
        JSONObject actualVisitorProps = actualProps.getJSONObject(KEY_$VISITOR).getJSONObject(KEY_PROPS);
        assertEquals(expectedVisitorProps.get(DIMENSION_KEY), actualVisitorProps.get(DIMENSION_KEY));
    }
}