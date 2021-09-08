package com.vwo.mobile.mock;

import androidx.annotation.NonNull;

import com.vwo.mobile.utils.VWOLog;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;

/**
 * Created by aman on Fri 12/01/18 16:29.
 */

@Implements(value = VWOLog.class, inheritImplementationMethods = true)
public class ShadowVWOLog {

    @Implementation
    public static void setLogLevel(@VWOLog.LogLevel int logLevel) {
        System.out.println("Setting log level to : " + logLevel);
    }

    @Implementation
    public static void v(String tag, String msg) {
        System.out.println((char) 27 + "[33m");
        System.out.println(tag + ": " + msg);
    }

    @Implementation
    public static void v(String tag, String msg, Throwable exception) {
        System.out.println(tag + ": " + msg + "\n" + exception.toString());
    }

    /**
     * Debug logging
     *
     * @param tag           the log tag
     * @param msg           the msg
     * @param checkLoggable the check loggable
     */
    @Implementation
    public static void d(String tag, String msg, boolean checkLoggable) {
        System.out.println(tag + ": " + msg);
    }


    @Implementation
    public static void d(@NonNull String tag, @NonNull String msg, @NonNull Throwable exception, boolean checkLoggable) {
        System.out.println(tag + ": " + msg + "\n" + exception.toString());

    }

    @Implementation
    public static void i(@NonNull String tag, @NonNull String msg, boolean checkLoggable) {
        System.out.println(tag + ": " + msg);
    }

    @Implementation
    public static void i(@NonNull String tag, @NonNull String msg, @NonNull Throwable exception, boolean checkLoggable) {
        System.out.println(tag + ": " + msg + "\n" + exception.toString());
    }

    @Implementation
    public static void e(@NonNull String tag, @NonNull Throwable ex, boolean checkLoggable, boolean sendToServer) {
        System.out.println(tag + ": " + ex.toString());
    }

    @Implementation
    public static void e(@NonNull String tag, @NonNull String msg, boolean checkLoggable, boolean sendToServer) {
        System.out.println(tag + ": " + msg);
    }

    @Implementation
    public static void e(@NonNull String tag, @NonNull String msg, @NonNull Throwable exception,
                         boolean checkLoggable, boolean sendToServer) {
        System.out.println(tag + ": " + msg + "\n" + exception.toString());
    }

    @Implementation
    public static void w(@NonNull String tag, @NonNull String msg, boolean checkLoggable) {
        System.out.println(tag + ": " + msg);
    }

    @Implementation
    public static void w(@NonNull String tag, @NonNull String msg, @NonNull Throwable exception,
                         boolean checkLoggable) {
        System.out.println(tag + ": " + msg + "\n" + exception.toString());
    }

    @Implementation
    public static void wtf(@NonNull String tag, @NonNull String msg, boolean checkLoggable) {
        System.out.println(tag + ": " + msg);
    }

    @Implementation
    public static void wtf(@NonNull String tag, @NonNull Throwable exception, boolean checkLoggable) {
        System.out.println(tag + ": " + exception.toString());
    }

    @Implementation
    public static void wtf(@NonNull String tag, @NonNull String msg, @NonNull Throwable exception,
                           boolean checkLoggable) {
        System.out.println(tag + ": " + msg + "\n" + exception.toString());
    }
}
