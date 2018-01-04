package com.vwo.mobile.utils;

import com.vwo.mobile.BuildConfig;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

/**
 * Created by aman on Sat 14/10/17 22:11.
 */

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Config.ALL_SDKS)
public class VWOUtilsTest {

    @Test
    public void testValidateApiKey() {
        Assert.assertEquals(VWOUtils.isValidVwoAppKey("ab123c12def23234"), false);
        Assert.assertEquals(VWOUtils.isValidVwoAppKey("aamk23sjcnd-1234"), false);
        Assert.assertEquals(VWOUtils.isValidVwoAppKey("abcnhdz3424234rh348ghginlaie7te4-23423"), true);
        Assert.assertEquals(VWOUtils.isValidVwoAppKey(""), false);
        Assert.assertEquals(VWOUtils.isValidVwoAppKey(null), false);
    }
}
