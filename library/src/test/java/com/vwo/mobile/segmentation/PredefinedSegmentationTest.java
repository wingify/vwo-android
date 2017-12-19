package com.vwo.mobile.segmentation;

import android.os.Build;

import com.vwo.mobile.BuildConfig;
import com.vwo.mobile.VWO;
import com.vwo.mobile.mock.ShadowConfiguration;
import com.vwo.mobile.mock.VWOMock;
import com.vwo.mobile.mock.VWOPersistDataMock;

import junit.framework.Assert;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

/**
 * Created by aman on Tue 19/12/17 11:18.
 */

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.JELLY_BEAN, shadows = {ShadowConfiguration.class,
        VWOPersistDataMock.class }, manifest = "AndroidManifest.xml")
public class PredefinedSegmentationTest {
    /**
     * Unit tests for predefined segments
     */
    @Test
    public void predefinedSegmentationTest() {

        try {
            VWO vwo = new VWOMock().getVWOMockObject();

            String phoneUser = "{\n" +
                    "\"segment_code\": {\n" +
                    "\"device\": \"Phone\"\n" +
                    "},\n" +
                    "\"id\": \"87\",\n" +
                    "\"type\": \"predefined\",\n" +
                    "\"platform\": \"mobile-app\",\n" +
                    "\"name\": \"Phone Users\",\n" +
                    "\"description\": \"Segment for Phone users only\"\n" +
                    "}";

            String tabletUser = "{\n" +
                    "\"segment_code\": {\n" +
                    "\"device\": \"Tablet\"\n" +
                    "},\n" +
                    "\"id\": \"88\",\n" +
                    "\"type\": \"predefined\",\n" +
                    "\"platform\": \"mobile-app\",\n" +
                    "\"name\": \"Tablet Users\",\n" +
                    "\"description\": \"Segment for Tablet users only\"\n" +
                    "}";

            String newUser = "{\n" +
                    "\"segment_code\": {\n" +
                    "\"returning_visitor\": false\n" +
                    "},\n" +
                    "\"id\": \"89\",\n" +
                    "\"type\": \"predefined\",\n" +
                    "\"platform\": \"mobile-app\",\n" +
                    "\"name\": \"New Users\",\n" +
                    "\"description\": \"Segment for new users only\"\n" +
                    "}";

            String returningUser = "{\n" +
                    "\"segment_code\": {\n" +
                    "\"returning_visitor\": true\n" +
                    "},\n" +
                    "\"id\": \"90\",\n" +
                    "\"type\": \"predefined\",\n" +
                    "\"platform\": \"mobile-app\",\n" +
                    "\"name\": \"Returning Users\",\n" +
                    "\"description\": \"Segment for returning users only\"\n" +
                    "}";

            PredefinedSegment predefinedSegmentIsNotPhoneUser = new PredefinedSegment(new JSONObject(phoneUser));
            Assert.assertEquals(predefinedSegmentIsNotPhoneUser.evaluate(vwo), false);

            PredefinedSegment predefinedSegmentIsTabletUser = new PredefinedSegment(new JSONObject(tabletUser));
            Assert.assertEquals(predefinedSegmentIsTabletUser.evaluate(vwo), true);

            PredefinedSegment predefinedSegmentIsNewUser = new PredefinedSegment(new JSONObject(newUser));
            Assert.assertEquals(predefinedSegmentIsNewUser.evaluate(vwo), false);

            PredefinedSegment predefinedSegmentIsReturningUser = new PredefinedSegment(new JSONObject(returningUser));
            Assert.assertEquals(predefinedSegmentIsReturningUser.evaluate(vwo), true);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
