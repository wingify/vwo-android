package com.vwo.mobile;


import com.vwo.mobile.mock.ShadowHandler;
import com.vwo.mobile.mock.ShadowVWODownloader;
import com.vwo.mobile.mock.ShadowVWOLog;
import com.vwo.mobile.mock.ShadowVWOUtils;
import com.vwo.mobile.mock.VWOMock;
import com.vwo.mobile.mock.VWOPersistDataShadow;
import com.vwo.mobile.utils.VWOPreference;
import com.vwo.mobile.utils.VWOUrlBuilder;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 21, shadows = {VWOPersistDataShadow.class, ShadowVWOLog.class, ShadowVWOUtils.class,
        ShadowVWODownloader.class, ShadowHandler.class}, manifest = "AndroidManifest.xml")
public class VWOCDNTest {


    @Rule
    public ExpectedException expectedException = ExpectedException.none();



    @Test
    public void chinaCDNConfigTruthyTest() {
        VWO vwo = new VWOMock().getVWOMockObject();
        Mockito.when(vwo.getConfig())
                .thenReturn(new VWOConfig.Builder().apiKey("abc-123")
                        .setOptOut(false)
                        .userID("userId")
                        .isChinaCDN(true)
                        .build());
        Mockito.when(vwo.getVwoPreference())
                .thenReturn(new VWOPreference(RuntimeEnvironment.application.getApplicationContext()));
        VWOUrlBuilder vwoUrlBuilder = new VWOUrlBuilder(vwo);

        Assert.assertTrue(vwoUrlBuilder.getDownloadUrl().contains(BuildConfig.CHINA_DACDN_URL));
        Assert.assertFalse(vwoUrlBuilder.getDownloadUrl().contains(BuildConfig.DACDN_URL));

        Assert.assertTrue(vwoUrlBuilder.getCampaignUrl(12, 2).contains(BuildConfig.CHINA_DACDN_URL));
        Assert.assertFalse(vwoUrlBuilder.getCampaignUrl(12, 2).contains(BuildConfig.DACDN_URL));

        Assert.assertTrue(vwoUrlBuilder.getGoalUrl( 12, 2, 3).contains(BuildConfig.CHINA_DACDN_URL));
        Assert.assertFalse(vwoUrlBuilder.getGoalUrl(12, 2, 3).contains(BuildConfig.DACDN_URL));

        Assert.assertTrue(vwoUrlBuilder.getCustomDimensionUrl("key", "value").contains(BuildConfig.CHINA_DACDN_URL));
        Assert.assertFalse(vwoUrlBuilder.getCustomDimensionUrl("key", "value").contains(BuildConfig.DACDN_URL));

        Assert.assertTrue(vwoUrlBuilder.getLoggingUrl().contains(BuildConfig.CHINA_DACDN_URL));
        Assert.assertFalse(vwoUrlBuilder.getLoggingUrl().contains(BuildConfig.DACDN_URL));
        vwoUrlBuilder = null;
    }

    @Test
    public void chinaCDNConfigFalsyTest() {
        VWO vwo = new VWOMock().getVWOMockObject();
        Mockito.when(vwo.getConfig())
                .thenReturn(new VWOConfig.Builder().apiKey("abc-123")
                        .setOptOut(false)
                        .userID("userId")
                        .isChinaCDN(false)
                        .build());
        Mockito.when(vwo.getVwoPreference())
                .thenReturn(new VWOPreference(RuntimeEnvironment.application.getApplicationContext()));
        VWOUrlBuilder vwoUrlBuilder = new VWOUrlBuilder(vwo);

        Assert.assertFalse(vwoUrlBuilder.getDownloadUrl().contains(BuildConfig.CHINA_DACDN_URL));
        Assert.assertTrue(vwoUrlBuilder.getDownloadUrl().contains(BuildConfig.DACDN_URL));

        Assert.assertFalse(vwoUrlBuilder.getCampaignUrl(12, 2).contains(BuildConfig.CHINA_DACDN_URL));
        Assert.assertTrue(vwoUrlBuilder.getCampaignUrl(12, 2).contains(BuildConfig.DACDN_URL));

        Assert.assertFalse(vwoUrlBuilder.getGoalUrl( 12, 2, 3).contains(BuildConfig.CHINA_DACDN_URL));
        Assert.assertTrue(vwoUrlBuilder.getGoalUrl(12, 2, 3).contains(BuildConfig.DACDN_URL));

        Assert.assertFalse(vwoUrlBuilder.getCustomDimensionUrl("key", "value").contains(BuildConfig.CHINA_DACDN_URL));
        Assert.assertTrue(vwoUrlBuilder.getCustomDimensionUrl("key", "value").contains(BuildConfig.DACDN_URL));

        Assert.assertFalse(vwoUrlBuilder.getLoggingUrl().contains(BuildConfig.CHINA_DACDN_URL));
        Assert.assertTrue(vwoUrlBuilder.getLoggingUrl().contains(BuildConfig.DACDN_URL));
    }

    @Test
    public void noChinaCDNConfigTest() {
        VWO vwo = new VWOMock().getVWOMockObject();
        Mockito.when(vwo.getConfig())
                .thenReturn(new VWOConfig.Builder().apiKey("abc-123")
                        .setOptOut(false)
                        .userID("userId")
                        .build());
        Mockito.when(vwo.getVwoPreference())
                .thenReturn(new VWOPreference(RuntimeEnvironment.application.getApplicationContext()));
        VWOUrlBuilder vwoUrlBuilder = new VWOUrlBuilder(vwo);

        Assert.assertFalse(vwoUrlBuilder.getDownloadUrl().contains(BuildConfig.CHINA_DACDN_URL));
        Assert.assertTrue(vwoUrlBuilder.getDownloadUrl().contains(BuildConfig.DACDN_URL));

        Assert.assertFalse(vwoUrlBuilder.getCampaignUrl(12, 2).contains(BuildConfig.CHINA_DACDN_URL));
        Assert.assertTrue(vwoUrlBuilder.getCampaignUrl(12, 2).contains(BuildConfig.DACDN_URL));

        Assert.assertFalse(vwoUrlBuilder.getGoalUrl( 12, 2, 3).contains(BuildConfig.CHINA_DACDN_URL));
        Assert.assertTrue(vwoUrlBuilder.getGoalUrl(12, 2, 3).contains(BuildConfig.DACDN_URL));

        Assert.assertFalse(vwoUrlBuilder.getCustomDimensionUrl("key", "value").contains(BuildConfig.CHINA_DACDN_URL));
        Assert.assertTrue(vwoUrlBuilder.getCustomDimensionUrl("key", "value").contains(BuildConfig.DACDN_URL));

        Assert.assertFalse(vwoUrlBuilder.getLoggingUrl().contains(BuildConfig.CHINA_DACDN_URL));
        Assert.assertTrue(vwoUrlBuilder.getLoggingUrl().contains(BuildConfig.DACDN_URL));
    }
}
