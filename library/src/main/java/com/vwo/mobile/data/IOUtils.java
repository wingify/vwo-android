package com.vwo.mobile.data;

import android.content.Context;
import android.os.Build;

import java.io.File;

/**
 * Created by aman on 19/09/17.
 */

public class IOUtils {

    public static File getCacheDirectory(Context context) {
        return context.getFilesDir();
    }
}
