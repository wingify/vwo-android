package com.vwo.mobile;

import com.vwo.mobile.events.VWOStatusListener;
import com.vwo.mobile.mock.ShadowHandler;
import com.vwo.mobile.mock.ShadowVWODownloader;
import com.vwo.mobile.mock.ShadowVWOLog;
import com.vwo.mobile.mock.ShadowVWOUtils;
import com.vwo.mobile.mock.VWOPersistDataShadow;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
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
    private static final Object lock = new Object();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private static boolean isInitialized = false;

    @Before
    public void setup() throws InterruptedException {
        if(!isInitialized) {
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

            isInitialized = true;

            synchronized (lock) {
                lock.wait();
            }
        }
    }

    @SuppressWarnings("deprecation")
    @Test
    public void variationForKeyTest() {
        Assert.assertEquals("grid", VWO.getObjectForKey("layout", "list"));
        Assert.assertEquals("email", VWO.getObjectForKey("social", "email"));

        expectedException.expect(NullPointerException.class);
        Assert.assertEquals("default", VWO.getObjectForKey(null, "default"));

        Assert.assertEquals("default", VWO.getObjectForKey("", "default"));
        Assert.assertEquals("grid", VWO.getObjectForKey("layout", null));

        Assert.assertEquals("grid", VWO.getStringForKey("layout", "list"));
        Assert.assertNull(VWO.getObjectForKey("email", null));

        Assert.assertEquals(1, VWO.getIntegerForKey("layout", 1));
        Assert.assertEquals(2.5, VWO.getDoubleForKey("double", 0));
        Assert.assertEquals("string", VWO.getStringForKey("String", null));
        Assert.assertTrue(VWO.getBooleanForKey("boolean", false));

        Assert.assertEquals(2, VWO.getIntegerForKey("String", 2));
        Assert.assertEquals(1.0, VWO.getDoubleForKey("integer", 1.5));
        Assert.assertEquals("true", VWO.getStringForKey("boolean", "String"));
        Assert.assertFalse(VWO.getBooleanForKey("double", false));
    }

    @Test
    public void variationNameForTestKeyTest() {
        Assert.assertEquals("third", VWO.getVariationNameForTestKey("demoapp"));
        Assert.assertEquals("second", VWO.getVariationNameForTestKey("default_value_test"));
        Assert.assertNotSame("third", VWO.getVariationNameForTestKey("default_value_test"));
        Assert.assertNull(VWO.getVariationNameForTestKey("demo"));
    }
}
