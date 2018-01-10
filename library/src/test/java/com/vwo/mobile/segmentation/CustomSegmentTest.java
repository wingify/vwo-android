package com.vwo.mobile.segmentation;

import android.content.Context;
import android.os.Build;

import com.vwo.mobile.VWO;
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
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import static com.vwo.mobile.segmentation.Operator.AND;
import static com.vwo.mobile.segmentation.Operator.CLOSE_PARENTHESES;
import static com.vwo.mobile.segmentation.Operator.OPEN_PARENTHESES;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;

/**
 * Created by aman on Fri 06/10/17 16:33.
 */

@RunWith(RobolectricTestRunner.class)
@Config(packageName = "com.abc", sdk = 17, shadows = {VWOPersistDataMock.class},
        manifest = "AndroidManifest.xml")
@PowerMockIgnore({"org.mockito.*", "org.robolectric.*", "android.*", "org.json.*"})
@PrepareForTest({VWOUtils.class, CustomSegmentEvaluateEnum.class})
public class CustomSegmentTest {

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Test
    @Config(packageName = "com.abc", sdk = Build.VERSION_CODES.JELLY_BEAN_MR2, shadows = {ShadowConfiguration.class,
            VWOPersistDataMock.class}, manifest = "AndroidManifest.xml")
    public void androidVersionEqualToTest() throws JSONException {
        VWO vwo = new VWOMock().getVWOMockObject();

        String equalToTrue = "{\n" +
                "\"type\": \"1\",\n" +
                "\"operator\": 11,\n" +
                "\"rOperandValue\": \"18\"\n" +
                "}";


        CustomSegment segmentEqualToTrue = new CustomSegment(vwo, new JSONObject(equalToTrue));
        Assert.assertTrue(segmentEqualToTrue.evaluate());

        String equalToFalse = "{\n" +
                "\"type\": \"1\",\n" +
                "\"operator\": 11,\n" +
                "\"rOperandValue\": \"20\"\n" +
                "}";

        CustomSegment segmentEqualToFalse = new CustomSegment(vwo, new JSONObject(equalToFalse));
        Assert.assertFalse(segmentEqualToFalse.evaluate());
    }

    @Test
    @Config(packageName = "com.abc", sdk = Build.VERSION_CODES.JELLY_BEAN_MR2, shadows = {ShadowConfiguration.class,
            VWOPersistDataMock.class}, manifest = "AndroidManifest.xml")
    public void androidVersionNotEqualToTest() throws JSONException {
        VWO vwo = new VWOMock().getVWOMockObject();

        String notEqualToTrue = "{\n" +
                "\"type\": \"1\",\n" +
                "\"operator\": 12,\n" +
                "\"rOperandValue\": \"14\"\n" +
                "}";


        CustomSegment segmentNotEqualToTrue = new CustomSegment(vwo, new JSONObject(notEqualToTrue));
        Assert.assertTrue(segmentNotEqualToTrue.evaluate());

        String notEqualToFalse = "{\n" +
                "\"type\": \"1\",\n" +
                "\"operator\": 12,\n" +
                "\"rOperandValue\": \"18\"\n" +
                "}";
        CustomSegment segmentNotEqualToFalse = new CustomSegment(vwo, new JSONObject(notEqualToFalse));
        Assert.assertFalse(segmentNotEqualToFalse.evaluate());
    }


    @Test
    @Config(packageName = "com.abc", sdk = Build.VERSION_CODES.JELLY_BEAN_MR2, shadows = {ShadowConfiguration.class,
            VWOPersistDataMock.class}, manifest = "AndroidManifest.xml")
    public void androidVersionGreaterThanTest() throws JSONException {
        VWO vwo = new VWOMock().getVWOMockObject();

        String androidVersionGreaterThanTrue = "{\n" +
                "\"type\": \"1\",\n" +
                "\"operator\": 15,\n" +
                "\"rOperandValue\": \"14\"\n" +
                "}";


        CustomSegment segmentAndroidVersionGreaterThanTrue = new CustomSegment(vwo, new JSONObject(androidVersionGreaterThanTrue));
        Assert.assertTrue(segmentAndroidVersionGreaterThanTrue.evaluate());

        String androidVersionGreaterThanFalse = "{\n" +
                "\"type\": \"1\",\n" +
                "\"operator\": 15,\n" +
                "\"rOperandValue\": \"20\"\n" +
                "}";
        CustomSegment segmentAndroidVersionGreaterThanFalse = new CustomSegment(vwo, new JSONObject(androidVersionGreaterThanFalse));
        Assert.assertFalse(segmentAndroidVersionGreaterThanFalse.evaluate());
    }

    @Test
    @Config(sdk = Build.VERSION_CODES.JELLY_BEAN_MR2, shadows = {ShadowConfiguration.class,
            VWOPersistDataMock.class})
    public void androidVersionLessThanTest() throws JSONException {
        VWO vwo = new VWOMock().getVWOMockObject();

        String lessThanTrue = "{\n" +
                "\"type\": \"1\",\n" +
                "\"operator\": 16,\n" +
                "\"rOperandValue\": \"20\"\n" +
                "}";


        CustomSegment segmentLessThanTrue = new CustomSegment(vwo, new JSONObject(lessThanTrue));
        Assert.assertTrue(segmentLessThanTrue.evaluate());

        String lessThanFalse = "{\n" +
                "\"type\": \"1\",\n" +
                "\"operator\": 16,\n" +
                "\"rOperandValue\": \"14\"\n" +
                "}";
        CustomSegment segmentLessThanFalse = new CustomSegment(vwo, new JSONObject(lessThanFalse));
        Assert.assertFalse(segmentLessThanFalse.evaluate());
    }

    @Test
    public void appVersionEqualsTest() throws JSONException {
        VWO vwo = new VWOMock().getVWOMockObject();

        PowerMockito.mockStatic(VWOUtils.class);
        PowerMockito.when(VWOUtils.applicationVersion(any(Context.class))).thenReturn(20);

        String appVersionEqualsTrue = "{\n" +
                "\"type\": \"6\",\n" +
                "\"operator\": 11,\n" +
                "\"rOperandValue\": \"20\"\n" +
                "}";

        CustomSegment customSegmentAppVersionEqualsTrue = new CustomSegment(vwo, new JSONObject(appVersionEqualsTrue));
        Assert.assertTrue(customSegmentAppVersionEqualsTrue.evaluate());

        String appVersionEqualsFalse = "{\n" +
                "\"type\": \"6\",\n" +
                "\"operator\": 11,\n" +
                "\"rOperandValue\": \"15\"\n" +
                "}";

        CustomSegment customSegmentAppVersionEqualsFalse = new CustomSegment(vwo, new JSONObject(appVersionEqualsFalse));
        Assert.assertFalse(customSegmentAppVersionEqualsFalse.evaluate());

        String equalToInvalid = "{\n" +
                "\"type\": \"6\",\n" +
                "\"operator\": 11,\n" +
                "\"rOperandValue\": \"abc\"\n" +
                "}";

        CustomSegment segmentInvalid = new CustomSegment(vwo, new JSONObject(equalToInvalid));
        Assert.assertFalse(segmentInvalid.evaluate());
    }

    @Test
    public void appVersionNotEqualsTest() throws JSONException {
        VWO vwo = new VWOMock().getVWOMockObject();

        PowerMockito.mockStatic(VWOUtils.class);
        PowerMockito.when(VWOUtils.applicationVersion(any(Context.class))).thenReturn(20);

        String appVersionNotEqualsTrue = "{\n" +
                "\"type\": \"6\",\n" +
                "\"operator\": 12,\n" +
                "\"rOperandValue\": \"10\"\n" +
                "}";

        CustomSegment customSegmentAppVersionNotEqualsTrue = new CustomSegment(vwo, new JSONObject(appVersionNotEqualsTrue));
        Assert.assertTrue(customSegmentAppVersionNotEqualsTrue.evaluate());

        String appVersionNotEqualsFalse = "{\n" +
                "\"type\": \"6\",\n" +
                "\"operator\": 12,\n" +
                "\"rOperandValue\": \"20\"\n" +
                "}";

        CustomSegment customSegmentAppVersionNotEqualsFalse = new CustomSegment(vwo, new JSONObject(appVersionNotEqualsFalse));
        Assert.assertFalse(customSegmentAppVersionNotEqualsFalse.evaluate());

        String invalid = "{\n" +
                "\"type\": \"6\",\n" +
                "\"operator\": 12,\n" +
                "\"rOperandValue\": \"abc\"\n" +
                "}";

        CustomSegment segmentInvalid = new CustomSegment(vwo, new JSONObject(invalid));
        Assert.assertFalse(segmentInvalid.evaluate());
    }

    @Test
    public void appVersionContainsTest() throws JSONException {
        VWO vwo = new VWOMock().getVWOMockObject();

        PowerMockito.mockStatic(VWOUtils.class);
        PowerMockito.when(VWOUtils.applicationVersion(any(Context.class))).thenReturn(20);

        String appVersionContainsTrue = "{\n" +
                "\"type\": \"6\",\n" +
                "\"operator\": 7,\n" +
                "\"rOperandValue\": \"2\"\n" +
                "}";

        CustomSegment customSegmentAppVersionContainsTrue = new CustomSegment(vwo, new JSONObject(appVersionContainsTrue));
        Assert.assertTrue(customSegmentAppVersionContainsTrue.evaluate());

        String appVersionContainsFalse = "{\n" +
                "\"type\": \"6\",\n" +
                "\"operator\": 7,\n" +
                "\"rOperandValue\": \"1\"\n" +
                "}";

        CustomSegment customSegmentAppVersionContainsFalse = new CustomSegment(vwo, new JSONObject(appVersionContainsFalse));
        Assert.assertFalse(customSegmentAppVersionContainsFalse.evaluate());

        String invalid = "{\n" +
                "\"type\": \"6\",\n" +
                "\"operator\": 7,\n" +
                "\"rOperandValue\": \"abc\"\n" +
                "}";

        CustomSegment segmentInvalid = new CustomSegment(vwo, new JSONObject(invalid));
        Assert.assertFalse(segmentInvalid.evaluate());
    }

    @Test
    public void appVersionStartsWithTest() throws JSONException {
        VWO vwo = new VWOMock().getVWOMockObject();

        PowerMockito.mockStatic(VWOUtils.class);
        PowerMockito.when(VWOUtils.applicationVersion(any(Context.class))).thenReturn(20);

        String startsWithTrue = "{\n" +
                "\"type\": \"6\",\n" +
                "\"operator\": 13,\n" +
                "\"rOperandValue\": \"2\"\n" +
                "}";

        CustomSegment segmentStartsWithTrue = new CustomSegment(vwo, new JSONObject(startsWithTrue));
        Assert.assertTrue(segmentStartsWithTrue.evaluate());

        String startsWithFalse = "{\n" +
                "\"type\": \"6\",\n" +
                "\"operator\": 13,\n" +
                "\"rOperandValue\": \"1\"\n" +
                "}";

        CustomSegment segmentStartsWithFalse = new CustomSegment(vwo, new JSONObject(startsWithFalse));
        Assert.assertFalse(segmentStartsWithFalse.evaluate());

        String invalid = "{\n" +
                "\"type\": \"6\",\n" +
                "\"operator\": 7,\n" +
                "\"rOperandValue\": \"abc\"\n" +
                "}";

        CustomSegment segmentInvalid = new CustomSegment(vwo, new JSONObject(invalid));
        Assert.assertFalse(segmentInvalid.evaluate());
    }

    @Test
    public void appVersionMatchesRegexTest() throws JSONException {
        VWO vwo = new VWOMock().getVWOMockObject();

        PowerMockito.mockStatic(VWOUtils.class);
        PowerMockito.when(VWOUtils.applicationVersion(any(Context.class))).thenReturn(20);

        String matchesRegexTrue = "{\n" +
                "\"type\": \"6\",\n" +
                "\"operator\": 5,\n" +
                "\"rOperandValue\": \"[0-9]*\"\n" +
                "}";

        CustomSegment segmentRegexMatchesTrue = new CustomSegment(vwo, new JSONObject(matchesRegexTrue));
        Assert.assertTrue(segmentRegexMatchesTrue.evaluate());

        String matchesRegexFalse = "{\n" +
                "\"type\": \"6\",\n" +
                "\"operator\": 5,\n" +
                "\"rOperandValue\": \"[3-9]*\"\n" +
                "}";

        CustomSegment segmentRegexMatchesFalse = new CustomSegment(vwo, new JSONObject(matchesRegexFalse));
        Assert.assertFalse(segmentRegexMatchesFalse.evaluate());

        String invalid = "{\n" +
                "\"type\": \"6\",\n" +
                "\"operator\": 7,\n" +
                "\"rOperandValue\": \"abc\"\n" +
                "}";

        CustomSegment segmentInvalid = new CustomSegment(vwo, new JSONObject(invalid));
        Assert.assertFalse(segmentInvalid.evaluate());
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

        CustomSegment segmentCustomVariableEqualsTrue = new CustomSegment(vwo, new JSONObject(customVariableEqualsTrue));
        Assert.assertTrue(segmentCustomVariableEqualsTrue.evaluate());

        String customVariableEqualsFalse = "{\n" +
                "\"type\": \"7\",\n" +
                "\"operator\": 11,\n" +
                "\"lOperandValue\": \"userType\",\n" +
                "\"rOperandValue\": \"free\"\n" +
                "}\n";

        CustomSegment segmentCustomVariableEqualsFalse = new CustomSegment(vwo, new JSONObject(customVariableEqualsFalse));
        Assert.assertFalse(segmentCustomVariableEqualsFalse.evaluate());
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

        CustomSegment segmentNotEqualsTrue = new CustomSegment(vwo, new JSONObject(notEqualsTrue));
        Assert.assertTrue(segmentNotEqualsTrue.evaluate());

        String notEqualsFalse = "{\n" +
                "\"type\": \"7\",\n" +
                "\"operator\": 12,\n" +
                "\"lOperandValue\": \"userType\",\n" +
                "\"rOperandValue\": \"paid\"\n" +
                "}\n";

        CustomSegment segmentNotEqualsFalse = new CustomSegment(vwo, new JSONObject(notEqualsFalse));
        Assert.assertFalse(segmentNotEqualsFalse.evaluate());

    }

    @Test
    public void customVariableContainsTest() throws JSONException {
        VWO vwo = new VWOMock().getVWOMockObject();

        String containsTrue = "{\n" +
                "\"type\": \"7\",\n" +
                "\"operator\": 7,\n" +
                "\"lOperandValue\": \"userType\",\n" +
                "\"rOperandValue\": \"ai\"\n" +
                "}";

        CustomSegment segmentContainsTrue = new CustomSegment(vwo, new JSONObject(containsTrue));
        Assert.assertTrue(segmentContainsTrue.evaluate());

        String containsFalse = "{\n" +
                "\"type\": \"7\",\n" +
                "\"operator\": 7,\n" +
                "\"lOperandValue\": \"userType\",\n" +
                "\"rOperandValue\": \"ee\"\n" +
                "}";

        CustomSegment segmentContainsFalse = new CustomSegment(vwo, new JSONObject(containsFalse));
        Assert.assertFalse(segmentContainsFalse.evaluate());
    }

    @Test
    public void customVariableStartsWithTest() throws JSONException {
        VWO vwo = new VWOMock().getVWOMockObject();

        String startsWithTrue = "{\n" +
                "\"type\": \"7\",\n" +
                "\"operator\": 13,\n" +
                "\"lOperandValue\": \"userType\",\n" +
                "\"rOperandValue\": \"pai\"\n" +
                "}";

        CustomSegment segmentStartsWithTrue = new CustomSegment(vwo, new JSONObject(startsWithTrue));
        Assert.assertTrue(segmentStartsWithTrue.evaluate());

        String startsWithFalse = "{\n" +
                "\"type\": \"7\",\n" +
                "\"operator\": 13,\n" +
                "\"lOperandValue\": \"userType\",\n" +
                "\"rOperandValue\": \"fr\"\n" +
                "}";

        CustomSegment segmentStartsWithFalse = new CustomSegment(vwo, new JSONObject(startsWithFalse));
        Assert.assertFalse(segmentStartsWithFalse.evaluate());
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

        CustomSegment segmentRegexMatchesTrue = new CustomSegment(vwo, new JSONObject(matchesRegexTrue));
        Assert.assertTrue(segmentRegexMatchesTrue.evaluate());

        String matchesRegexFalse = "{\n" +
                "\"type\": \"7\",\n" +
                "\"operator\": 5,\n" +
                "\"lOperandValue\": \"userType\",\n" +
                "\"rOperandValue\": \"[p-z]*\"\n" +
                "}";

        CustomSegment segmentRegexMatchesFalse = new CustomSegment(vwo, new JSONObject(matchesRegexFalse));
        Assert.assertFalse(segmentRegexMatchesFalse.evaluate());
    }

    @Test
    public void dayOfWeekEqualsTest() throws JSONException {
        VWO vwo = new VWOMock().getVWOMockObject();

        String dayOfTheWeekTrue = "{\n" +
        "\"type\": \"3\",\n" +
        "\"operator\": 11,\n" +
        "\"rOperandValue\": [\n" +
        "0,\n" +
        "1,\n" +
        "2\n" +
        "]\n" +
        "}";


        Calendar calendar = PowerMockito.mock(GregorianCalendar.class);

        PowerMockito.mockStatic(VWOUtils.class);
        PowerMockito.when(VWOUtils.getCalendar()).thenReturn(calendar);


        CustomSegment customSegment = new CustomSegment(vwo, new JSONObject(dayOfTheWeekTrue));

        Mockito.when(calendar.get(anyInt())).thenReturn(1);
        Assert.assertTrue(customSegment.evaluate());

        Mockito.when(calendar.get(anyInt())).thenReturn(2);
        Assert.assertTrue(customSegment.evaluate());

        Mockito.when(calendar.get(anyInt())).thenReturn(3);
        Assert.assertTrue(customSegment.evaluate());

        Mockito.when(calendar.get(anyInt())).thenReturn(4);
        Assert.assertFalse(customSegment.evaluate());

        Mockito.when(calendar.get(anyInt())).thenReturn(5);
        Assert.assertFalse(customSegment.evaluate());

        Mockito.when(calendar.get(anyInt())).thenReturn(6);
        Assert.assertFalse(customSegment.evaluate());

        Mockito.when(calendar.get(anyInt())).thenReturn(7);
        Assert.assertFalse(customSegment.evaluate());
    }


    @Test
    public void dayOfWeekNotEqualsTest() throws JSONException {
        VWO vwo = new VWOMock().getVWOMockObject();

        String dayOfTheWeekJson = "{\n" +
                "\"type\": \"3\",\n" +
                "\"operator\": 12,\n" +
                "\"rOperandValue\": [\n" +
                "0,\n" +
                "1,\n" +
                "2\n" +
                "]\n" +
                "}";


        Calendar calendar = PowerMockito.mock(GregorianCalendar.class);

        PowerMockito.mockStatic(VWOUtils.class);
        PowerMockito.when(VWOUtils.getCalendar()).thenReturn(calendar);

        CustomSegment customSegment = new CustomSegment(vwo, new JSONObject(dayOfTheWeekJson));

        Mockito.when(calendar.get(anyInt())).thenReturn(1);
        Assert.assertFalse(customSegment.evaluate());

        Mockito.when(calendar.get(anyInt())).thenReturn(2);
        Assert.assertFalse(customSegment.evaluate());

        Mockito.when(calendar.get(anyInt())).thenReturn(3);
        Assert.assertFalse(customSegment.evaluate());

        Mockito.when(calendar.get(anyInt())).thenReturn(4);
        Assert.assertTrue(customSegment.evaluate());

        Mockito.when(calendar.get(anyInt())).thenReturn(5);
        Assert.assertTrue(customSegment.evaluate());

        Mockito.when(calendar.get(anyInt())).thenReturn(6);
        Assert.assertTrue(customSegment.evaluate());

        Mockito.when(calendar.get(anyInt())).thenReturn(7);
        Assert.assertTrue(customSegment.evaluate());
    }

    @Test
    public void hourOfDayEqualsTest() throws JSONException {
        VWO vwo = new VWOMock().getVWOMockObject();

        String hourOfDayJson = "{\n" +
                "\"type\": \"4\",\n" +
                "\"operator\": 11,\n" +
                "\"rOperandValue\": [\n" +
                "0,\n" +
                "3,\n" +
                "7\n" +
                "]\n" +
                "}";

        Calendar calendar = PowerMockito.mock(GregorianCalendar.class);

        PowerMockito.mockStatic(VWOUtils.class);
        PowerMockito.when(VWOUtils.getCalendar()).thenReturn(calendar);

        CustomSegment customSegment = new CustomSegment(vwo, new JSONObject(hourOfDayJson));
        customSegment.evaluate();

        Mockito.when(calendar.get(anyInt())).thenReturn(0);
        Assert.assertTrue(customSegment.evaluate());

        Mockito.when(calendar.get(anyInt())).thenReturn(2);
        Assert.assertFalse(customSegment.evaluate());

        Mockito.when(calendar.get(anyInt())).thenReturn(3);
        Assert.assertTrue(customSegment.evaluate());

        Mockito.when(calendar.get(anyInt())).thenReturn(25);
        Assert.assertFalse(customSegment.evaluate());

        Mockito.when(calendar.get(anyInt())).thenReturn(5);
        Assert.assertFalse(customSegment.evaluate());

        Mockito.when(calendar.get(anyInt())).thenReturn(6);
        Assert.assertFalse(customSegment.evaluate());

        Mockito.when(calendar.get(anyInt())).thenReturn(7);
        Assert.assertTrue(customSegment.evaluate());
    }

    @Test
    public void hourOfDayNotEqualsTest() throws JSONException {
        VWO vwo = new VWOMock().getVWOMockObject();

        String hourOfDayJson = "{\n" +
                "\"type\": \"4\",\n" +
                "\"operator\": 12,\n" +
                "\"rOperandValue\": [\n" +
                "0,\n" +
                "3,\n" +
                "7\n" +
                "]\n" +
                "}";

        Calendar calendar = PowerMockito.mock(GregorianCalendar.class);

        PowerMockito.mockStatic(VWOUtils.class);
        PowerMockito.when(VWOUtils.getCalendar()).thenReturn(calendar);

        CustomSegment customSegment = new CustomSegment(vwo, new JSONObject(hourOfDayJson));
        customSegment.evaluate();

        Mockito.when(calendar.get(anyInt())).thenReturn(0);
        Assert.assertFalse(customSegment.evaluate());

        Mockito.when(calendar.get(anyInt())).thenReturn(2);
        Assert.assertTrue(customSegment.evaluate());

        Mockito.when(calendar.get(anyInt())).thenReturn(3);
        Assert.assertFalse(customSegment.evaluate());

        Mockito.when(calendar.get(anyInt())).thenReturn(24);
        Assert.assertTrue(customSegment.evaluate());

        Mockito.when(calendar.get(anyInt())).thenReturn(5);
        Assert.assertTrue(customSegment.evaluate());

        Mockito.when(calendar.get(anyInt())).thenReturn(6);
        Assert.assertTrue(customSegment.evaluate());

        Mockito.when(calendar.get(anyInt())).thenReturn(7);
        Assert.assertFalse(customSegment.evaluate());
    }

    @Test
    public void invalidSegmentTest() throws JSONException {
        VWO vwo = new VWOMock().getVWOMockObject();

        String invalidSegmentTypeUpperBound = "{\n" +
                "\"type\": \"8\",\n" +
                "\"operator\": 7,\n" +
                "\"rOperandValue\": \"2\"\n" +
                "}";

        CustomSegment upperBoundSegment = new CustomSegment(vwo, new JSONObject(invalidSegmentTypeUpperBound));
        Assert.assertFalse(upperBoundSegment.evaluate());

        String invalidSegmentTypeLowerBound = "{\n" +
                "\"type\": \"0\",\n" +
                "\"operator\": 7,\n" +
                "\"rOperandValue\": \"2\"\n" +
                "}";

        CustomSegment lowerBoundSegment = new CustomSegment(vwo, new JSONObject(invalidSegmentTypeLowerBound));
        Assert.assertFalse(lowerBoundSegment.evaluate());
    }

    @Test
    public void invalidOperatorTest() throws JSONException {
        VWO vwo = new VWOMock().getVWOMockObject();

        String invalidSegmentTypeUpperBound = "{\n" +
                "\"type\": \"1\",\n" +
                "\"operator\": -1,\n" +
                "\"rOperandValue\": \"2\"\n" +
                "}";

        CustomSegment upperBoundSegment = new CustomSegment(vwo, new JSONObject(invalidSegmentTypeUpperBound));
        Assert.assertFalse(upperBoundSegment.evaluate());

        String invalidSegmentTypeLowerBound = "{\n" +
                "\"type\": \"1\",\n" +
                "\"operator\": 200,\n" +
                "\"rOperandValue\": \"2\"\n" +
                "}";

        CustomSegment lowerBoundSegment = new CustomSegment(vwo, new JSONObject(invalidSegmentTypeLowerBound));
        Assert.assertFalse(lowerBoundSegment.evaluate());
    }


    @Test
    @Config(sdk = 17)
    public void toInfixTest() throws JSONException {
        VWO vwo = new VWOMock().getVWOMockObject();


        String equal1 = "{\n" +
                "\"lBracket\": false,\n" +
                "\"rOperandValue\": \"17\",\n" +
                "\"type\": \"1\",\n" +
                "\"operator\": 11,\n" +
                "\"rBracket\": false,\n" +
                "\"prevLogicalOperator\": null\n" +
                "}";

        CustomSegment segmentEqual1 = new CustomSegment(vwo, new JSONObject(equal1));

        Object[] expectedValue1 = {Boolean.TRUE};

        Assert.assertEquals(Arrays.asList(expectedValue1).toString(), segmentEqual1.toInfix().toString());


        String equalToTrue = "{\n" +
                "\"lBracket\": true,\n" +
                "\"rOperandValue\": \"17\",\n" +
                "\"type\": \"1\",\n" +
                "\"operator\": 11,\n" +
                "\"rBracket\": true,\n" +
                "\"prevLogicalOperator\": AND\n" +
                "}";

        CustomSegment segmentEqualToTrue = new CustomSegment(vwo, new JSONObject(equalToTrue));

        List<Object> expectedValueArray = new ArrayList<>();
        expectedValueArray.add(AND);
        expectedValueArray.add(OPEN_PARENTHESES);
        expectedValueArray.add(Boolean.TRUE);
        expectedValueArray.add(CLOSE_PARENTHESES);

        Assert.assertEquals(expectedValueArray.toString(), segmentEqualToTrue.toInfix().toString());


        String data2 = "{\n" +
                "\"lBracket\": false,\n" +
                "\"rOperandValue\": \"17\",\n" +
                "\"type\": \"1\",\n" +
                "\"operator\": 11,\n" +
                "\"rBracket\": true,\n" +
                "\"prevLogicalOperator\": AND\n" +
                "}";

        CustomSegment segment2 = new CustomSegment(vwo, new JSONObject(data2));

        Object[] expectedValue2 = {AND, Boolean.TRUE, CLOSE_PARENTHESES};

        Assert.assertEquals(Arrays.asList(expectedValue2).toString(), segment2.toInfix().toString());


        String equal3 = "{\n" +
                "\"lBracket\": false,\n" +
                "\"rOperandValue\": \"17\",\n" +
                "\"type\": \"1\",\n" +
                "\"operator\": 11,\n" +
                "\"rBracket\": false,\n" +
                "\"prevLogicalOperator\": AND\n" +
                "}";

        CustomSegment segmentEqual3 = new CustomSegment(vwo, new JSONObject(equal3));

        Object[] expectedValue3 = {AND, Boolean.TRUE};

        Assert.assertEquals(Arrays.asList(expectedValue3).toString(), segmentEqual3.toInfix().toString());

    }
}
