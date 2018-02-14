package com.vwo.mobile.mock;

import android.app.ActivityManager;
import android.content.Context;
import android.support.annotation.Nullable;

import com.vwo.mobile.logging.LogUtils;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;

/**
 * Created by aman on Wed 14/02/18 14:18.
 */
@Implements(value = LogUtils.class)
public class ShadowLogUtils {

    @Implementation
    private static boolean isExternalStorageMounted() {
        return true;
    }

    @Implementation
    @Nullable
    public static Long getUnusedInternalStorageSize() {
        return null;
    }

    @Implementation
    @Nullable
    public static Long getInternalStorageSize() {
        return null;
    }

    @Implementation
    @Nullable
    public static Long getUnusedExternalStorageSize() {
        return null;
    }

    @Implementation
    @Nullable
    public static Long getExternalStorageSize() {
        return null;
    }

    @Implementation
    @Nullable
    public static ActivityManager.MemoryInfo getAppMemoryInfo(Context context) {
        return null;
    }
}
