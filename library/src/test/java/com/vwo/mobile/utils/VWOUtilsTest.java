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
@Config(constants = BuildConfig.class, sdk = 22)
public class VWOUtilsTest {

    @Test
    public void testValidateApiKey() {
        Assert.assertFalse(VWOUtils.isValidVwoAppKey("ab123c12def23234"));
        Assert.assertFalse(VWOUtils.isValidVwoAppKey("aamk23sjcnd-1234"));
        Assert.assertTrue(VWOUtils.isValidVwoAppKey("abcnhdz3424234rh348ghginlaie7te4-23423"));
        Assert.assertFalse(VWOUtils.isValidVwoAppKey(""));
        Assert.assertFalse(VWOUtils.isValidVwoAppKey(null));
        Assert.assertFalse(VWOUtils.isValidVwoAppKey("-123"));
        Assert.assertFalse(VWOUtils.isValidVwoAppKey("abc-"));
        Assert.assertFalse(VWOUtils.isValidVwoAppKey("abcnhdz3424234rh348ghginlaie7te4-"));
        Assert.assertFalse(VWOUtils.isValidVwoAppKey("-"));
        Assert.assertFalse(VWOUtils.isValidVwoAppKey("abcnhdz3424234rh348ghginlaie7te4-abc"));
    }
}
