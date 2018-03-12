package com.vwo.mobile;

import com.vwo.mobile.events.VWOStatusListener;
import com.vwo.mobile.mock.ShadowHandler;
import com.vwo.mobile.mock.ShadowVWODownloader;
import com.vwo.mobile.mock.ShadowVWOLog;
import com.vwo.mobile.mock.ShadowVWOUtils;
import com.vwo.mobile.mock.VWOPersistDataShadow;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

/**
 * Created by aman on Fri 09/03/18 12:43.
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 21, shadows = {VWOPersistDataShadow.class, ShadowVWOLog.class, ShadowVWOUtils.class,
        ShadowVWODownloader.class, ShadowHandler.class}, manifest = "AndroidManifest.xml")
public class VWOTest {
    private final Object lock = new Object();

    @Test
    public void variationForKeyTest() throws InterruptedException {
        VWO.with(RuntimeEnvironment.application.getApplicationContext(), "test-123")
                .launch(new VWOStatusListener() {
                    @Override
                    public void onVWOLoaded() {
                        synchronized (lock) {
                            lock.notify();
                        }
                    }

                    @Override
                    public void onVWOLoadFailure(String reason) {
                        System.out.println(reason);
                        synchronized (lock) {
                            lock.notify();
                        }
                    }
                });

        synchronized (lock) {
            lock.wait();
        }

        Assert.assertEquals("grid", VWO.getVariationForKey("layout", "list"));
        Assert.assertEquals("email", VWO.getVariationForKey("social", "email"));
        Assert.assertEquals("default", VWO.getVariationForKey(null, "default"));
        Assert.assertEquals("default", VWO.getVariationForKey("", "default"));
        Assert.assertEquals("grid", VWO.getVariationForKey("layout"));
        Assert.assertNull(VWO.getVariationForKey("email"));
    }
}
