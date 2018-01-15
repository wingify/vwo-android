package com.vwo.mobile.mock;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;

/**
 * Created by aman on Thu 11/01/18 16:22.
 */

@Implements(value = android.os.Handler.class)
public class ShadowHandler {

    @Implementation
    public final boolean post(Runnable r) {
        r.run();
        return true;
    }
}
