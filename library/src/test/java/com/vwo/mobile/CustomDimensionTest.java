package com.vwo.mobile;

import com.vwo.mobile.events.VWOStatusListener;
import com.vwo.mobile.mock.ShadowHandler;
import com.vwo.mobile.mock.ShadowVWODownloader;
import com.vwo.mobile.mock.ShadowVWOLog;
import com.vwo.mobile.mock.ShadowVWOUtils;
import com.vwo.mobile.mock.VWOMock;
import com.vwo.mobile.mock.VWOPersistDataShadow;
import com.vwo.mobile.utils.VWOPreference;
import com.vwo.mobile.utils.VWOUrlBuilder;
import com.vwo.mobile.utils.VWOUtils;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 21, shadows = {VWOPersistDataShadow.class,
        ShadowVWOLog.class,
        ShadowVWOUtils.class,
        ShadowVWODownloader.class,
        ShadowHandler.class}, manifest = "AndroidManifest.xml")
public class CustomDimensionTest {
    private static final Object lock = new Object();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private static boolean isInitialized = false;


    @Test
    public void customDimensionNullKey() {
        expectedException.expect(IllegalArgumentException.class);
        VWO.pushCustomDimension(null, "value");
    }

    @Test
    public void customDimensionEmptyKey() {
        expectedException.expect(IllegalArgumentException.class);
        VWO.pushCustomDimension("", "value");
    }

    @Test
    public void customDimensionNullValue() {
        expectedException.expect(IllegalArgumentException.class);
        VWO.pushCustomDimension("key", null);
    }

    @Test
    public void customDimensionEmptyValue() {
        expectedException.expect(IllegalArgumentException.class);
        VWO.pushCustomDimension("key", "");
    }

    @Test
    public void customDimensionSuccess() {
        VWO.pushCustomDimension("key", "value");
    }

    @Test
    public void vwoConfigNullKey() {
        //with null key passed
        expectedException.expect(IllegalArgumentException.class);
        new VWOConfig.Builder().apiKey("abc-123").setCustomDimension(null, "key").build();
    }

    @Test
    public void vwoConfigEmptyKey() {
        //with null key passed
        expectedException.expect(IllegalArgumentException.class);
        new VWOConfig.Builder().apiKey("abc-123").setCustomDimension("", "key").build();
    }

    @Test
    public void vwoConfigNullValue() {
        //with null key passed
        expectedException.expect(IllegalArgumentException.class);
        new VWOConfig.Builder().apiKey("abc-123").setCustomDimension("key", null).build();
    }

    @Test
    public void vwoConfigEmptyValue() {
        //with null key passed
        expectedException.expect(IllegalArgumentException.class);
        new VWOConfig.Builder().apiKey("abc-123").setCustomDimension("key", "").build();
    }

    @Test
    public void pushCDUrlWithUserId() {
        VWO vwo = new VWOMock().getVWOMockObject();
        Mockito.when(vwo.getConfig())
                .thenReturn(new VWOConfig.Builder().apiKey("abc-123")
                                    .setOptOut(false)
                                    .userID("userId")
                                    .build());

        Mockito.when(vwo.getVwoUrlBuilder()).thenReturn(new VWOUrlBuilder(vwo));
        Mockito.when(vwo.getVwoPreference())
                .thenReturn(new VWOPreference(RuntimeEnvironment.application.getApplicationContext()));
        String userId = vwo.getConfig().getUserID();

        String downloadUrl = vwo.getVwoUrlBuilder().getDownloadUrl();
        Assert.assertTrue(downloadUrl.contains(userId));

        String pushUrl = vwo.getVwoUrlBuilder().getCustomDimensionUrl("key", "value");
        Assert.assertTrue(pushUrl.contains(userId));
        Assert.assertFalse(pushUrl.contains("ed"));
    }

    @Test
    public void pushCDUrlWithoutUserId() {
        VWO vwo = new VWOMock().getVWOMockObject();
        Mockito.when(vwo.getConfig())
                .thenReturn(new VWOConfig.Builder().apiKey("abc-123").setOptOut(false).build());

        Mockito.when(vwo.getVwoUrlBuilder()).thenReturn(new VWOUrlBuilder(vwo));
        Mockito.when(vwo.getVwoPreference())
                .thenReturn(new VWOPreference(RuntimeEnvironment.application.getApplicationContext()));

        String uuid = VWOUtils.getDeviceUUID(vwo.getVwoPreference());
        String downloadUrl = vwo.getVwoUrlBuilder().getDownloadUrl();
        Assert.assertFalse(downloadUrl.contains(uuid));
        Assert.assertFalse(downloadUrl.contains("uHash"));

        String pushUrl = vwo.getVwoUrlBuilder().getCustomDimensionUrl("key", "value");
        Assert.assertTrue(pushUrl.contains(uuid));
        Assert.assertFalse(pushUrl.contains("ed"));
    }

    @Test
    public void trackUserWithCD() throws UnsupportedEncodingException {
        VWO vwo = new VWOMock().getVWOMockObject();
        Mockito.when(vwo.getConfig())
                .thenReturn(new VWOConfig.Builder().apiKey("abc-123")
                                    .setCustomDimension("key", "Value")
                                    .userID("userId")
                                    .build());

        Mockito.when(vwo.getVwoUrlBuilder()).thenReturn(new VWOUrlBuilder(vwo));
        Mockito.when(vwo.getVwoPreference())
                .thenReturn(new VWOPreference(RuntimeEnvironment.application.getApplicationContext()));
        String userId = vwo.getConfig().getUserID();

        String downloadUrl = vwo.getVwoUrlBuilder().getDownloadUrl();
        Assert.assertTrue(downloadUrl.contains(userId));

        String campaignUrl = vwo.getVwoUrlBuilder().getCampaignUrl(1, 1);
        String campaignUrlDecoded = URLDecoder.decode(campaignUrl, "UTF-8");
        Assert.assertTrue(campaignUrlDecoded.contains(userId));
        Assert.assertTrue(campaignUrlDecoded.contains(vwo.getConfig().getCustomDimension()));
        Assert.assertTrue(campaignUrlDecoded.contains("tags"));
    }
}
