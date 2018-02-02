package com.vwo.mobile.logging;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by aman on Fri 13:14.
 */

public class LogUtils {

    @Nullable
    public static String getStackTrace(@NonNull Throwable throwable) {
        return Log.getStackTraceString(throwable);
    }

    @Nullable
    static String getCause(Throwable throwable, String packageName) {
        String stackTrace = getStackTrace(throwable);
        if (stackTrace != null && stackTrace.contains(packageName)) {
            return stackTrace;
        }

        return null;
    }
}
