package com.vwo.mobile.segmentation;

import com.vwo.mobile.BuildConfig;
import com.vwo.mobile.TestUtils;
import com.vwo.mobile.VWO;
import com.vwo.mobile.data.VWOPersistData;
import com.vwo.mobile.mock.ShadowConfiguration;
import com.vwo.mobile.mock.VWOMock;
import com.vwo.mobile.mock.VWOPersistDataMock;

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

import java.io.IOException;

/**
 * Created by aman on Tue 19/12/17 11:18.
 */

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 22, shadows = {
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
    public void phoneUserTest() throws JSONException, IOException {
        VWO vwo = new VWOMock().getVWOMockObject();

        String phoneUser = TestUtils.readJsonFile(getClass(), "com/vwo/mobile/segmentation/phone_user.json");

        PredefinedSegment segmentPhoneUser = new PredefinedSegment(vwo, new JSONObject(phoneUser));
        Assert.assertTrue(segmentPhoneUser.evaluate());
    }

    @Test
    @Config(shadows = {ShadowConfiguration.class,
            VWOPersistDataMock.class})
    public void tabletUserTest() throws JSONException, IOException {
        VWO vwo = new VWOMock().getVWOMockObject();
        String tabletUser = TestUtils.readJsonFile(getClass(), "com/vwo/mobile/segmentation/tablet_user.json");

        PredefinedSegment segmentTabletUser = new PredefinedSegment(vwo, new JSONObject(tabletUser));
        Assert.assertTrue(segmentTabletUser.evaluate());
    }

    @Test
    public void newUserTest() throws JSONException, IOException {
        VWO vwo = new VWOMock().getVWOMockObject();

        PowerMockito.mockStatic(VWOPersistData.class);
        PowerMockito.when(VWOPersistData.isReturningUser(ArgumentMatchers.any(VWO.class))).thenReturn(false);

        String newUser = TestUtils.readJsonFile(getClass(), "com/vwo/mobile/segmentation/new_user.json");

        PredefinedSegment segmentNewUser = new PredefinedSegment(vwo, new JSONObject(newUser));
        Assert.assertTrue(segmentNewUser.evaluate());
    }

    @Test
    public void returningUserTest() throws JSONException, IOException {
        VWO vwo = new VWOMock().getVWOMockObject();

        PowerMockito.mockStatic(VWOPersistData.class);
        PowerMockito.when(VWOPersistData.isReturningUser(ArgumentMatchers.any(VWO.class))).thenReturn(true);

        String returningUser = TestUtils.readJsonFile(getClass(), "com/vwo/mobile/segmentation/returning_user.json");


        PredefinedSegment segmentReturningUser = new PredefinedSegment(vwo, new JSONObject(returningUser));
        Assert.assertEquals(segmentReturningUser.evaluate(), true);
    }
}
