package com.vwo.mobile.utils;

import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.vwo.mobile.BuildConfig;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import io.sentry.Sentry;

public class VWOLog {

    /**
     * The constant INITIALIZATION_LOGS.
     */
    public static final String INITIALIZATION_LOGS = "init";
    /**
     * The constant SOCKET_LOGS.
     */
    public static final String SOCKET_LOGS = "socket";
    /**
     * The constant INIT_SOCKET_LOGS.
     */
    public static final String INIT_SOCKET_LOGS = "socketInit";
    /**
     * The constant DATA_LOGS.
     */
    public static final String DATA_LOGS = "data";
    /**
     * The constant DOWNLOAD_DATA_LOGS.
     */
    public static final String DOWNLOAD_DATA_LOGS = "downloadData";
    /**
     * The constant UPLOAD_LOGS.
     */
    public static final String UPLOAD_LOGS = "uploadData";
    /**
     * The constant CONFIG_LOGS.
     */
    public static final String CONFIG_LOGS = "config";
    /**
     * The constant PREFERENCE_LOGS.
     */
    public static final String PREFERENCE_LOGS = "preference";
    /**
     * The constant URL_LOGS.
     */
    public static final String URL_LOGS = "url";
    /**
     * The constant TEST_LOGS.
     */
    public static final String TEST_LOGS = "test";
    /**
     * The constant SEGMENTATION_LOGS.
     */
    public static final String SEGMENTATION_LOGS = "segmentation";
    /**
     * The constant CAMPAIGN_LOGS.
     */
    public static final String CAMPAIGN_LOGS = "campaign";

    public static final String STORAGE_LOGS = "storage";

    public static final String UNCAUGHT = "uncaught";

    public static final String DATA = "data";

    public static final String ANALYTICS = "analytics";
    /**
     * OFF is a special level that can be used to turn off logging.
     * This level is initialized to <CODE>Integer.MAX_VALUE</CODE>.
     */
    public static final int OFF = Integer.MAX_VALUE;
    /**
     * SEVERE is a message level indicating a serious failure.
     * <p>
     * In general SEVERE messages should describe events that are
     * of considerable importance and which will prevent normal
     * program execution.   They should be reasonably intelligible
     * to end users and to system administrators.
     * This level is initialized to <CODE>1000</CODE>.
     */
    public static final int SEVERE = 1000;
    /**
     * WARNING is a message level indicating a potential problem.
     * <p>
     * In general WARNING messages should describe events that will
     * be of interest to end users or system managers, or which
     * indicate potential problems.
     * This level is initialized to <CODE>900</CODE>.
     */
    public static final int WARNING = 900;
    /**
     * The constant CONFIG.
     */
    public static final int CONFIG = 700;
    /**
     * INFO is a message level for informational messages.
     * <p>
     * Typically INFO messages will be written to the console
     * or its equivalent.  So the INFO level should only be
     * used for reasonably significant messages that will
     * make sense to end users and system administrators.
     * This level is initialized to <CODE>800</CODE>.
     */
    public static final int INFO = 800;
    /**
     * ALL indicates that all messages should be logged.
     * This level is initialized to <CODE>Integer.MIN_VALUE</CODE>.
     */
    public static final int ALL = Integer.MIN_VALUE;
    @LogLevel
    private static int LEVEL = BuildConfig.DEBUG ? SEVERE : OFF;

    /**
     * Sets log level.
     *
     * @param logLevel the log level
     */
    public static void setLogLevel(@LogLevel int logLevel) {
        VWOLog.LEVEL = logLevel;
    }

    /**
     * Verbose logging
     *
     * @param tag is the log tag
     * @param msg is the message to log.
     */
    public static void v(String tag, String msg) {
        if (TextUtils.isEmpty(msg)) {
            return;
        }
        if (LEVEL <= INFO) {
            if (Log.isLoggable(tag, Log.VERBOSE)) {
                Log.v(tag, msg);
            }
        }
    }

    /**
     * Always check if loggable
     */

    /**
     * V.
     *
     * @param tag       is the log tag to identify log in logcat
     * @param msg       is the log message
     * @param exception is the exception to be logged
     */
    public static void v(String tag, String msg, Throwable exception) {
        if (LEVEL <= INFO) {
            if (Log.isLoggable(tag, Log.VERBOSE)) {
                if (!TextUtils.isEmpty(msg)) {
                    Log.v(tag, msg, exception);
                } else {
                    Log.v(tag, "", exception);
                }
            }
        }
    }

    /**
     * Debug logging
     *
     * @param tag           the log tag
     * @param msg           the msg
     * @param checkLoggable the check loggable
     */
    public static void d(String tag, String msg, boolean checkLoggable) {
        if (TextUtils.isEmpty(msg)) {
            return;
        }
        if (LEVEL <= CONFIG) {
            if (checkLoggable) {
                if (Log.isLoggable(tag, Log.DEBUG)) {
                    Log.d(tag, msg);
                }
            } else {
                Log.d(tag, msg);
            }
        }
    }


    /*
      Check loggable based on flag
     */

    /**
     * Debug logging
     *
     * @param tag           the tag
     * @param msg           the msg
     * @param exception     the exception
     * @param checkLoggable the check loggable
     */
    public static void d(@NonNull String tag, @NonNull String msg, @NonNull Throwable exception, boolean checkLoggable) {
        if (LEVEL <= CONFIG) {
            if (checkLoggable) {
                if (Log.isLoggable(tag, Log.DEBUG)) {
                    if (!TextUtils.isEmpty(msg)) {
                        Log.d(tag, msg, exception);
                    } else {
                        Log.d(tag, "", exception);
                    }
                }
            } else {
                if (!TextUtils.isEmpty(msg)) {
                    Log.d(tag, msg, exception);
                } else {
                    Log.d(tag, "", exception);
                }
            }
        }
    }

    /**
     * information.
     *
     * @param tag           the tag
     * @param msg           the msg
     * @param checkLoggable the check loggable
     */
    public static void i(@NonNull String tag, @NonNull String msg, boolean checkLoggable) {
        if (TextUtils.isEmpty(msg)) {
            return;
        }
        if (LEVEL <= INFO) {
            if (checkLoggable) {
                if (Log.isLoggable(tag, Log.INFO)) {
                    Log.i(tag, msg);
                }
            } else {
                Log.i(tag, msg);
            }
        }
    }

    /**
     * information.
     *
     * @param tag           the tag
     * @param msg           the msg
     * @param exception     the exception
     * @param checkLoggable the check loggable
     */
    public static void i(@NonNull String tag, @NonNull String msg, @NonNull Throwable exception, boolean checkLoggable) {
        if (LEVEL <= INFO) {
            if (checkLoggable) {
                if (Log.isLoggable(tag, Log.INFO)) {
                    if (!TextUtils.isEmpty(msg)) {
                        Log.i(tag, msg, exception);
                    } else {
                        Log.i(tag, "", exception);
                    }
                }
            } else {
                if (!TextUtils.isEmpty(msg)) {
                    Log.i(tag, msg, exception);
                } else {
                    Log.i(tag, "", exception);
                }
            }
        }
    }

    /**
     * Method for error logging.
     *
     * @param tag           the tag
     * @param ex            the ex
     * @param checkLoggable the check loggable
     * @param sendToServer  check to send data to server
     */
    public static void e(@NonNull String tag, @NonNull Throwable ex, boolean checkLoggable, boolean sendToServer) {
        if (LEVEL <= SEVERE) {
            if (checkLoggable) {
                if (Log.isLoggable(tag, Log.ERROR)) {
                    ex.printStackTrace();
                }
            } else {
                ex.printStackTrace();
            }

            if (sendToServer && VWOUtils.checkIfClassExists("io.sentry.Sentry")) {
                Sentry.capture(ex);
            }
        }
    }

    /**
     * E.
     *
     * @param tag           the tag
     * @param msg           the msg
     * @param checkLoggable the check loggable
     * @param sendToServer  check to send data to server
     */
    public static void e(@NonNull String tag, @NonNull String msg, boolean checkLoggable, boolean sendToServer) {
        if (TextUtils.isEmpty(msg)) {
            return;
        }
        if (LEVEL <= SEVERE) {
            if (checkLoggable) {
                if (Log.isLoggable(tag, Log.ERROR)) {
                    Log.e(tag, msg);
                }
            } else {
                Log.e(tag, msg);
            }

            if (sendToServer && VWOUtils.checkIfClassExists("io.sentry.Sentry")) {
                Sentry.capture(new Exception(tag + ": " + msg));
            }
        }
    }

    /**
     * E.
     *
     * @param tag           the tag
     * @param msg           the msg
     * @param exception     the exception
     * @param checkLoggable the check loggable
     * @param sendToServer  check to send data to server
     */
    public static void e(@NonNull String tag, @NonNull String msg, @NonNull Throwable exception,
                         boolean checkLoggable, boolean sendToServer) {
        if (LEVEL <= SEVERE) {
            if (checkLoggable) {
                if (Log.isLoggable(tag, Log.ERROR)) {
                    if (!TextUtils.isEmpty(msg)) {
                        Log.e(tag, msg, exception);
                    } else {
                        Log.e(tag, "", exception);
                    }
                }
            } else {
                if (!TextUtils.isEmpty(msg)) {
                    Log.e(tag, msg, exception);
                } else {
                    Log.e(tag, "", exception);
                }
            }

            if (!TextUtils.isEmpty(msg) && sendToServer && VWOUtils.checkIfClassExists("io.sentry.Sentry")) {
                Sentry.capture(tag + ": " + msg);
            }
        }
    }

    /**
     * W.
     *
     * @param tag           the tag
     * @param msg           the msg
     * @param checkLoggable the check loggable
     */
    public static void w(@NonNull String tag, @NonNull String msg, boolean checkLoggable) {
        if (TextUtils.isEmpty(msg)) {
            return;
        }
        if (LEVEL <= WARNING) {
            if (checkLoggable) {
                if (Log.isLoggable(tag, Log.WARN)) {
                    Log.w(tag, msg);
                }
            } else {
                Log.w(tag, msg);
            }
        }
    }

    /**
     * W.
     *
     * @param tag           the tag
     * @param msg           the msg
     * @param exception     the exception
     * @param checkLoggable the check loggable
     */
    public static void w(@NonNull String tag, @NonNull String msg, @NonNull Throwable exception,
                         boolean checkLoggable) {
        if (LEVEL <= WARNING) {
            if (checkLoggable) {
                if (Log.isLoggable(tag, Log.WARN)) {
                    if (!TextUtils.isEmpty(msg)) {
                        Log.w(tag, msg, exception);
                    } else {
                        Log.w(tag, exception);
                    }
                }
            } else {
                if (!TextUtils.isEmpty(msg)) {
                    Log.w(tag, msg, exception);
                } else {
                    Log.w(tag, exception);
                }
            }
        }
    }

    /**
     * Wtf.
     *
     * @param tag           the tag
     * @param msg           the msg
     * @param checkLoggable the check loggable
     */
    public static void wtf(@NonNull String tag, @NonNull String msg, boolean checkLoggable) {
        if (TextUtils.isEmpty(msg)) {
            return;
        }
        if (LEVEL <= SEVERE) {
            if (checkLoggable) {
                if (Log.isLoggable(tag, Log.ERROR)) {
                    Log.wtf(tag, msg);
                }
            } else {
                Log.wtf(tag, msg);
            }
        }
    }

    /**
     * @param tag           the tag
     * @param exception     the exception
     * @param checkLoggable check if message is loggable. @see Log#isLoggable(String, int)
     */
    public static void wtf(@NonNull String tag, @NonNull Throwable exception, boolean checkLoggable) {
        if (LEVEL <= SEVERE) {
            if (checkLoggable) {
                if (Log.isLoggable(tag, Log.ERROR)) {
                    Log.wtf(tag, exception);
                }
            } else {
                Log.wtf(tag, exception);
            }
            if (VWOUtils.checkIfClassExists("io.sentry.Sentry")) {
                Sentry.capture(exception);
            }
        }
    }

    /**
     * @param tag           the log tag
     * @param msg           the message to be logged
     * @param exception     the exception to be logged
     * @param checkLoggable the check loggable {@link Log#isLoggable(String, int)}
     */
    public static void wtf(@NonNull String tag, @NonNull String msg, @NonNull Throwable exception,
                           boolean checkLoggable) {
        if (LEVEL <= SEVERE) {
            if (checkLoggable) {
                if (Log.isLoggable(tag, Log.ERROR)) {
                    if (!TextUtils.isEmpty(msg)) {
                        Log.wtf(tag, msg, exception);
                    } else {
                        Log.wtf(tag, exception);
                    }
                }
            } else {
                if (!TextUtils.isEmpty(msg)) {
                    Log.wtf(tag, msg, exception);
                } else {
                    Log.wtf(tag, exception);
                }
            }
            if (VWOUtils.checkIfClassExists("io.sentry.Sentry")) {
                if (!TextUtils.isEmpty(msg)) {
                    Sentry.capture(msg);
                }
                Sentry.capture(exception);
            }
        }
    }

    /**
     * The interface Log level.
     */
    @Retention(RetentionPolicy.SOURCE)
    @IntDef(value = {OFF, SEVERE, WARNING, CONFIG, INFO, ALL})
    public @interface LogLevel {
    }
}
