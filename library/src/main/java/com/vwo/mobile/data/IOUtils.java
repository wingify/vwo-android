package com.vwo.mobile.data;

import android.content.Context;
import android.os.Build;

import java.io.File;

/**
 * Created by aman on 19/09/17.
 */

public class IOUtils {

    public static File getCacheDirectory(Context context) {
        File dir;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            dir = context.getFilesDir();
        } else {
            dir = context.getExternalFilesDir(null);
        }

        return dir;
    }
}
