package com.vwo.mobile.utils;

import android.util.Log;

import io.sentry.Sentry;

/**
 * Created by abhishek on 29/07/15 at 1:41 PM.
 */
public class VWOLog {

    public static final String INITIALIZATION_LOGS = "com.vwo.init";
    public static final String DOWNLOAD_DATA_LOGS = "com.vwo.data.download";
    public static final String UPLOAD_LOGS = "com.vwo.data.upload";

    private static final boolean ENABLE_LOGS = false;

    public static final int INFO = 0;
    public static final int WARNING = 1;
    public static final int ERROR = 2;


    public static void d(String msg) {
        if(ENABLE_LOGS) {
            Log.d("**VWO_MOBILE", msg);
        }
    }

    public static void d(String tag, String msg) {
        if(ENABLE_LOGS) {
            Log.d("**" + tag, msg);
        }
    }

    public static void i(String tag, String msg) {
        if(ENABLE_LOGS) {
            Log.i("**" + tag, msg);
        }
    }

    public static void e(String tag, Exception ex) {
        Sentry.capture(ex);
        try {
            Log.e("**" + tag, ex.getLocalizedMessage());
            ex.printStackTrace();
        } catch (Exception e) {
            e(tag, e.getLocalizedMessage());
        }
    }

    public static void e(String tag, String msg) {
        Sentry.capture(new Exception(tag + ": " + msg));
        Log.e("**" + tag, msg);
    }

    public static void log(String msg, int code) {
        if(code == ERROR) {
            Log.e("VWO Mobile", msg);
        } else if(code == INFO) {
            Log.i("VWO Mobile", msg);
        } else if(code == WARNING) {
            Log.w("VWO Mobile", msg);
        } else {
            Log.d("VWO Mobile", msg);
        }
    }

}
