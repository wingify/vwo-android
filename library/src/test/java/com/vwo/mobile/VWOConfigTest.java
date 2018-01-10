package com.vwo.mobile;

import com.vwo.mobile.mock.VWOUtilsShadow;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

/**
 * Created by aman on Fri 05/01/18 12:14.
 */

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 22, shadows = {VWOUtilsShadow.class}, manifest = "AndroidManifest.xml")
public class VWOConfigTest {

    @Test
    public void testConfig() {
        VWOConfig vwoConfig = new VWOConfig.Builder().apiKey("abc-123")
                .setOptOut(false)
                .build();

        Assert.assertEquals(vwoConfig.getAppKey(), "abc");
        Assert.assertEquals(vwoConfig.getAccountId(), "123");
    }
}
