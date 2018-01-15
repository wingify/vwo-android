package com.vwo.mobile.mock;

import com.vwo.mobile.VWOConfig;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;

/**
 * Created by aman on Tue 02/01/18 16:05.
 */

@Implements(value = VWOConfig.class, inheritImplementationMethods = true)
public class ShadowVWOConfig {

    @Implementation
    boolean isOptOut() {
        return false;
    }
}
