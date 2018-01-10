package com.vwo.mobile;

import com.vwo.mobile.mock.ShadowVWOConfig;
import com.vwo.mobile.mock.VWOUtilsShadow;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

/**
 * Created by aman on Tue 02/01/18 14:39.
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 22, shadows = {VWOUtilsShadow.class,
        ShadowVWOConfig.class}, manifest = "AndroidManifest.xml")
public class InitializerTest {
    private final Object lock = new Object();

    @Test
    public void launchTest() {
//        VWO vwo = new VWOMock().getVWOMockObject();
//        VWO.with(RuntimeEnvironment.application.getApplicationContext(), "adbas-1234").launch(new VWOStatusListener() {
//            @Override
//            public void onVWOLoaded() {
//                synchronized (lock) {
//                    lock.notify();
//                }
//            }
//
//            @Override
//            public void onVWOLoadFailure(String reason) {
//                synchronized (lock) {
//                    lock.notify();
//                }
//            }
//        });
//        synchronized (lock) {
//            lock.wait();
//        }
//        Assert.assertEquals(0, vwo.getState());
    }
}

