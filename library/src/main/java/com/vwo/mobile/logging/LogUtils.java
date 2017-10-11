package com.vwo.mobile.logging;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by aman on Fri 13:14.
 */

public class LogUtils {

    @Nullable
    public static JSONObject getStackTrace(@NonNull Throwable throwable) throws JSONException {
        JSONArray frameList = new JSONArray();

        for (StackTraceElement ste : throwable.getStackTrace()) {
            JSONObject frame = new JSONObject();

            String method = ste.getMethodName();
            if (method.length() != 0) {
                frame.put("function", method);
            }

            int lineno = ste.getLineNumber();
            if (!ste.isNativeMethod() && lineno >= 0) {
                frame.put("lineno", lineno);
            }

            boolean inApp = true;

            String className = ste.getClassName();
            frame.put("module", className);

            // Take out some of the system packages to improve the exception folding on the sentry server
            if (className.startsWith("android.")
                    || className.startsWith("java.")
                    || className.startsWith("dalvik.")
                    || className.startsWith("com.android.")) {

                inApp = false;
            }

            frame.put("in_app", inApp);

            frameList.put(frame);
        }

        JSONObject frameHash = new JSONObject();
        frameHash.put("frames", frameList);

        return frameHash;
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
