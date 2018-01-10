package com.vwo.mobile.segmentation;

import android.content.Context;
import android.os.Build;

import com.vwo.mobile.TestUtils;
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

import java.io.IOException;
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
    public void androidVersionEqualToTest() throws JSONException, IOException {
        VWO vwo = new VWOMock().getVWOMockObject();

        String equalToTrue = TestUtils.readJsonFile(getClass(), "com/vwo/mobile/segmentation/android_version_equals_true.json");


        CustomSegment segmentEqualToTrue = new CustomSegment(vwo, new JSONObject(equalToTrue));
        Assert.assertTrue(segmentEqualToTrue.evaluate());

        String equalToFalse = TestUtils.readJsonFile(getClass(), "com/vwo/mobile/segmentation/android_version_equals_false.json");

        CustomSegment segmentEqualToFalse = new CustomSegment(vwo, new JSONObject(equalToFalse));
        Assert.assertFalse(segmentEqualToFalse.evaluate());
    }

    @Test
    @Config(packageName = "com.abc", sdk = Build.VERSION_CODES.JELLY_BEAN_MR2, shadows = {ShadowConfiguration.class,
            VWOPersistDataMock.class}, manifest = "AndroidManifest.xml")
    public void androidVersionNotEqualToTest() throws JSONException, IOException {
        VWO vwo = new VWOMock().getVWOMockObject();

        String notEqualToTrue = TestUtils.readJsonFile(getClass(), "com/vwo/mobile/segmentation/android_version_not_equals_true.json");


        CustomSegment segmentNotEqualToTrue = new CustomSegment(vwo, new JSONObject(notEqualToTrue));
        Assert.assertTrue(segmentNotEqualToTrue.evaluate());

        String notEqualToFalse = TestUtils.readJsonFile(getClass(), "com/vwo/mobile/segmentation/android_version_not_equals_false.json");
        CustomSegment segmentNotEqualToFalse = new CustomSegment(vwo, new JSONObject(notEqualToFalse));
        Assert.assertFalse(segmentNotEqualToFalse.evaluate());
    }


    @Test
    @Config(packageName = "com.abc", sdk = Build.VERSION_CODES.JELLY_BEAN_MR2, shadows = {ShadowConfiguration.class,
            VWOPersistDataMock.class}, manifest = "AndroidManifest.xml")
    public void androidVersionGreaterThanTest() throws JSONException, IOException {
        VWO vwo = new VWOMock().getVWOMockObject();

        String androidVersionGreaterThanTrue = TestUtils.readJsonFile(getClass(), "com/vwo/mobile/segmentation/android_version_greater_than_true.json");


        CustomSegment segmentAndroidVersionGreaterThanTrue = new CustomSegment(vwo, new JSONObject(androidVersionGreaterThanTrue));
        Assert.assertTrue(segmentAndroidVersionGreaterThanTrue.evaluate());

        String androidVersionGreaterThanFalse = TestUtils.readJsonFile(getClass(), "com/vwo/mobile/segmentation/android_version_greater_than_false.json");
        CustomSegment segmentAndroidVersionGreaterThanFalse = new CustomSegment(vwo, new JSONObject(androidVersionGreaterThanFalse));
        Assert.assertFalse(segmentAndroidVersionGreaterThanFalse.evaluate());
    }

    @Test
    @Config(sdk = Build.VERSION_CODES.JELLY_BEAN_MR2, shadows = {ShadowConfiguration.class,
            VWOPersistDataMock.class})
    public void androidVersionLessThanTest() throws JSONException, IOException {
        VWO vwo = new VWOMock().getVWOMockObject();

        String lessThanTrue = TestUtils.readJsonFile(getClass(), "com/vwo/mobile/segmentation/android_version_less_than_true.json");


        CustomSegment segmentLessThanTrue = new CustomSegment(vwo, new JSONObject(lessThanTrue));
        Assert.assertTrue(segmentLessThanTrue.evaluate());

        String lessThanFalse = TestUtils.readJsonFile(getClass(), "com/vwo/mobile/segmentation/android_version_less_than_false.json");
        CustomSegment segmentLessThanFalse = new CustomSegment(vwo, new JSONObject(lessThanFalse));
        Assert.assertFalse(segmentLessThanFalse.evaluate());
    }

    @Test
    public void appVersionEqualsTest() throws JSONException, IOException {
        VWO vwo = new VWOMock().getVWOMockObject();

        PowerMockito.mockStatic(VWOUtils.class);
        PowerMockito.when(VWOUtils.applicationVersion(any(Context.class))).thenReturn(20);

        String appVersionEqualsTrue = TestUtils.readJsonFile(getClass(), "com/vwo/mobile/segmentation/app_version_equals.json");

        CustomSegment customSegmentAppVersionEqualsTrue = new CustomSegment(vwo, new JSONObject(appVersionEqualsTrue));
        Assert.assertTrue(customSegmentAppVersionEqualsTrue.evaluate());

        PowerMockito.when(VWOUtils.applicationVersion(any(Context.class))).thenReturn(15);

        String appVersionEqualsFalse = TestUtils.readJsonFile(getClass(), "com/vwo/mobile/segmentation/app_version_equals.json");

        CustomSegment customSegmentAppVersionEqualsFalse = new CustomSegment(vwo, new JSONObject(appVersionEqualsFalse));
        Assert.assertFalse(customSegmentAppVersionEqualsFalse.evaluate());

        String equalToInvalid = TestUtils.readJsonFile(getClass(), "com/vwo/mobile/segmentation/app_version_invalid.json");

        CustomSegment segmentInvalid = new CustomSegment(vwo, new JSONObject(equalToInvalid));
        Assert.assertFalse(segmentInvalid.evaluate());
    }

    @Test
    public void appVersionNotEqualsTest() throws JSONException, IOException {
        VWO vwo = new VWOMock().getVWOMockObject();

        PowerMockito.mockStatic(VWOUtils.class);
        PowerMockito.when(VWOUtils.applicationVersion(any(Context.class))).thenReturn(20);

        String appVersionNotEqualsTrue = TestUtils.readJsonFile(getClass(), "com/vwo/mobile/segmentation/app_version_not_equals.json");

        CustomSegment customSegmentAppVersionNotEqualsTrue = new CustomSegment(vwo, new JSONObject(appVersionNotEqualsTrue));
        Assert.assertTrue(customSegmentAppVersionNotEqualsTrue.evaluate());
        PowerMockito.when(VWOUtils.applicationVersion(any(Context.class))).thenReturn(10);

        String appVersionNotEqualsFalse = TestUtils.readJsonFile(getClass(), "com/vwo/mobile/segmentation/app_version_not_equals.json");

        CustomSegment customSegmentAppVersionNotEqualsFalse = new CustomSegment(vwo, new JSONObject(appVersionNotEqualsFalse));
        Assert.assertFalse(customSegmentAppVersionNotEqualsFalse.evaluate());

        String invalid = TestUtils.readJsonFile(getClass(), "com/vwo/mobile/segmentation/app_version_not_equals_invalid.json");

        CustomSegment segmentInvalid = new CustomSegment(vwo, new JSONObject(invalid));
        Assert.assertFalse(segmentInvalid.evaluate());
    }

    @Test
    public void appVersionContainsTest() throws JSONException, IOException {
        VWO vwo = new VWOMock().getVWOMockObject();

        PowerMockito.mockStatic(VWOUtils.class);
        PowerMockito.when(VWOUtils.applicationVersion(any(Context.class))).thenReturn(20);

        String appVersionContainsTrue = TestUtils.readJsonFile(getClass(), "com/vwo/mobile/segmentation/app_version_contains.json");

        CustomSegment customSegmentAppVersionContainsTrue = new CustomSegment(vwo, new JSONObject(appVersionContainsTrue));
        Assert.assertTrue(customSegmentAppVersionContainsTrue.evaluate());
        PowerMockito.when(VWOUtils.applicationVersion(any(Context.class))).thenReturn(10);

        String appVersionContainsFalse = TestUtils.readJsonFile(getClass(), "com/vwo/mobile/segmentation/app_version_contains.json");

        CustomSegment customSegmentAppVersionContainsFalse = new CustomSegment(vwo, new JSONObject(appVersionContainsFalse));
        Assert.assertFalse(customSegmentAppVersionContainsFalse.evaluate());

        String invalid = TestUtils.readJsonFile(getClass(), "com/vwo/mobile/segmentation/app_version_contains_invalid.json");

        CustomSegment segmentInvalid = new CustomSegment(vwo, new JSONObject(invalid));
        Assert.assertFalse(segmentInvalid.evaluate());
    }

    @Test
    public void appVersionStartsWithTest() throws JSONException, IOException {
        VWO vwo = new VWOMock().getVWOMockObject();

        PowerMockito.mockStatic(VWOUtils.class);
        PowerMockito.when(VWOUtils.applicationVersion(any(Context.class))).thenReturn(20);

        String startsWithTrue = TestUtils.readJsonFile(getClass(), "com/vwo/mobile/segmentation/app_version_starts_with.json");

        CustomSegment segmentStartsWithTrue = new CustomSegment(vwo, new JSONObject(startsWithTrue));
        Assert.assertTrue(segmentStartsWithTrue.evaluate());

        PowerMockito.when(VWOUtils.applicationVersion(any(Context.class))).thenReturn(10);

        String startsWithFalse = TestUtils.readJsonFile(getClass(), "com/vwo/mobile/segmentation/app_version_starts_with.json");

        CustomSegment segmentStartsWithFalse = new CustomSegment(vwo, new JSONObject(startsWithFalse));
        Assert.assertFalse(segmentStartsWithFalse.evaluate());

        String invalid = TestUtils.readJsonFile(getClass(), "com/vwo/mobile/segmentation/app_version_starts_with_invalid.json");

        CustomSegment segmentInvalid = new CustomSegment(vwo, new JSONObject(invalid));
        Assert.assertFalse(segmentInvalid.evaluate());
    }

    @Test
    public void appVersionMatchesRegexTest() throws JSONException, IOException {
        VWO vwo = new VWOMock().getVWOMockObject();

        PowerMockito.mockStatic(VWOUtils.class);
        PowerMockito.when(VWOUtils.applicationVersion(any(Context.class))).thenReturn(20);

        String matchesRegexTrue = TestUtils.readJsonFile(getClass(), "com/vwo/mobile/segmentation/app_version_matches_regex.json");

        CustomSegment segmentRegexMatchesTrue = new CustomSegment(vwo, new JSONObject(matchesRegexTrue));
        Assert.assertTrue(segmentRegexMatchesTrue.evaluate());

        PowerMockito.when(VWOUtils.applicationVersion(any(Context.class))).thenReturn(40);

        String matchesRegexFalse = TestUtils.readJsonFile(getClass(), "com/vwo/mobile/segmentation/app_version_matches_regex.json");

        CustomSegment segmentRegexMatchesFalse = new CustomSegment(vwo, new JSONObject(matchesRegexFalse));
        Assert.assertFalse(segmentRegexMatchesFalse.evaluate());

        String invalid = TestUtils.readJsonFile(getClass(), "com/vwo/mobile/segmentation/app_version_matches_regex_invalid.json");

        CustomSegment segmentInvalid = new CustomSegment(vwo, new JSONObject(invalid));
        Assert.assertFalse(segmentInvalid.evaluate());
    }

    @Test
    public void customVariableEqualsTest() throws JSONException, IOException {
        VWO vwo = new VWOMock().getVWOMockObject();

        String customVariableEqualsTrue = TestUtils.readJsonFile(getClass(), "com/vwo/mobile/segmentation/custom_variable_equals_true.json");

        CustomSegment segmentCustomVariableEqualsTrue = new CustomSegment(vwo, new JSONObject(customVariableEqualsTrue));
        Assert.assertTrue(segmentCustomVariableEqualsTrue.evaluate());

        String customVariableEqualsFalse = TestUtils.readJsonFile(getClass(), "com/vwo/mobile/segmentation/custom_variable_equals_false.json");

        CustomSegment segmentCustomVariableEqualsFalse = new CustomSegment(vwo, new JSONObject(customVariableEqualsFalse));
        Assert.assertFalse(segmentCustomVariableEqualsFalse.evaluate());
    }

    @Test
    public void customVariableNotEqualsTest() throws JSONException, IOException {
        VWO vwo = new VWOMock().getVWOMockObject();

        String notEqualsTrue = TestUtils.readJsonFile(getClass(), "com/vwo/mobile/segmentation/custom_variable_not_equals_true.json");

        CustomSegment segmentNotEqualsTrue = new CustomSegment(vwo, new JSONObject(notEqualsTrue));
        Assert.assertTrue(segmentNotEqualsTrue.evaluate());

        String notEqualsFalse = TestUtils.readJsonFile(getClass(), "com/vwo/mobile/segmentation/custom_variable_not_equals_false.json");

        CustomSegment segmentNotEqualsFalse = new CustomSegment(vwo, new JSONObject(notEqualsFalse));
        Assert.assertFalse(segmentNotEqualsFalse.evaluate());

    }

    @Test
    public void customVariableContainsTest() throws JSONException, IOException {
        VWO vwo = new VWOMock().getVWOMockObject();

        String containsTrue = TestUtils.readJsonFile(getClass(), "com/vwo/mobile/segmentation/custom_variable_contains_true.json");

        CustomSegment segmentContainsTrue = new CustomSegment(vwo, new JSONObject(containsTrue));
        Assert.assertTrue(segmentContainsTrue.evaluate());

        String containsFalse = TestUtils.readJsonFile(getClass(), "com/vwo/mobile/segmentation/custom_variable_contains_false.json");

        CustomSegment segmentContainsFalse = new CustomSegment(vwo, new JSONObject(containsFalse));
        Assert.assertFalse(segmentContainsFalse.evaluate());
    }

    @Test
    public void customVariableStartsWithTest() throws JSONException, IOException {
        VWO vwo = new VWOMock().getVWOMockObject();

        String startsWithTrue = TestUtils.readJsonFile(getClass(), "com/vwo/mobile/segmentation/custom_variable_starts_with_true.json");

        CustomSegment segmentStartsWithTrue = new CustomSegment(vwo, new JSONObject(startsWithTrue));
        Assert.assertTrue(segmentStartsWithTrue.evaluate());

        String startsWithFalse = TestUtils.readJsonFile(getClass(), "com/vwo/mobile/segmentation/custom_variable_starts_with_false.json");

        CustomSegment segmentStartsWithFalse = new CustomSegment(vwo, new JSONObject(startsWithFalse));
        Assert.assertFalse(segmentStartsWithFalse.evaluate());
    }

    @Test
    public void customVariableMatchesRegexTest() throws JSONException, IOException {
        VWO vwo = new VWOMock().getVWOMockObject();

        String matchesRegexTrue = TestUtils.readJsonFile(getClass(), "com/vwo/mobile/segmentation/custom_variable_matches_regex_true.json");

        CustomSegment segmentRegexMatchesTrue = new CustomSegment(vwo, new JSONObject(matchesRegexTrue));
        Assert.assertTrue(segmentRegexMatchesTrue.evaluate());

        String matchesRegexFalse = TestUtils.readJsonFile(getClass(), "com/vwo/mobile/segmentation/custom_variable_matches_regex_false.json");

        CustomSegment segmentRegexMatchesFalse = new CustomSegment(vwo, new JSONObject(matchesRegexFalse));
        Assert.assertFalse(segmentRegexMatchesFalse.evaluate());
    }

    @Test
    public void dayOfWeekEqualsTest() throws JSONException, IOException {
        VWO vwo = new VWOMock().getVWOMockObject();

        String dayOfTheWeekTrue = TestUtils.readJsonFile(getClass(), "com/vwo/mobile/segmentation/day_of_week_equals.json");


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
    public void dayOfWeekNotEqualsTest() throws JSONException, IOException {
        VWO vwo = new VWOMock().getVWOMockObject();

        String dayOfTheWeekJson = TestUtils.readJsonFile(getClass(), "com/vwo/mobile/segmentation/day_of_week_not_equals.json");


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
    public void hourOfDayEqualsTest() throws JSONException, IOException {
        VWO vwo = new VWOMock().getVWOMockObject();

        String hourOfDayJson = TestUtils.readJsonFile(getClass(), "com/vwo/mobile/segmentation/hour_of_day_equals.json");

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
    public void hourOfDayNotEqualsTest() throws JSONException, IOException {
        VWO vwo = new VWOMock().getVWOMockObject();

        String hourOfDayJson = TestUtils.readJsonFile(getClass(), "com/vwo/mobile/segmentation/hour_of_day_not_equals.json");

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
    public void invalidSegmentTest() throws JSONException, IOException {
        VWO vwo = new VWOMock().getVWOMockObject();

        String invalidSegmentTypeUpperBound = TestUtils.readJsonFile(getClass(), "com/vwo/mobile/segmentation/invalid_segment_upper_bound.json");

        CustomSegment upperBoundSegment = new CustomSegment(vwo, new JSONObject(invalidSegmentTypeUpperBound));
        Assert.assertFalse(upperBoundSegment.evaluate());

        String invalidSegmentTypeLowerBound = TestUtils.readJsonFile(getClass(), "com/vwo/mobile/segmentation/invalid_segment_lower_bound.json");

        CustomSegment lowerBoundSegment = new CustomSegment(vwo, new JSONObject(invalidSegmentTypeLowerBound));
        Assert.assertFalse(lowerBoundSegment.evaluate());
    }

    @Test
    public void invalidOperatorTest() throws JSONException, IOException {
        VWO vwo = new VWOMock().getVWOMockObject();

        String invalidSegmentTypeUpperBound = TestUtils.readJsonFile(getClass(), "com/vwo/mobile/segmentation/invalid_operator_upper_bound.json");

        CustomSegment upperBoundSegment = new CustomSegment(vwo, new JSONObject(invalidSegmentTypeUpperBound));
        Assert.assertFalse(upperBoundSegment.evaluate());

        String invalidSegmentTypeLowerBound = TestUtils.readJsonFile(getClass(), "com/vwo/mobile/segmentation/invalid_operator_lower_bound.json");

        CustomSegment lowerBoundSegment = new CustomSegment(vwo, new JSONObject(invalidSegmentTypeLowerBound));
        Assert.assertFalse(lowerBoundSegment.evaluate());
    }


    @Test
    @Config(sdk = 17)
    public void toInfixTest() throws JSONException, IOException {
        VWO vwo = new VWOMock().getVWOMockObject();

        String equal1 = TestUtils.readJsonFile(getClass(), "com/vwo/mobile/segmentation/to_infix1.json");

        CustomSegment segmentEqual1 = new CustomSegment(vwo, new JSONObject(equal1));

        Object[] expectedValue1 = {Boolean.TRUE};

        Assert.assertEquals(Arrays.asList(expectedValue1).toString(), segmentEqual1.toInfix().toString());


        String equalToTrue = TestUtils.readJsonFile(getClass(), "com/vwo/mobile/segmentation/to_infix2.json");

        CustomSegment segmentEqualToTrue = new CustomSegment(vwo, new JSONObject(equalToTrue));

        List<Object> expectedValueArray = new ArrayList<>();
        expectedValueArray.add(AND);
        expectedValueArray.add(OPEN_PARENTHESES);
        expectedValueArray.add(Boolean.TRUE);
        expectedValueArray.add(CLOSE_PARENTHESES);

        Assert.assertEquals(expectedValueArray.toString(), segmentEqualToTrue.toInfix().toString());


        String data2 = TestUtils.readJsonFile(getClass(), "com/vwo/mobile/segmentation/to_infix3.json");

        CustomSegment segment2 = new CustomSegment(vwo, new JSONObject(data2));

        Object[] expectedValue2 = {AND, Boolean.TRUE, CLOSE_PARENTHESES};

        Assert.assertEquals(Arrays.asList(expectedValue2).toString(), segment2.toInfix().toString());


        String equal3 = TestUtils.readJsonFile(getClass(), "com/vwo/mobile/segmentation/to_infix4.json");

        CustomSegment segmentEqual3 = new CustomSegment(vwo, new JSONObject(equal3));

        Object[] expectedValue3 = {AND, Boolean.TRUE};

        Assert.assertEquals(Arrays.asList(expectedValue3).toString(), segmentEqual3.toInfix().toString());

    }
}
