package com.vwo.mobile.mock;

import android.content.res.Configuration;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.annotation.RealObject;

import java.util.Locale;

/**
 * Created by aman on Tue 10/10/17 16:08.
 */

@Implements(value = android.content.res.Configuration.class, inheritImplementationMethods = true)
public class ShadowConfiguration {
    @RealObject
    private Configuration realConfiguration;

    public int screenLayout;
    public int touchscreen;
    public int orientation;

    @Implementation
    public void setToDefaults() {
        realConfiguration.screenLayout =
                Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    @Implementation
    public void setLocale(Locale l) {
        realConfiguration.locale = l;
    }
}
