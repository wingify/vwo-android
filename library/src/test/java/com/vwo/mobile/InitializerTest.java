package com.vwo.mobile;

import android.content.Context;

import com.vwo.mobile.events.VWOStatusListener;
import com.vwo.mobile.mock.ShadowHandler;
import com.vwo.mobile.mock.ShadowVWODownloader;
import com.vwo.mobile.mock.ShadowVWOUtils;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

/**
 * Created by aman on Tue 02/01/18 14:39.
 */
@SuppressWarnings("deprecation")
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 17,
        shadows = {ShadowVWOUtils.class, ShadowHandler.class, ShadowVWODownloader.class})
public class InitializerTest {
    private final Object lock = new Object();
    private Initializer initializer;

    @Before
    public void setup() {
        Context context = RuntimeEnvironment.application.getApplicationContext();
        initializer = VWO.with(context, "adbas-1234");
    }

    @Test
    public void launchAsyncTest() throws InterruptedException {
        initializer.launch(new VWOStatusListener() {
            @Override
            public void onVWOLoaded() {
                synchronized (lock) {
                    lock.notifyAll();
                }
                System.out.println("Loaded successfully");
            }

            @Override
            public void onVWOLoadFailure(String reason) {
                synchronized (lock) {
                    lock.notifyAll();
                }
                System.out.println("Failed to load data because " + reason);
            }
        });
        synchronized (lock) {
            lock.wait();
        }

        Assert.assertEquals("grid", VWO.getObjectForKey("layout", null));
    }

    @Test
    public void launchSyncTest() {
        Context context = RuntimeEnvironment.application.getApplicationContext();
        VWO.with(context, "adbas-1234").launchSynchronously(5000);
        Assert.assertEquals("grid", VWO.getObjectForKey("layout", null));
    }
}

