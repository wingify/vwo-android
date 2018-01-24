package com.vwo.mobile.logging;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

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
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        throwable.printStackTrace(printWriter);
        return stringWriter.toString();
    }

    @Nullable
    static String getCause(Throwable throwable, String packageName) {
        for (StackTraceElement stackTrace : throwable.getStackTrace()) {
            if (stackTrace.toString().contains(packageName)) {
                return stackTrace.toString();
            }
        }

        return null;
    }
}
