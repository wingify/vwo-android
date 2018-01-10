package com.vwo.mobile.mock;

import android.content.Context;
import android.text.TextUtils;

import com.vwo.mobile.utils.VWOUtils;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;

import java.util.regex.Pattern;

/**
 * Created by aman on Tue 02/01/18 15:58.
 */

@Implements(value = VWOUtils.class, inheritImplementationMethods = true)
public class VWOUtilsShadow {

    @Implementation
    public static boolean isValidVwoAppKey(String appKey) {
        String regex = "[\\w]+-[0-9]+";
        Pattern pattern = Pattern.compile(regex);
        return !TextUtils.isEmpty(appKey) && pattern.matcher(appKey).matches();
    }

    @Implementation
    public static boolean checkForInternetPermissions(Context context) {
        return true;
    }

}
