package com.vwo.mobile.segmentation;

import android.content.Context;

import com.vwo.mobile.TestUtils;
import com.vwo.mobile.VWO;
import com.vwo.mobile.mock.VWOMock;
import com.vwo.mobile.mock.VWOPersistDataMock;
import com.vwo.mobile.models.Campaign;
import com.vwo.mobile.utils.VWOUtils;

import junit.framework.Assert;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;

/**
 * Created by aman on Mon 08/01/18 14:28.
 */

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 21, shadows = {VWOPersistDataMock.class})
@PowerMockIgnore({"org.mockito.*", "org.robolectric.*", "android.*", "org.json.*"})
@PrepareForTest({VWOUtils.class})
public class SegmentUtilsTest {

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Test
    public void segmentUtilsTest() throws JSONException, IOException {
        VWO vwo = new VWOMock().getVWOMockObject();

        PowerMockito.mockStatic(VWOUtils.class);
        PowerMockito.when(VWOUtils.applicationVersion(any(Context.class))).thenReturn(1);
        PowerMockito.when(VWOUtils.androidVersion()).thenReturn("21");

        String data = TestUtils.readJsonFile(getClass(), "com/vwo/mobile/segmentation/campaign1.json");

        String data2 = TestUtils.readJsonFile(getClass(), "com/vwo/mobile/segmentation/campaign2.json");

        // Extra operator
        String data3 = TestUtils.readJsonFile(getClass(), "com/vwo/mobile/segmentation/campaign3.json");

        String data4 = TestUtils.readJsonFile(getClass(), "com/vwo/mobile/segmentation/campaign4.json");

        String data5 = TestUtils.readJsonFile(getClass(), "com/vwo/mobile/segmentation/campaign5.json");

        String data6 = TestUtils.readJsonFile(getClass(), "com/vwo/mobile/segmentation/campaign6.json");

        String data7 = TestUtils.readJsonFile(getClass(), "com/vwo/mobile/segmentation/campaign7.json");

        Campaign campaign = new Campaign(vwo, new JSONObject(data));
        Campaign campaign2 = new Campaign(vwo, new JSONObject(data2));
        Campaign campaign3 = new Campaign(vwo, new JSONObject(data3));
        Campaign campaign4 = new Campaign(vwo, new JSONObject(data4));
        Campaign campaign5 = new Campaign(vwo, new JSONObject(data5));
        Campaign campaign6 = new Campaign(vwo, new JSONObject(data6));
        Campaign campaign7 = new Campaign(vwo, new JSONObject(data7));

        Assert.assertTrue(SegmentUtils.evaluateSegmentation(campaign));
        Assert.assertTrue(SegmentUtils.evaluateSegmentation(campaign2));
        Assert.assertTrue(SegmentUtils.evaluateSegmentation(campaign3));
        Assert.assertTrue(SegmentUtils.evaluateSegmentation(campaign4));
        Assert.assertTrue(SegmentUtils.evaluateSegmentation(campaign5));
        Assert.assertTrue(SegmentUtils.evaluateSegmentation(campaign6));
        Assert.assertTrue(SegmentUtils.evaluateSegmentation(campaign7));
    }
}
