package com.vwo.mobile.data;

import android.Manifest;
import android.content.Context;
import android.os.Build;
import android.support.v4.content.ContextCompat;

import com.vwo.mobile.utils.VWOUtils;

import java.io.File;

/**
 * Created by aman on 19/09/17.
 */

public class IOUtils {

    public static File getCacheDirectory(Context context) {
        File dir;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            if (VWOUtils.checkForPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                dir = ContextCompat.getExternalFilesDirs(context, null)[0];
            } else {
                dir = context.getCacheDir();
            }
        } else {
            dir = context.getExternalFilesDir(null);
        }

        return dir;
    }
}
