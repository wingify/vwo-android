package com.vwo.mobile.segmentation;

import android.content.Context;
import android.os.Build;

import com.vwo.mobile.VWO;
import com.vwo.mobile.constants.AppConstants;
import com.vwo.mobile.data.VWOPersistData;
import com.vwo.mobile.mock.ShadowVWOLog;
import com.vwo.mobile.mock.VWOMock;
import com.vwo.mobile.mock.VWOPersistDataShadow;
import com.vwo.mobile.utils.VWOUtils;

import junit.framework.Assert;

import org.json.JSONArray;
import org.json.JSONException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Calendar;
import java.util.GregorianCalendar;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;

/**
 * Created by aman on Thu 21/12/17 14:53.
 */

@RunWith(RobolectricTestRunner.class)
@Config(packageName = "com.abc", sdk = 22, shadows = {VWOPersistDataShadow.class, ShadowVWOLog.class},
        manifest = "AndroidManifest.xml")
@PowerMockIgnore({"org.mockito.*", "org.robolectric.*", "android.*", "org.json.*", "com.vwo.mobile.utils.VWOLog"})
public class CustomSegmentEnumTest {
    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Test
    @Config(packageName = "com.abc", sdk = Build.VERSION_CODES.JELLY_BEAN_MR2, shadows = {VWOPersistDataShadow.class},
            manifest = "AndroidManifest.xml")
    public void androidVersionEqualToTest() throws JSONException {
        VWO vwo = new VWOMock().getVWOMockObject();

        CustomSegmentEvaluateEnum.EvaluateSegment evaluator =
                CustomSegmentEvaluateEnum.getEvaluator(AppConstants.ANDROID_VERSION, AppConstants.EQUAL_TO);

        Assert.assertTrue(evaluator.evaluate(vwo, new JSONArray("[\"18\"]")));

        Assert.assertFalse(evaluator.evaluate(vwo, new JSONArray("[\"20\"]")));
    }

    @Test
    @Config(packageName = "com.abc", sdk = Build.VERSION_CODES.JELLY_BEAN_MR2, shadows = {
            VWOPersistDataShadow.class}, manifest = "AndroidManifest.xml")
    public void androidVersionNotEqualToTest() throws JSONException {
        VWO vwo = new VWOMock().getVWOMockObject();

        CustomSegmentEvaluateEnum.EvaluateSegment evaluator =
                CustomSegmentEvaluateEnum.getEvaluator(AppConstants.ANDROID_VERSION, AppConstants.NOT_EQUAL_TO);

        Assert.assertTrue(evaluator.evaluate(vwo, new JSONArray("[\"14\"]")));

        Assert.assertFalse(evaluator.evaluate(vwo, new JSONArray("[\"18\"]")));
    }


    @Test
    @Config(packageName = "com.abc", sdk = Build.VERSION_CODES.JELLY_BEAN_MR2, shadows = {
            VWOPersistDataShadow.class}, manifest = "AndroidManifest.xml")
    public void androidVersionGreaterThanTest() throws JSONException {
        VWO vwo = new VWOMock().getVWOMockObject();

        CustomSegmentEvaluateEnum.EvaluateSegment evaluator =
                CustomSegmentEvaluateEnum.getEvaluator(AppConstants.ANDROID_VERSION, AppConstants.GREATER_THAN);

        Assert.assertTrue(evaluator.evaluate(vwo, new JSONArray("[\"14\"]")));

        Assert.assertFalse(evaluator.evaluate(vwo, new JSONArray("[\"20\"]")));
    }

    @Test
    @Config(packageName = "com.abc", sdk = Build.VERSION_CODES.JELLY_BEAN_MR2, shadows = {
            VWOPersistDataShadow.class}, manifest = "AndroidManifest.xml")
    public void androidVersionLessThanTest() throws JSONException {
        VWO vwo = new VWOMock().getVWOMockObject();

        CustomSegmentEvaluateEnum.EvaluateSegment evaluator =
                CustomSegmentEvaluateEnum.getEvaluator(AppConstants.ANDROID_VERSION, AppConstants.LESS_THAN);

        Assert.assertTrue(evaluator.evaluate(vwo, new JSONArray("[\"20\"]")));

        Assert.assertFalse(evaluator.evaluate(vwo, new JSONArray("[\"14\"]")));
    }

    @Test
    @PrepareForTest({VWOUtils.class})
    public void appVersionEqualsTest() throws JSONException {
        VWO vwo = new VWOMock().getVWOMockObject();

        PowerMockito.mockStatic(VWOUtils.class);
        PowerMockito.when(VWOUtils.applicationVersion(any(Context.class))).thenReturn(20);

        CustomSegmentEvaluateEnum.EvaluateSegment evaluator =
                CustomSegmentEvaluateEnum.getEvaluator(AppConstants.APP_VERSION, AppConstants.EQUAL_TO);

        Assert.assertTrue(evaluator.evaluate(vwo, new JSONArray("[\"20\"]")));

        Assert.assertFalse(evaluator.evaluate(vwo, new JSONArray("[\"15\"]")));

        Assert.assertFalse(evaluator.evaluate(vwo, new JSONArray("[\"abc\"]")));
    }

    @Test
    @PrepareForTest({VWOUtils.class})
    public void appVersionNotEqualsTest() throws JSONException {
        VWO vwo = new VWOMock().getVWOMockObject();

        PowerMockito.mockStatic(VWOUtils.class);
        PowerMockito.when(VWOUtils.applicationVersion(any(Context.class))).thenReturn(20);

        CustomSegmentEvaluateEnum.EvaluateSegment evaluator =
                CustomSegmentEvaluateEnum.getEvaluator(AppConstants.APP_VERSION, AppConstants.NOT_EQUAL_TO);

        Assert.assertTrue(evaluator.evaluate(vwo, new JSONArray("[\"10\"]")));

        Assert.assertFalse(evaluator.evaluate(vwo, new JSONArray("[\"20\"]")));

        Assert.assertFalse(evaluator.evaluate(vwo, new JSONArray("[\"abc\"]")));
    }

    @Test
    @PrepareForTest({VWOUtils.class})
    public void appVersionLessThanTest() throws JSONException {
        VWO vwo = new VWOMock().getVWOMockObject();

        PowerMockito.mockStatic(VWOUtils.class);
        PowerMockito.when(VWOUtils.applicationVersion(any(Context.class))).thenReturn(20);

        CustomSegmentEvaluateEnum.EvaluateSegment evaluator =
                CustomSegmentEvaluateEnum.getEvaluator(AppConstants.APP_VERSION, AppConstants.LESS_THAN);

        Assert.assertTrue(evaluator.evaluate(vwo, new JSONArray("[\"21\"]")));

        Assert.assertFalse(evaluator.evaluate(vwo, new JSONArray("[\"1\"]")));

        Assert.assertFalse(evaluator.evaluate(vwo, new JSONArray("[\"abc\"]")));
    }

    @Test
    @PrepareForTest({VWOUtils.class})
    public void appVersionGreaterThanTest() throws JSONException {
        VWO vwo = new VWOMock().getVWOMockObject();

        PowerMockito.mockStatic(VWOUtils.class);
        PowerMockito.when(VWOUtils.applicationVersion(any(Context.class))).thenReturn(20);

        CustomSegmentEvaluateEnum.EvaluateSegment evaluator =
                CustomSegmentEvaluateEnum.getEvaluator(AppConstants.APP_VERSION, AppConstants.GREATER_THAN);

        Assert.assertTrue(evaluator.evaluate(vwo, new JSONArray("[\"2\"]")));

        Assert.assertFalse(evaluator.evaluate(vwo, new JSONArray("[\"21\"]")));

        Assert.assertFalse(evaluator.evaluate(vwo, new JSONArray("[\"abc\"]")));
    }

    @Test
    public void customVariableEqualsTest() throws JSONException {
        VWO vwo = new VWOMock().getVWOMockObject();

        CustomSegmentEvaluateEnum.EvaluateSegment evaluator =
                CustomSegmentEvaluateEnum.getEvaluator(AppConstants.CUSTOM_SEGMENT, AppConstants.EQUAL_TO);

        Assert.assertTrue(evaluator.evaluate(vwo, new JSONArray("[\"paid\"]"), "userType"));

        Assert.assertFalse(evaluator.evaluate(vwo, new JSONArray("[\"free\"]"), "userType"));
    }

    @Test
    public void customVariableNotEqualsTest() throws JSONException {
        VWO vwo = new VWOMock().getVWOMockObject();

        CustomSegmentEvaluateEnum.EvaluateSegment evaluator =
                CustomSegmentEvaluateEnum.getEvaluator(AppConstants.CUSTOM_SEGMENT, AppConstants.NOT_EQUAL_TO);

        Assert.assertTrue(evaluator.evaluate(vwo, new JSONArray("[\"free\"]"), "userType"));

        Assert.assertFalse(evaluator.evaluate(vwo, new JSONArray("[\"paid\"]"), "userType"));

    }

    @Test
    public void customVariableContainsTest() throws JSONException {
        VWO vwo = new VWOMock().getVWOMockObject();

        CustomSegmentEvaluateEnum.EvaluateSegment evaluator =
                CustomSegmentEvaluateEnum.getEvaluator(AppConstants.CUSTOM_SEGMENT, AppConstants.CONTAINS);

        Assert.assertTrue(evaluator.evaluate(vwo, new JSONArray("[\"ai\"]"), "userType"));

        Assert.assertFalse(evaluator.evaluate(vwo, new JSONArray("[\"ee\"]"), "userType"));
    }

    @Test
    public void customVariableStartsWithTest() throws JSONException {
        VWO vwo = new VWOMock().getVWOMockObject();

        CustomSegmentEvaluateEnum.EvaluateSegment evaluator =
                CustomSegmentEvaluateEnum.getEvaluator(AppConstants.CUSTOM_SEGMENT, AppConstants.STARTS_WITH);

        Assert.assertTrue(evaluator.evaluate(vwo, new JSONArray("[\"pai\"]"), "userType"));

        Assert.assertFalse(evaluator.evaluate(vwo, new JSONArray("[\"fr\"]"), "userType"));
    }

    @Test
    public void customVariableMatchesRegexTest() throws JSONException {
        VWO vwo = new VWOMock().getVWOMockObject();

        CustomSegmentEvaluateEnum.EvaluateSegment evaluator =
                CustomSegmentEvaluateEnum.getEvaluator(AppConstants.CUSTOM_SEGMENT, AppConstants.MATCHES_REGEX);

        Assert.assertTrue(evaluator.evaluate(vwo, new JSONArray("[\"[a-p]*\"]"), "userType"));

        Assert.assertFalse(evaluator.evaluate(vwo, new JSONArray("[\"[p-z]*\"]"), "userType"));
    }

    @Test
    @PrepareForTest({VWOUtils.class, GregorianCalendar.class})
    public void dayOfWeekEqualsTest() throws JSONException {
        VWO vwo = new VWOMock().getVWOMockObject();

        CustomSegmentEvaluateEnum.EvaluateSegment evaluator =
                CustomSegmentEvaluateEnum.getEvaluator(AppConstants.DAY_OF_WEEK, AppConstants.EQUAL_TO);

        Calendar calendar = Mockito.mock(GregorianCalendar.class);

        PowerMockito.mockStatic(VWOUtils.class);
        Mockito.when(VWOUtils.getCalendar()).thenReturn(calendar);

        Mockito.when(calendar.get(anyInt())).thenReturn(1, 2, 3, 4, 5, 6, 7);
        Assert.assertTrue(evaluator.evaluate(vwo, new JSONArray("[0, 1, 2]")));

        Assert.assertTrue(evaluator.evaluate(vwo, new JSONArray("[0, 1, 2]")));

        Assert.assertTrue(evaluator.evaluate(vwo, new JSONArray("[0, 1, 2]")));

        Assert.assertFalse(evaluator.evaluate(vwo, new JSONArray("[0, 1, 2]")));

        Assert.assertFalse(evaluator.evaluate(vwo, new JSONArray("[0, 1, 2]")));

        Assert.assertFalse(evaluator.evaluate(vwo, new JSONArray("[0, 1, 2]")));

        Assert.assertFalse(evaluator.evaluate(vwo, new JSONArray("[0, 1, 2]")));
    }


    @Test
    @PrepareForTest({VWOUtils.class, GregorianCalendar.class})
    public void dayOfWeekNotEqualsTest() throws JSONException {
        VWO vwo = new VWOMock().getVWOMockObject();

        CustomSegmentEvaluateEnum.EvaluateSegment evaluator =
                CustomSegmentEvaluateEnum.getEvaluator(AppConstants.DAY_OF_WEEK, AppConstants.NOT_EQUAL_TO);

        Calendar calendar = Mockito.mock(GregorianCalendar.class);

        PowerMockito.mockStatic(VWOUtils.class);
        Mockito.when(VWOUtils.getCalendar()).thenReturn(calendar);

        Mockito.when(calendar.get(any(Integer.class))).thenReturn(1, 2, 3, 4, 5, 6, 7);
        Assert.assertFalse(evaluator.evaluate(vwo, new JSONArray("[0, 1, 2]")));

        Assert.assertFalse(evaluator.evaluate(vwo, new JSONArray("[0, 1, 2]")));

        Assert.assertFalse(evaluator.evaluate(vwo, new JSONArray("[0, 1, 2]")));

        Assert.assertTrue(evaluator.evaluate(vwo, new JSONArray("[0, 1, 2]")));

        Assert.assertTrue(evaluator.evaluate(vwo, new JSONArray("[0, 1, 2]")));

        Assert.assertTrue(evaluator.evaluate(vwo, new JSONArray("[0, 1, 2]")));

        Assert.assertTrue(evaluator.evaluate(vwo, new JSONArray("[0, 1, 2]")));
    }

    @Test
    @PrepareForTest({VWOUtils.class, GregorianCalendar.class})
    public void hourOfDayEqualsTest() throws JSONException {
        VWO vwo = new VWOMock().getVWOMockObject();

        CustomSegmentEvaluateEnum.EvaluateSegment evaluator =
                CustomSegmentEvaluateEnum.getEvaluator(AppConstants.HOUR_OF_DAY, AppConstants.EQUAL_TO);

        Calendar calendar = Mockito.mock(GregorianCalendar.class);

        PowerMockito.mockStatic(VWOUtils.class);
        Mockito.when(VWOUtils.getCalendar()).thenReturn(calendar);

        Mockito.when(calendar.get(anyInt())).thenReturn(0, 2, 3, 25, 5, 6, 7);
        Assert.assertTrue(evaluator.evaluate(vwo, new JSONArray("[0, 3, 7]")));

        Assert.assertFalse(evaluator.evaluate(vwo, new JSONArray("[0, 3, 7]")));

        Assert.assertTrue(evaluator.evaluate(vwo, new JSONArray("[0, 3, 7]")));

        Assert.assertFalse(evaluator.evaluate(vwo, new JSONArray("[0, 3, 7]")));

        Assert.assertFalse(evaluator.evaluate(vwo, new JSONArray("[0, 3, 7]")));

        Assert.assertFalse(evaluator.evaluate(vwo, new JSONArray("[0, 3, 7]")));

        Assert.assertTrue(evaluator.evaluate(vwo, new JSONArray("[0, 3, 7]")));
    }

    @Test
    @PrepareForTest({VWOUtils.class, GregorianCalendar.class})
    public void hourOfDayNotEqualsTest() throws JSONException {
        VWO vwo = new VWOMock().getVWOMockObject();

        CustomSegmentEvaluateEnum.EvaluateSegment evaluator =
                CustomSegmentEvaluateEnum.getEvaluator(AppConstants.HOUR_OF_DAY, AppConstants.NOT_EQUAL_TO);

        Calendar calendar = Mockito.mock(GregorianCalendar.class);

        PowerMockito.mockStatic(VWOUtils.class);
        Mockito.when(VWOUtils.getCalendar()).thenReturn(calendar);

        Mockito.when(calendar.get(anyInt())).thenReturn(0, 2, 3, 24, 5, 6, 7);
        Assert.assertFalse(evaluator.evaluate(vwo, new JSONArray("[0, 3, 7]")));

        Assert.assertTrue(evaluator.evaluate(vwo, new JSONArray("[0, 3, 7]")));

        Assert.assertFalse(evaluator.evaluate(vwo, new JSONArray("[0, 3, 7]")));

        Assert.assertTrue(evaluator.evaluate(vwo, new JSONArray("[0, 3, 7]")));

        Assert.assertTrue(evaluator.evaluate(vwo, new JSONArray("[0, 3, 7]")));

        Assert.assertTrue(evaluator.evaluate(vwo, new JSONArray("[0, 3, 7]")));

        Assert.assertFalse(evaluator.evaluate(vwo, new JSONArray("[0, 3, 7]")));
    }

    @Test
    @Config(qualifiers = "en-rIN")
    public void locationEqualsTest() throws JSONException {
        VWO vwo = new VWOMock().getVWOMockObject();

        CustomSegmentEvaluateEnum.EvaluateSegment evaluator =
                CustomSegmentEvaluateEnum.getEvaluator(AppConstants.LOCATION, AppConstants.EQUAL_TO);

        Assert.assertTrue(evaluator.evaluate(vwo, new JSONArray("[\n" +
                "\"AF\",\n" +
                "\"AI\",\n" +
                "\"AQ\",\n" +
                "\"IN\",\n" +
                "\"SR\"\n" +
                "]")));
    }

    @Test
    @Config(qualifiers = "en-rUS")
    public void locationNotEqualsTest() throws JSONException {
        VWO vwo = new VWOMock().getVWOMockObject();

        CustomSegmentEvaluateEnum.EvaluateSegment evaluator =
                CustomSegmentEvaluateEnum.getEvaluator(AppConstants.LOCATION, AppConstants.NOT_EQUAL_TO);

        Assert.assertTrue(evaluator.evaluate(vwo, new JSONArray("[\n" +
                "\"AF\",\n" +
                "\"AI\",\n" +
                "\"AQ\",\n" +
                "\"IN\",\n" +
                "\"SR\"\n" +
                "]")));
    }

    @Test
    @Config(qualifiers = "en-rUS-w320dp-h240dp-xxxhdpi")
    public void phoneUserEqualsTest() throws JSONException {
        VWO vwo = new VWOMock().getVWOMockObject();

        CustomSegmentEvaluateEnum.EvaluateSegment evaluator =
                CustomSegmentEvaluateEnum.getEvaluator(AppConstants.DEVICE_TYPE, AppConstants.EQUAL_TO);

        Assert.assertTrue(evaluator.evaluate(vwo, new JSONArray("[\"Phone\"]")));
    }

    @Test
    @Config(qualifiers = "en-rUS-w480dp-h640dp-xxxhdpi")
    public void phoneUserNotEqualsTest() throws JSONException {
        VWO vwo = new VWOMock().getVWOMockObject();

        CustomSegmentEvaluateEnum.EvaluateSegment evaluator =
                CustomSegmentEvaluateEnum.getEvaluator(AppConstants.DEVICE_TYPE, AppConstants.NOT_EQUAL_TO);

        Assert.assertTrue(evaluator.evaluate(vwo, new JSONArray("[\"Phone\"]")));
    }

    @Test
    @Config(qualifiers = "en-rUS-w480dp-h640dp-xxxhdpi")
    public void tabletUserEqualsTest() throws JSONException {
        VWO vwo = new VWOMock().getVWOMockObject();

        CustomSegmentEvaluateEnum.EvaluateSegment evaluator =
                CustomSegmentEvaluateEnum.getEvaluator(AppConstants.DEVICE_TYPE, AppConstants.EQUAL_TO);

        Assert.assertTrue(evaluator.evaluate(vwo, new JSONArray("[\"Tablet\"]")));
    }

    @Test
    @Config(qualifiers = "en-rUS-w320dp-h240dp-xxxhdpi")
    public void tabletUserNotEqualsTest() throws JSONException {
        VWO vwo = new VWOMock().getVWOMockObject();

        CustomSegmentEvaluateEnum.EvaluateSegment evaluator =
                CustomSegmentEvaluateEnum.getEvaluator(AppConstants.DEVICE_TYPE, AppConstants.NOT_EQUAL_TO);

        Assert.assertTrue(evaluator.evaluate(vwo, new JSONArray("[\"Tablet\"]")));
    }

    @Test
    @PrepareForTest(VWOPersistData.class)
    public void newUserEqualsTest() throws JSONException {
        VWO vwo = new VWOMock().getVWOMockObject();

        PowerMockito.mockStatic(VWOPersistData.class);
        PowerMockito.when(VWOPersistData.isReturningUser(ArgumentMatchers.any(VWO.class))).thenReturn(false);

        CustomSegmentEvaluateEnum.EvaluateSegment evaluator =
                CustomSegmentEvaluateEnum.getEvaluator(AppConstants.USER_TYPE, AppConstants.EQUAL_TO);

        Assert.assertTrue(evaluator.evaluate(vwo, new JSONArray("[\"new\"]")));
    }

    @Test
    @PrepareForTest(VWOPersistData.class)
    public void newUserNotEqualsTest() throws JSONException {
        VWO vwo = new VWOMock().getVWOMockObject();

        PowerMockito.mockStatic(VWOPersistData.class);
        PowerMockito.when(VWOPersistData.isReturningUser(ArgumentMatchers.any(VWO.class))).thenReturn(true);

        CustomSegmentEvaluateEnum.EvaluateSegment evaluator =
                CustomSegmentEvaluateEnum.getEvaluator(AppConstants.USER_TYPE, AppConstants.NOT_EQUAL_TO);

        Assert.assertTrue(evaluator.evaluate(vwo, new JSONArray("[\"new\"]")));
    }

    @Test
    @PrepareForTest(VWOPersistData.class)
    public void returningUserEqualsTest() throws JSONException {
        VWO vwo = new VWOMock().getVWOMockObject();

        PowerMockito.mockStatic(VWOPersistData.class);
        PowerMockito.when(VWOPersistData.isReturningUser(ArgumentMatchers.any(VWO.class))).thenReturn(true);

        CustomSegmentEvaluateEnum.EvaluateSegment evaluator =
                CustomSegmentEvaluateEnum.getEvaluator(AppConstants.USER_TYPE, AppConstants.EQUAL_TO);

        Assert.assertTrue(evaluator.evaluate(vwo, new JSONArray("[\"ret\"]")));
    }

    @Test
    @PrepareForTest(VWOPersistData.class)
    public void returningUserNotEqualsTest() throws JSONException {
        VWO vwo = new VWOMock().getVWOMockObject();

        PowerMockito.mockStatic(VWOPersistData.class);
        PowerMockito.when(VWOPersistData.isReturningUser(ArgumentMatchers.any(VWO.class))).thenReturn(false);

        CustomSegmentEvaluateEnum.EvaluateSegment evaluator =
                CustomSegmentEvaluateEnum.getEvaluator(AppConstants.USER_TYPE, AppConstants.NOT_EQUAL_TO);

        Assert.assertTrue(evaluator.evaluate(vwo, new JSONArray("[\"ret\"]")));
    }

    @Test
    @PrepareForTest(VWOPersistData.class)
    @Config(qualifiers = "en-rUS-w320dp-h240dp-xxhdpi")
    public void screenWidthEqualsTest() throws JSONException {  // 320 * 3.0 = 960  xxhdpi is of the ratio 1:3.0
        VWO vwo = new VWOMock().getVWOMockObject();

        PowerMockito.mockStatic(VWOPersistData.class);
        PowerMockito.when(VWOPersistData.isReturningUser(ArgumentMatchers.any(VWO.class))).thenReturn(false);

        CustomSegmentEvaluateEnum.EvaluateSegment evaluator =
                CustomSegmentEvaluateEnum.getEvaluator(AppConstants.SCREEN_WIDTH, AppConstants.EQUAL_TO);

        Assert.assertTrue(evaluator.evaluate(vwo, new JSONArray("[960]")));
    }

    @Test
    @PrepareForTest(VWOPersistData.class)
    @Config(qualifiers = "en-rUS-w320dp-h240dp-xxxhdpi")
    public void screenWidthNotEqualsTest() throws JSONException {  // 320 * 4.0 = 1280  xxxhdpi is of the ratio 1:4.0
        VWO vwo = new VWOMock().getVWOMockObject();

        PowerMockito.mockStatic(VWOPersistData.class);
        PowerMockito.when(VWOPersistData.isReturningUser(ArgumentMatchers.any(VWO.class))).thenReturn(false);

        CustomSegmentEvaluateEnum.EvaluateSegment evaluator =
                CustomSegmentEvaluateEnum.getEvaluator(AppConstants.SCREEN_WIDTH, AppConstants.NOT_EQUAL_TO);

        Assert.assertTrue(evaluator.evaluate(vwo, new JSONArray("[960]")));
    }

    @Test
    @PrepareForTest(VWOPersistData.class)
    @Config(qualifiers = "en-rUS-w480dp-h240dp-ldpi")
    public void screenWidthLessThanTest() throws JSONException {  // 480 * 0.75 = 360  ldpi is of the ratio 1:0.75
        VWO vwo = new VWOMock().getVWOMockObject();

        PowerMockito.mockStatic(VWOPersistData.class);
        PowerMockito.when(VWOPersistData.isReturningUser(ArgumentMatchers.any(VWO.class))).thenReturn(false);

        CustomSegmentEvaluateEnum.EvaluateSegment evaluator =
                CustomSegmentEvaluateEnum.getEvaluator(AppConstants.SCREEN_WIDTH, AppConstants.LESS_THAN);

        Assert.assertTrue(evaluator.evaluate(vwo, new JSONArray("[960]")));
    }

    @Test
    @PrepareForTest(VWOPersistData.class)
    @Config(qualifiers = "en-rUS-w480dp-h240dp-xxxhdpi")
    public void screenWidthGreaterThanTest() throws JSONException {  // 480 * 4.0 = 1920  xxxhdpi is of the ratio 1:4.0
        VWO vwo = new VWOMock().getVWOMockObject();

        PowerMockito.mockStatic(VWOPersistData.class);
        PowerMockito.when(VWOPersistData.isReturningUser(ArgumentMatchers.any(VWO.class))).thenReturn(false);

        CustomSegmentEvaluateEnum.EvaluateSegment evaluator =
                CustomSegmentEvaluateEnum.getEvaluator(AppConstants.SCREEN_WIDTH, AppConstants.GREATER_THAN);

        Assert.assertTrue(evaluator.evaluate(vwo, new JSONArray("[960]")));
    }

    @Test
    @PrepareForTest(VWOPersistData.class)
    @Config(qualifiers = "en-rUS-w480dp-h240dp-xxxhdpi")
    public void invalidScreenWidthTest() throws JSONException {  // 480 * 4.0 = 1920  xxxhdpi is of the ratio 1:4.0
        VWO vwo = new VWOMock().getVWOMockObject();

        PowerMockito.mockStatic(VWOPersistData.class);
        PowerMockito.when(VWOPersistData.isReturningUser(ArgumentMatchers.any(VWO.class))).thenReturn(false);

        CustomSegmentEvaluateEnum.EvaluateSegment evaluator =
                CustomSegmentEvaluateEnum.getEvaluator(AppConstants.SCREEN_WIDTH, AppConstants.GREATER_THAN);

        Assert.assertFalse(evaluator.evaluate(vwo, new JSONArray("[\"960dp\"]")));
    }

    @Test
    @PrepareForTest(VWOPersistData.class)
    @Config(qualifiers = "en-rUS-w480dp-h320dp-xxhdpi")
    public void screenHeightEqualsTest() throws JSONException {  // 320 * 3.0 = 960  xxhdpi is of the ratio 1:3.0
        VWO vwo = new VWOMock().getVWOMockObject();

        PowerMockito.mockStatic(VWOPersistData.class);
        PowerMockito.when(VWOPersistData.isReturningUser(ArgumentMatchers.any(VWO.class))).thenReturn(false);

        CustomSegmentEvaluateEnum.EvaluateSegment evaluator =
                CustomSegmentEvaluateEnum.getEvaluator(AppConstants.SCREEN_HEIGHT, AppConstants.EQUAL_TO);

        Assert.assertTrue(evaluator.evaluate(vwo, new JSONArray("[960]")));
    }

    @Test
    @PrepareForTest(VWOPersistData.class)
    @Config(qualifiers = "en-rUS-w320dp-h320dp-xxxhdpi")
    public void screenHeightNotEqualsTest() throws JSONException {  // 320 * 4.0 = 1280  xxxhdpi is of the ratio 1:4.0
        VWO vwo = new VWOMock().getVWOMockObject();

        PowerMockito.mockStatic(VWOPersistData.class);
        PowerMockito.when(VWOPersistData.isReturningUser(ArgumentMatchers.any(VWO.class))).thenReturn(false);

        CustomSegmentEvaluateEnum.EvaluateSegment evaluator =
                CustomSegmentEvaluateEnum.getEvaluator(AppConstants.SCREEN_HEIGHT, AppConstants.NOT_EQUAL_TO);

        Assert.assertTrue(evaluator.evaluate(vwo, new JSONArray("[960]")));
    }

    @Test
    @PrepareForTest(VWOPersistData.class)
    @Config(qualifiers = "en-rUS-w320dp-h480dp-ldpi")
    public void screenHeightLessThanTest() throws JSONException {  // 480 * 0.75 = 360  ldpi is of the ratio 1:0.75
        VWO vwo = new VWOMock().getVWOMockObject();

        PowerMockito.mockStatic(VWOPersistData.class);
        PowerMockito.when(VWOPersistData.isReturningUser(ArgumentMatchers.any(VWO.class))).thenReturn(false);

        CustomSegmentEvaluateEnum.EvaluateSegment evaluator =
                CustomSegmentEvaluateEnum.getEvaluator(AppConstants.SCREEN_HEIGHT, AppConstants.LESS_THAN);

        Assert.assertTrue(evaluator.evaluate(vwo, new JSONArray("[960]")));
    }

    @Test
    @PrepareForTest(VWOPersistData.class)
    @Config(qualifiers = "en-rUS-w240dp-h480dp-xxxhdpi")
    public void screenHeightGreaterThanTest() throws JSONException {  // 480 * 4.0 = 1920  xxxhdpi is of the ratio 1:4.0
        VWO vwo = new VWOMock().getVWOMockObject();

        PowerMockito.mockStatic(VWOPersistData.class);
        PowerMockito.when(VWOPersistData.isReturningUser(ArgumentMatchers.any(VWO.class))).thenReturn(false);

        CustomSegmentEvaluateEnum.EvaluateSegment evaluator =
                CustomSegmentEvaluateEnum.getEvaluator(AppConstants.SCREEN_HEIGHT, AppConstants.GREATER_THAN);

        Assert.assertTrue(evaluator.evaluate(vwo, new JSONArray("[960]")));
    }

    @Test
    @PrepareForTest(VWOPersistData.class)
    @Config(qualifiers = "en-rUS-w240dp-h480dp-xxxhdpi")
    public void invalidScreenHeightTest() throws JSONException {  // 480 * 4.0 = 1920  xxxhdpi is of the ratio 1:4.0
        VWO vwo = new VWOMock().getVWOMockObject();

        PowerMockito.mockStatic(VWOPersistData.class);
        PowerMockito.when(VWOPersistData.isReturningUser(ArgumentMatchers.any(VWO.class))).thenReturn(false);

        CustomSegmentEvaluateEnum.EvaluateSegment evaluator =
                CustomSegmentEvaluateEnum.getEvaluator(AppConstants.SCREEN_HEIGHT, AppConstants.GREATER_THAN);

        Assert.assertFalse(evaluator.evaluate(vwo, new JSONArray("[\"960dp\"]")));
    }

    @Test
    @PrepareForTest(VWOPersistData.class)
    @Config(qualifiers = "en-rUS-w480dp-h320dp-xxhdpi")
    public void scaleEqualsTest() throws JSONException {  // for xxhdpi scale = 3.0
        VWO vwo = new VWOMock().getVWOMockObject();

        PowerMockito.mockStatic(VWOPersistData.class);
        PowerMockito.when(VWOPersistData.isReturningUser(ArgumentMatchers.any(VWO.class))).thenReturn(false);

        CustomSegmentEvaluateEnum.EvaluateSegment evaluator =
                CustomSegmentEvaluateEnum.getEvaluator(AppConstants.SCALE, AppConstants.EQUAL_TO);

        Assert.assertTrue(evaluator.evaluate(vwo, new JSONArray("[3.0]")));
    }

    @Test
    @PrepareForTest(VWOPersistData.class)
    @Config(qualifiers = "en-rUS-w320dp-h320dp-xxxhdpi")
    public void scaleNotEqualsTest() throws JSONException {  // for xxxhdpi scale = 4.0
        VWO vwo = new VWOMock().getVWOMockObject();

        PowerMockito.mockStatic(VWOPersistData.class);
        PowerMockito.when(VWOPersistData.isReturningUser(ArgumentMatchers.any(VWO.class))).thenReturn(false);

        CustomSegmentEvaluateEnum.EvaluateSegment evaluator =
                CustomSegmentEvaluateEnum.getEvaluator(AppConstants.SCALE, AppConstants.NOT_EQUAL_TO);

        Assert.assertTrue(evaluator.evaluate(vwo, new JSONArray("[3.0]")));
    }

    @Test
    @PrepareForTest(VWOPersistData.class)
    @Config(qualifiers = "en-rUS-w320dp-h480dp-ldpi")
    public void scaleLessThanTest() throws JSONException {  // for ldpi scale = 0.75
        VWO vwo = new VWOMock().getVWOMockObject();

        PowerMockito.mockStatic(VWOPersistData.class);
        PowerMockito.when(VWOPersistData.isReturningUser(ArgumentMatchers.any(VWO.class))).thenReturn(false);

        CustomSegmentEvaluateEnum.EvaluateSegment evaluator =
                CustomSegmentEvaluateEnum.getEvaluator(AppConstants.SCALE, AppConstants.LESS_THAN);

        Assert.assertTrue(evaluator.evaluate(vwo, new JSONArray("[3.0]")));
    }

    @Test
    @PrepareForTest(VWOPersistData.class)
    @Config(qualifiers = "en-rUS-w240dp-h480dp-xxxhdpi")
    public void scaleGreaterThanTest() throws JSONException { // for xxxhdpi scale = 4.0
        VWO vwo = new VWOMock().getVWOMockObject();

        PowerMockito.mockStatic(VWOPersistData.class);
        PowerMockito.when(VWOPersistData.isReturningUser(ArgumentMatchers.any(VWO.class))).thenReturn(false);

        CustomSegmentEvaluateEnum.EvaluateSegment evaluator =
                CustomSegmentEvaluateEnum.getEvaluator(AppConstants.SCALE, AppConstants.GREATER_THAN);

        Assert.assertTrue(evaluator.evaluate(vwo, new JSONArray("[2.0]")));
    }

    @Test
    @PrepareForTest(VWOPersistData.class)
    @Config(qualifiers = "en-rUS-w240dp-h480dp-xxxhdpi")
    public void invalidScaleTest() throws JSONException { // for xxxhdpi scale = 4.0
        VWO vwo = new VWOMock().getVWOMockObject();

        PowerMockito.mockStatic(VWOPersistData.class);
        PowerMockito.when(VWOPersistData.isReturningUser(ArgumentMatchers.any(VWO.class))).thenReturn(false);

        CustomSegmentEvaluateEnum.EvaluateSegment evaluator =
                CustomSegmentEvaluateEnum.getEvaluator(AppConstants.SCALE, AppConstants.GREATER_THAN);

        Assert.assertFalse(evaluator.evaluate(vwo, new JSONArray("[\"2.0px\"]")));
    }

    @Test
    public void invalidSegmentTest() throws JSONException {
        VWO vwo = new VWOMock().getVWOMockObject();

        CustomSegmentEvaluateEnum.EvaluateSegment evaluatorUpperBound =
                CustomSegmentEvaluateEnum.getEvaluator("8", AppConstants.CONTAINS);

        Assert.assertFalse(evaluatorUpperBound.evaluate(vwo, new JSONArray("[\"2\"]")));

        CustomSegmentEvaluateEnum.EvaluateSegment evaluatorLowerBound =
                CustomSegmentEvaluateEnum.getEvaluator("0", AppConstants.CONTAINS);
        Assert.assertFalse(evaluatorLowerBound.evaluate(vwo, new JSONArray("[\"2\"]")));
    }

    @Test
    public void invalidOperatorTest() throws JSONException {
        VWO vwo = new VWOMock().getVWOMockObject();

        CustomSegmentEvaluateEnum.EvaluateSegment evaluatorLowerBound =
                CustomSegmentEvaluateEnum.getEvaluator(AppConstants.ANDROID_VERSION, -1);

        Assert.assertFalse(evaluatorLowerBound.evaluate(vwo, new JSONArray("[\"2\"]")));

        CustomSegmentEvaluateEnum.EvaluateSegment evaluatorUpperBound =
                CustomSegmentEvaluateEnum.getEvaluator(AppConstants.ANDROID_VERSION, 200);

        Assert.assertFalse(evaluatorUpperBound.evaluate(vwo, new JSONArray("[\"2\"]")));
    }
}
