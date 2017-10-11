package com.vwo.mobile.mock;

import com.vwo.mobile.VWOConfig;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;

/**
 * Created by aman on Tue 10/10/17 12:41.
 */

@Implements(VWOConfig.class)
public class VWOConfigShadow {

    @Implementation
    public String getValueForCustomSegment(String key) {
        return "value";
    }
}
