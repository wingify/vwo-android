package com.vwo.mobile.segmentation;

import android.content.Context;
import android.os.Build;

import com.vwo.mobile.BuildConfig;
import com.vwo.mobile.VWO;
import com.vwo.mobile.mock.ShadowConfiguration;
import com.vwo.mobile.mock.VWOMock;
import com.vwo.mobile.mock.VWOPersistDataMock;
import com.vwo.mobile.utils.VWOUtils;

import junit.framework.Assert;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

/**
 * Created by aman on Fri 06/10/17 16:33.
 */

@RunWith(RobolectricTestRunner.class)
@Config(packageName = "com.abc", sdk = Build.VERSION_CODES.JELLY_BEAN_MR2, shadows = {ShadowConfiguration.class,
        VWOPersistDataMock.class}, manifest = "AndroidManifest.xml")
@PowerMockIgnore({"org.mockito.*", "org.robolectric.*", "android.*", "org.json.*"})
@PrepareForTest(VWOUtils.class)
public class CustomSegmentTest {

    @Before
    public void setup() {
    }

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    /**
     * Unit tests for custom segments
     */
    @Test
    public void androidVersionTest() {
        try {
            VWO vwo = new VWOMock().getVWOMockObject();

            String androidVersionGreaterThan = "{\n" +
                    "\"prevLogicalOperator\": \"AND\",\n" +
                    "\"type\": \"1\",\n" +
                    "\"operator\": 15,\n" +
                    "\"rOperandValue\": \"14\",\n" +
                    "\"rBracket\": true\n" +
                    "}";


            CustomSegment customSegmentAndroidVersionGreaterThan = new CustomSegment(new JSONObject(androidVersionGreaterThan));
            Assert.assertTrue(customSegmentAndroidVersionGreaterThan.evaluate(vwo));


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void appVersionEqualsTest() throws JSONException {
        VWO vwo = new VWOMock().getVWOMockObject();

        PowerMockito.mockStatic(VWOUtils.class);
        PowerMockito.when(VWOUtils.applicationVersion(ArgumentMatchers.any(Context.class))).thenReturn(20);

        String appVersionEqualsTrue = "{\n" +
                "\"type\": \"6\",\n" +
                "\"operator\": 11,\n" +
                "\"rOperandValue\": \"20\"\n" +
                "}";

        CustomSegment customSegmentAppVersionEqualsTrue = new CustomSegment(new JSONObject(appVersionEqualsTrue));
        Assert.assertTrue(customSegmentAppVersionEqualsTrue.evaluate(vwo));

        String appVersionEqualsFalse = "{\n" +
                "\"type\": \"6\",\n" +
                "\"operator\": 11,\n" +
                "\"rOperandValue\": \"15\"\n" +
                "}";

        CustomSegment customSegmentAppVersionEqualsFalse = new CustomSegment(new JSONObject(appVersionEqualsFalse));
        Assert.assertFalse(customSegmentAppVersionEqualsFalse.evaluate(vwo));
    }

    @Test
    public void appVersionNotEqualsTest() throws JSONException {
        VWO vwo = new VWOMock().getVWOMockObject();

        PowerMockito.mockStatic(VWOUtils.class);
        PowerMockito.when(VWOUtils.applicationVersion(ArgumentMatchers.any(Context.class))).thenReturn(20);

        String appVersionNotEqualsTrue = "{\n" +
                "\"type\": \"6\",\n" +
                "\"operator\": 12,\n" +
                "\"rOperandValue\": \"10\"\n" +
                "}";

        CustomSegment customSegmentAppVersionNotEqualsTrue = new CustomSegment(new JSONObject(appVersionNotEqualsTrue));
        Assert.assertTrue(customSegmentAppVersionNotEqualsTrue.evaluate(vwo));

        String appVersionNotEqualsFalse = "{\n" +
                "\"type\": \"6\",\n" +
                "\"operator\": 12,\n" +
                "\"rOperandValue\": \"20\"\n" +
                "}";

        CustomSegment customSegmentAppVersionNotEqualsFalse = new CustomSegment(new JSONObject(appVersionNotEqualsFalse));
        Assert.assertFalse(customSegmentAppVersionNotEqualsFalse.evaluate(vwo));

    }

    @Test
    public void appVersionContainsTest() throws JSONException {
        VWO vwo = new VWOMock().getVWOMockObject();

        PowerMockito.mockStatic(VWOUtils.class);
        PowerMockito.when(VWOUtils.applicationVersion(ArgumentMatchers.any(Context.class))).thenReturn(20);

        String appVersionContainsTrue = "{\n" +
                "\"type\": \"6\",\n" +
                "\"operator\": 7,\n" +
                "\"rOperandValue\": \"2\"\n" +
                "}";

        CustomSegment customSegmentAppVersionContainsTrue = new CustomSegment(new JSONObject(appVersionContainsTrue));
        Assert.assertTrue(customSegmentAppVersionContainsTrue.evaluate(vwo));

        String appVersionContainsFalse = "{\n" +
                "\"type\": \"6\",\n" +
                "\"operator\": 7,\n" +
                "\"rOperandValue\": \"1\"\n" +
                "}";

        CustomSegment customSegmentAppVersionContainsFalse = new CustomSegment(new JSONObject(appVersionContainsFalse));
        Assert.assertFalse(customSegmentAppVersionContainsFalse.evaluate(vwo));
    }

    @Test
    public void appVersionStartsWithTest() throws JSONException {
        VWO vwo = new VWOMock().getVWOMockObject();

        PowerMockito.mockStatic(VWOUtils.class);
        PowerMockito.when(VWOUtils.applicationVersion(ArgumentMatchers.any(Context.class))).thenReturn(20);

        String startsWithTrue = "{\n" +
                "\"type\": \"6\",\n" +
                "\"operator\": 13,\n" +
                "\"rOperandValue\": \"2\"\n" +
                "}";

        CustomSegment segmentStartsWithTrue = new CustomSegment(new JSONObject(startsWithTrue));
        Assert.assertTrue(segmentStartsWithTrue.evaluate(vwo));

        String startsWithFalse = "{\n" +
                "\"type\": \"6\",\n" +
                "\"operator\": 13,\n" +
                "\"rOperandValue\": \"1\"\n" +
                "}";

        CustomSegment segmentStartsWithFalse = new CustomSegment(new JSONObject(startsWithFalse));
        Assert.assertFalse(segmentStartsWithFalse.evaluate(vwo));
    }

    @Test
    public void appVersionMatchesRegexTest() throws JSONException {
        VWO vwo = new VWOMock().getVWOMockObject();

        PowerMockito.mockStatic(VWOUtils.class);
        PowerMockito.when(VWOUtils.applicationVersion(ArgumentMatchers.any(Context.class))).thenReturn(20);

        String matchesRegexTrue = "{\n" +
                "\"type\": \"6\",\n" +
                "\"operator\": 5,\n" +
                "\"rOperandValue\": \"[0-9]*\"\n" +
                "}";

        CustomSegment segmentRegexMatchesTrue = new CustomSegment(new JSONObject(matchesRegexTrue));
        Assert.assertTrue(segmentRegexMatchesTrue.evaluate(vwo));

        String matchesRegexFalse = "{\n" +
                "\"type\": \"6\",\n" +
                "\"operator\": 5,\n" +
                "\"rOperandValue\": \"[3-9]*\"\n" +
                "}";

        CustomSegment segmentRegexMatchesFalse = new CustomSegment(new JSONObject(matchesRegexFalse));
        Assert.assertFalse(segmentRegexMatchesFalse.evaluate(vwo));
    }

}
