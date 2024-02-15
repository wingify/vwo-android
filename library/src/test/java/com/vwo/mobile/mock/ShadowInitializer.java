package com.vwo.mobile.mock;

import androidx.annotation.Nullable;

import com.vwo.mobile.Initializer;
import com.vwo.mobile.events.VWOStatusListener;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.annotation.RealObject;

/**
 * Created by aman on Fri 09/03/18 13:10.
 */
@Implements(value = com.vwo.mobile.Initializer.class)
public class ShadowInitializer {

    @RealObject
    Initializer initializer;

    void __constructor__(com.vwo.mobile.VWO vwo, java.lang.String apiKey, boolean optOut) {

    }

    @Implementation
    public void launch(@Nullable final VWOStatusListener statusListener) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (statusListener != null) {
                    statusListener.onVWOLoaded();
                }
            }
        });
        thread.start();
    }
}
