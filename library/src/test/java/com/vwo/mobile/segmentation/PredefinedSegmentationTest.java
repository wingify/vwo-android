package com.vwo.mobile.segmentation;

import android.os.Build;

import com.vwo.mobile.BuildConfig;
import com.vwo.mobile.VWO;
import com.vwo.mobile.data.VWOPersistData;
import com.vwo.mobile.mock.ShadowConfiguration;
import com.vwo.mobile.mock.VWOMock;
import com.vwo.mobile.mock.VWOPersistDataMock;
import com.vwo.mobile.utils.VWOUtils;

import junit.framework.Assert;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

/**
 * Created by aman on Tue 19/12/17 11:18.
 */

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.JELLY_BEAN, shadows = {
        VWOPersistDataMock.class}, manifest = "AndroidManifest.xml")
@PowerMockIgnore({"org.mockito.*", "org.robolectric.*", "android.*", "org.json.*"})
@PrepareForTest(VWOPersistData.class)
public class PredefinedSegmentationTest {

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    /**
     * Unit tests for predefined segments
     */
    @Test
    public void phoneUserTest() throws JSONException {
        VWO vwo = new VWOMock().getVWOMockObject();

        String phoneUser = "{\n" +
                "\"segment_code\": {\n" +
                "\"device\": \"Phone\"\n" +
                "},\n" +
                "\"id\": \"97\",\n" +
                "\"type\": \"predefined\",\n" +
                "\"platform\": \"mobile-app\",\n" +
                "\"name\": \"Phone Users\",\n" +
                "\"description\": \"Segment for Phone users only\"\n" +
                "}";

        PredefinedSegment segmentPhoneUser = new PredefinedSegment(new JSONObject(phoneUser));
        Assert.assertTrue(segmentPhoneUser.evaluate(vwo));
    }

    @Test
    @Config(shadows = {ShadowConfiguration.class,
            VWOPersistDataMock.class})
    public void tabletUserTest() throws JSONException {
        VWO vwo = new VWOMock().getVWOMockObject();
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

        PredefinedSegment segmentTabletUser = new PredefinedSegment(new JSONObject(tabletUser));
        Assert.assertTrue(segmentTabletUser.evaluate(vwo));
    }

    @Test
    public void newUserTest() throws JSONException {
        VWO vwo = new VWOMock().getVWOMockObject();

        PowerMockito.mockStatic(VWOPersistData.class);
        PowerMockito.when(VWOPersistData.isReturningUser(ArgumentMatchers.any(VWO.class))).thenReturn(false);

        String newUser = "{\n" +
                "\"segment_code\": {\n" +
                "\"returning_visitor\": false\n" +
                "},\n" +
                "\"id\": \"103\",\n" +
                "\"type\": \"predefined\",\n" +
                "\"platform\": \"mobile-app\",\n" +
                "\"name\": \"New Users\",\n" +
                "\"description\": \"Segment for new users only\"\n" +
                "}";

        PredefinedSegment segmentNewUser = new PredefinedSegment(new JSONObject(newUser));
        Assert.assertTrue(segmentNewUser.evaluate(vwo));
    }

    @Test
    public void returningUserTest() throws JSONException {
        VWO vwo = new VWOMock().getVWOMockObject();

        PowerMockito.mockStatic(VWOPersistData.class);
        PowerMockito.when(VWOPersistData.isReturningUser(ArgumentMatchers.any(VWO.class))).thenReturn(true);

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


        PredefinedSegment segmentReturningUser = new PredefinedSegment(new JSONObject(returningUser));
        Assert.assertEquals(segmentReturningUser.evaluate(vwo), true);
    }
}
