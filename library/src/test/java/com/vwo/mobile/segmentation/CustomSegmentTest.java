package com.vwo.mobile.segmentation;

import android.content.Context;
import android.os.Build;

import com.vwo.mobile.VWO;
import com.vwo.mobile.mock.GregorianCalendarShadow;
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

import java.util.regex.Pattern;

/**
 * Created by aman on Fri 06/10/17 16:33.
 */

@RunWith(RobolectricTestRunner.class)
@Config(packageName = "com.abc", sdk = Build.VERSION_CODES.JELLY_BEAN_MR2, shadows = {ShadowConfiguration.class,
        VWOPersistDataMock.class, GregorianCalendarShadow.class}, manifest = "AndroidManifest.xml")
@PowerMockIgnore({"org.mockito.*", "org.robolectric.*", "android.*", "org.json.*"})
@PrepareForTest(VWOUtils.class)
public class CustomSegmentTest {

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Test
    public void androidVersionEqualToTest() throws JSONException {
        VWO vwo = new VWOMock().getVWOMockObject();

        String equalToTrue = "{\n" +
                "\"type\": \"1\",\n" +
                "\"operator\": 11,\n" +
                "\"rOperandValue\": \"18\"\n" +
                "}";


        CustomSegment segmentEqualToTrue = new CustomSegment(new JSONObject(equalToTrue));
        Assert.assertTrue(segmentEqualToTrue.evaluate(vwo));

        String equalToFalse = "{\n" +
                "\"type\": \"1\",\n" +
                "\"operator\": 11,\n" +
                "\"rOperandValue\": \"20\"\n" +
                "}";
        CustomSegment segmentEqualToFalse = new CustomSegment(new JSONObject(equalToFalse));
        Assert.assertFalse(segmentEqualToFalse.evaluate(vwo));
    }

    @Test
    public void androidVersionNotEqualToTest() throws JSONException {
        VWO vwo = new VWOMock().getVWOMockObject();

        String notEqualToTrue = "{\n" +
                "\"type\": \"1\",\n" +
                "\"operator\": 12,\n" +
                "\"rOperandValue\": \"14\"\n" +
                "}";


        CustomSegment segmentNotEqualToTrue = new CustomSegment(new JSONObject(notEqualToTrue));
        Assert.assertTrue(segmentNotEqualToTrue.evaluate(vwo));

        String notEqualToFalse = "{\n" +
                "\"type\": \"1\",\n" +
                "\"operator\": 12,\n" +
                "\"rOperandValue\": \"18\"\n" +
                "}";
        CustomSegment segmentNotEqualToFalse = new CustomSegment(new JSONObject(notEqualToFalse));
        Assert.assertFalse(segmentNotEqualToFalse.evaluate(vwo));
    }


    @Test
    public void androidVersionGreaterThanTest() throws JSONException {
        VWO vwo = new VWOMock().getVWOMockObject();

        String androidVersionGreaterThanTrue = "{\n" +
                "\"type\": \"1\",\n" +
                "\"operator\": 15,\n" +
                "\"rOperandValue\": \"14\"\n" +
                "}";


        CustomSegment segmentAndroidVersionGreaterThanTrue = new CustomSegment(new JSONObject(androidVersionGreaterThanTrue));
        Assert.assertTrue(segmentAndroidVersionGreaterThanTrue.evaluate(vwo));

        String androidVersionGreaterThanFalse = "{\n" +
                "\"type\": \"1\",\n" +
                "\"operator\": 15,\n" +
                "\"rOperandValue\": \"20\"\n" +
                "}";
        CustomSegment segmentAndroidVersionGreaterThanFalse = new CustomSegment(new JSONObject(androidVersionGreaterThanFalse));
        Assert.assertFalse(segmentAndroidVersionGreaterThanFalse.evaluate(vwo));
    }

    @Test
    public void androidVersionLessThanTest() throws JSONException {
        VWO vwo = new VWOMock().getVWOMockObject();

        String lessThanTrue = "{\n" +
                "\"type\": \"1\",\n" +
                "\"operator\": 16,\n" +
                "\"rOperandValue\": \"20\"\n" +
                "}";


        CustomSegment segmentLessThanTrue = new CustomSegment(new JSONObject(lessThanTrue));
        Assert.assertTrue(segmentLessThanTrue.evaluate(vwo));

        String lessThanFalse = "{\n" +
                "\"type\": \"1\",\n" +
                "\"operator\": 16,\n" +
                "\"rOperandValue\": \"14\"\n" +
                "}";
        CustomSegment segmentLessThanFalse = new CustomSegment(new JSONObject(lessThanFalse));
        Assert.assertFalse(segmentLessThanFalse.evaluate(vwo));
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

    @Test
    public void customVariableEqualsTest() throws JSONException {
        VWO vwo = new VWOMock().getVWOMockObject();

        String customVariableEqualsTrue = "{\n" +
                "\"type\": \"7\",\n" +
                "\"operator\": 11,\n" +
                "\"lOperandValue\": \"userType\",\n" +
                "\"rOperandValue\": \"paid\"\n" +
                "}\n";

        CustomSegment segmentCustomVariableEqualsTrue = new CustomSegment(new JSONObject(customVariableEqualsTrue));
        Assert.assertTrue(segmentCustomVariableEqualsTrue.evaluate(vwo));

        String customVariableEqualsFalse = "{\n" +
                "\"type\": \"7\",\n" +
                "\"operator\": 11,\n" +
                "\"lOperandValue\": \"userType\",\n" +
                "\"rOperandValue\": \"free\"\n" +
                "}\n";

        CustomSegment segmentCustomVariableEqualsFalse = new CustomSegment(new JSONObject(customVariableEqualsFalse));
        Assert.assertFalse(segmentCustomVariableEqualsFalse.evaluate(vwo));
    }

    @Test
    public void customVariableNotEqualsTest() throws JSONException {
        VWO vwo = new VWOMock().getVWOMockObject();

        String notEqualsTrue = "{\n" +
                "\"type\": \"7\",\n" +
                "\"operator\": 12,\n" +
                "\"lOperandValue\": \"userType\",\n" +
                "\"rOperandValue\": \"free\"\n" +
                "}\n";

        CustomSegment segmentNotEqualsTrue = new CustomSegment(new JSONObject(notEqualsTrue));
        Assert.assertTrue(segmentNotEqualsTrue.evaluate(vwo));

        String notEqualsFalse = "{\n" +
                "\"type\": \"7\",\n" +
                "\"operator\": 12,\n" +
                "\"lOperandValue\": \"userType\",\n" +
                "\"rOperandValue\": \"paid\"\n" +
                "}\n";

        CustomSegment segmentNotEqualsFalse = new CustomSegment(new JSONObject(notEqualsFalse));
        Assert.assertFalse(segmentNotEqualsFalse.evaluate(vwo));

    }

    @Test
    public void customVariableContainsTest() throws JSONException {
        VWO vwo = new VWOMock().getVWOMockObject();

        PowerMockito.mockStatic(VWOUtils.class);
        PowerMockito.when(VWOUtils.applicationVersion(ArgumentMatchers.any(Context.class))).thenReturn(20);

        String containsTrue = "{\n" +
                "\"type\": \"7\",\n" +
                "\"operator\": 7,\n" +
                "\"lOperandValue\": \"userType\",\n" +
                "\"rOperandValue\": \"ai\"\n" +
                "}";

        CustomSegment segmentContainsTrue = new CustomSegment(new JSONObject(containsTrue));
        Assert.assertTrue(segmentContainsTrue.evaluate(vwo));

        String containsFalse = "{\n" +
                "\"type\": \"7\",\n" +
                "\"operator\": 7,\n" +
                "\"lOperandValue\": \"userType\",\n" +
                "\"rOperandValue\": \"ee\"\n" +
                "}";

        CustomSegment segmentContainsFalse = new CustomSegment(new JSONObject(containsFalse));
        Assert.assertFalse(segmentContainsFalse.evaluate(vwo));
    }

    @Test
    public void customVariableStartsWithTest() throws JSONException {
        VWO vwo = new VWOMock().getVWOMockObject();

        PowerMockito.mockStatic(VWOUtils.class);
        PowerMockito.when(VWOUtils.applicationVersion(ArgumentMatchers.any(Context.class))).thenReturn(20);

        String startsWithTrue = "{\n" +
                "\"type\": \"7\",\n" +
                "\"operator\": 13,\n" +
                "\"lOperandValue\": \"userType\",\n" +
                "\"rOperandValue\": \"pai\"\n" +
                "}";

        CustomSegment segmentStartsWithTrue = new CustomSegment(new JSONObject(startsWithTrue));
        Assert.assertTrue(segmentStartsWithTrue.evaluate(vwo));

        String startsWithFalse = "{\n" +
                "\"type\": \"7\",\n" +
                "\"operator\": 13,\n" +
                "\"lOperandValue\": \"userType\",\n" +
                "\"rOperandValue\": \"fr\"\n" +
                "}";

        CustomSegment segmentStartsWithFalse = new CustomSegment(new JSONObject(startsWithFalse));
        Assert.assertFalse(segmentStartsWithFalse.evaluate(vwo));
    }

    @Test
    public void customVariableMatchesRegexTest() throws JSONException {
        VWO vwo = new VWOMock().getVWOMockObject();

        String matchesRegexTrue = "{\n" +
                "\"type\": \"7\",\n" +
                "\"operator\": 5,\n" +
                "\"lOperandValue\": \"userType\",\n" +
                "\"rOperandValue\": \"[a-p]*\"\n" +
                "}";

        CustomSegment segmentRegexMatchesTrue = new CustomSegment(new JSONObject(matchesRegexTrue));
        Assert.assertTrue(segmentRegexMatchesTrue.evaluate(vwo));

        String matchesRegexFalse = "{\n" +
                "\"type\": \"7\",\n" +
                "\"operator\": 5,\n" +
                "\"lOperandValue\": \"userType\",\n" +
                "\"rOperandValue\": \"[p-z]*\"\n" +
                "}";

        CustomSegment segmentRegexMatchesFalse = new CustomSegment(new JSONObject(matchesRegexFalse));
        Assert.assertFalse(segmentRegexMatchesFalse.evaluate(vwo));
    }

}
