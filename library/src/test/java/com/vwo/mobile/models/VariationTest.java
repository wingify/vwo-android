package com.vwo.mobile.models;

import com.vwo.mobile.BuildConfig;
import com.vwo.mobile.TestUtils;
import com.vwo.mobile.mock.ShadowVWOLog;

import junit.framework.Assert;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.IOException;

/**
 * Created by aman on Thu 04/01/18 13:06.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 22, shadows = {ShadowVWOLog.class})
public class VariationTest {

    @Test
    public void variationTest() throws JSONException, IOException {
        String variationData = TestUtils.readJsonFile(getClass(), "com/vwo/mobile/models/variation.json");
        Variation variation = new Variation(new JSONObject(variationData));

        Assert.assertEquals(variation.getId(), 2);

        Assert.assertTrue(variation.hasKey("layout"));
        Assert.assertTrue(variation.hasKey("socialMedia"));

        Assert.assertFalse(variation.hasKey("android"));

        Assert.assertEquals(variation.getKey("layout"), "grid");
        Assert.assertEquals(variation.getKey("socialMedia"), true);
        Assert.assertNull(variation.getKey("android"));
    }
}
