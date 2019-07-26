package com.vwo.mobile.utils;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

/**
 * Created by aman on Sat 14/10/17 22:11.
 */

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 22)
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

    @Test
    @Config(qualifiers = "en-rIN")
    public void countryCodeTestIndia() {
        Assert.assertEquals("IN", VWOUtils.getDeviceCountryCode(RuntimeEnvironment.application.getApplicationContext()));
        Assert.assertNotSame("US", VWOUtils.getDeviceCountryCode(RuntimeEnvironment.application.getApplicationContext()));
    }

    @Test
    @Config(qualifiers = "ar-rAE-ldrtl")
    public void countryCodeTestUnitedArab() {
        Assert.assertEquals("AE", VWOUtils.getDeviceCountryCode(RuntimeEnvironment.application.getApplicationContext()));
        Assert.assertNotSame("US", VWOUtils.getDeviceCountryCode(RuntimeEnvironment.application.getApplicationContext()));
    }

    @Test
    @Config(qualifiers = "en-rUS-w320dp-h240dp-ldpi")
    public void screenWidthLdpiTest() {
        Assert.assertEquals(240, VWOUtils.getScreenWidth());     // 320 * 0.75 = 320  ldpi is 1:0.75 ratio
    }

    @Test
    @Config(qualifiers = "en-rUS-w320dp-h240dp-mdpi")
    public void screenWidthMdpiTest() {
        Assert.assertEquals(320, VWOUtils.getScreenWidth());     // 320 * 1 = 320  mdpi is 1:1 ratio
    }

    @Test
    @Config(qualifiers = "en-rUS-w320dp-h240dp-hdpi")
    public void screenWidthHdpiTest() {
        Assert.assertEquals(480, VWOUtils.getScreenWidth());     // 320 * 1.5 = 480  hdpi is 1:1.5 ratio
    }

    @Test
    @Config(qualifiers = "en-rUS-w320dp-h240dp-xhdpi")
    public void screenWidthXhdpiTest() {
        Assert.assertEquals(640, VWOUtils.getScreenWidth());     // 320 * 2.0 = 640  xhdpi is 1:2.0 ratio
    }

    @Test
    @Config(qualifiers = "en-rUS-w320dp-h240dp-xxhdpi")
    public void screenWidthXxhdpiTest() {
        Assert.assertEquals(960, VWOUtils.getScreenWidth());     // 320 * 3.0 = 960  xxhdpi is 1:3.0 ratio
    }

    @Test
    @Config(qualifiers = "en-rUS-w320dp-h240dp-xxxhdpi")
    public void screenWidthXxxhdpiTest() {
        Assert.assertEquals(1280, VWOUtils.getScreenWidth());     // 320 * 4.0 = 1280  xxxhdpi is 1:4.0 ratio
    }


    @Test
    @Config(qualifiers = "en-rUS-w240dp-h320dp-ldpi")
    public void screenHeightLdpiTest() {
        Assert.assertEquals(240, VWOUtils.getScreenHeight());     // 320 * 0.75 = 320  ldpi is 1:0.75 ratio
    }

    @Test
    @Config(qualifiers = "en-rUS-w240dp-h320dp-mdpi")
    public void screenHeightMdpiTest() {
        Assert.assertEquals(320, VWOUtils.getScreenHeight());     // 320 * 1 = 320  mdpi is 1:1 ratio
    }

    @Test
    @Config(qualifiers = "en-rUS-w240dp-h320dp-hdpi")
    public void screenHeightHdpiTest() {
        Assert.assertEquals(480, VWOUtils.getScreenHeight());     // 320 * 1.5 = 480  hdpi is 1:1.5 ratio
    }

    @Test
    @Config(qualifiers = "en-rUS-w240dp-h320dp-xhdpi")
    public void screenHeightXhdpiTest() {
        Assert.assertEquals(640, VWOUtils.getScreenHeight());     // 320 * 2.0 = 640  xhdpi is 1:2.0 ratio
    }

    @Test
    @Config(qualifiers = "en-rUS-w240dp-h320dp-xxhdpi")
    public void screenHeightXxhdpiTest() {
        Assert.assertEquals(960, VWOUtils.getScreenHeight());     // 320 * 3.0 = 960  xxhdpi is 1:3.0 ratio
    }

    @Test
    @Config(qualifiers = "en-rUS-w240dp-h320dp-xxxhdpi")
    public void screenHeightXxxhdpiTest() {
        Assert.assertEquals(1280, VWOUtils.getScreenHeight());     // 320 * 4.0 = 1280  xxxhdpi is 1:4.0 ratio
    }




    @Test
    @Config(qualifiers = "en-rUS-w240dp-h320dp-ldpi")
    public void scaleLdpiTest() {
        Assert.assertEquals(0.75, VWOUtils.getScale(RuntimeEnvironment.application.getApplicationContext()));
    }

    @Test
    @Config(qualifiers = "en-rUS-w240dp-h320dp-mdpi")
    public void scaleMdpiTest() {
        Assert.assertEquals(1.0, VWOUtils.getScale(RuntimeEnvironment.application.getApplicationContext()));
    }

    @Test
    @Config(qualifiers = "en-rUS-w240dp-h320dp-hdpi")
    public void scaleHdpiTest() {
        Assert.assertEquals(1.5, VWOUtils.getScale(RuntimeEnvironment.application.getApplicationContext()));
    }

    @Test
    @Config(qualifiers = "en-rUS-w240dp-h320dp-xhdpi")
    public void scaleXhdpiTest() {
        Assert.assertEquals(2.0, VWOUtils.getScale(RuntimeEnvironment.application.getApplicationContext()));
    }

    @Test
    @Config(qualifiers = "en-rUS-w240dp-h320dp-xxhdpi")
    public void scaleXxhdpiTest() {
        Assert.assertEquals(3.0, VWOUtils.getScale(RuntimeEnvironment.application.getApplicationContext()));
    }

    @Test
    @Config(qualifiers = "en-rUS-w240dp-h320dp-xxxhdpi")
    public void scaleXxxhdpiTest() {
        Assert.assertEquals(4.0, VWOUtils.getScale(RuntimeEnvironment.application.getApplicationContext()));
    }

    @Test
    public void toMD5HashTest() {
        Assert.assertEquals("b6a5706e37a909488da39f9ec842a22c", VWOUtils.toMD5Hash("amandeep.anguralla@wingify.com"));
        Assert.assertNotSame("b6a5706e37a909488da39f9ec842a22c", VWOUtils.toMD5Hash("Amandeep.anguralla@wingify.com"));
    }
}
