package com.vwo.mobile.mock;

import android.content.Context;

import com.vwo.mobile.utils.VWOUtils;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;

/**
 * Created by aman on Tue 02/01/18 15:58.
 */

@Implements(value = VWOUtils.class, inheritImplementationMethods = true)
public class VWOUtilsShadow {

    @Implementation
    public static boolean isValidVwoAppKey(String appKey) {
        return true;
    }

    @Implementation
    public static boolean checkForInternetPermissions(Context context) {
        return true;
    }

}
