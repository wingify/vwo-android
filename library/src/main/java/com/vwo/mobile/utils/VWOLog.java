package com.vwo.mobile.utils;

import android.support.annotation.IntDef;
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
     * V.
     *
     * @param tag is the log tag
     * @param msg is the message to log.
     */
    public static void v(String tag, String msg) {
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
                Log.v(tag, msg, exception);
            }
        }
    }

    /**
     * D.
     *
     * @param tag           the tag
     * @param msg           the msg
     * @param checkLoggable the check loggable
     */
    public static void d(String tag, String msg, boolean checkLoggable) {
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
     * D.
     *
     * @param tag           the tag
     * @param msg           the msg
     * @param exception     the exception
     * @param checkLoggable the check loggable
     */
    public static void d(String tag, String msg, Throwable exception, boolean checkLoggable) {
        if (LEVEL <= CONFIG) {
            if (checkLoggable) {
                if (Log.isLoggable(tag, Log.DEBUG)) {
                    Log.d(tag, msg, exception);
                }
            } else {
                Log.d(tag, msg, exception);
            }
        }
    }

    /**
     * .
     *
     * @param tag           the tag
     * @param msg           the msg
     * @param checkLoggable the check loggable
     */
    public static void i(String tag, String msg, boolean checkLoggable) {
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
     * .
     *
     * @param tag           the tag
     * @param msg           the msg
     * @param exception     the exception
     * @param checkLoggable the check loggable
     */
    public static void i(String tag, String msg, Throwable exception, boolean checkLoggable) {
        if (LEVEL <= INFO) {
            if (checkLoggable) {
                if (Log.isLoggable(tag, Log.INFO)) {
                    Log.i(tag, msg, exception);
                }
            } else {
                Log.i(tag, msg, exception);
            }
        }
    }

    /**
     * E.
     *
     * @param tag           the tag
     * @param ex            the ex
     * @param checkLoggable the check loggable
     * @param sendToServer  check to send data to server
     */
    public static void e(String tag, Exception ex, boolean checkLoggable, boolean sendToServer) {
        if (LEVEL <= SEVERE) {
            if (checkLoggable) {
                if (Log.isLoggable(tag, Log.ERROR)) {
                    try {
                        Log.e(tag, ex.getLocalizedMessage());
                        ex.printStackTrace();
                    } catch (Exception e) {
                        e(tag, e.getLocalizedMessage(), true, sendToServer);
                    }
                }
            } else {
                try {
                    Log.e(tag, ex.getLocalizedMessage());
                    ex.printStackTrace();
                } catch (Exception e) {
                    e(tag, e.getLocalizedMessage(), false, sendToServer);
                }
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
    public static void e(String tag, String msg, boolean checkLoggable, boolean sendToServer) {
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
    public static void e(String tag, String msg, Throwable exception, boolean checkLoggable, boolean sendToServer) {
        if (LEVEL <= SEVERE) {
            if (checkLoggable) {
                if (Log.isLoggable(tag, Log.ERROR)) {
                    Log.e(tag, msg, exception);
                }
            } else {
                Log.e(tag, msg, exception);
            }

            if (sendToServer && VWOUtils.checkIfClassExists("io.sentry.Sentry")) {
                Sentry.capture(new Exception(tag + ": " + msg));
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
    public static void w(String tag, String msg, boolean checkLoggable) {
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
    public static void w(String tag, String msg, Throwable exception, boolean checkLoggable) {
        if (LEVEL <= WARNING) {
            if (checkLoggable) {
                if (Log.isLoggable(tag, Log.WARN)) {
                    Log.w(tag, msg, exception);
                }
            } else {
                Log.w(tag, msg, exception);
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
    public static void wtf(String tag, String msg, boolean checkLoggable) {
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
    public static void wtf(String tag, Throwable exception, boolean checkLoggable) {
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
    public static void wtf(String tag, String msg, Throwable exception, boolean checkLoggable) {
        if (LEVEL <= SEVERE) {
            if (checkLoggable) {
                if (Log.isLoggable(tag, Log.ERROR)) {
                    Log.wtf(tag, msg, exception);
                }
            } else {
                Log.wtf(tag, msg, exception);
            }
            if (VWOUtils.checkIfClassExists("io.sentry.Sentry")) {
                Sentry.capture(msg);
                Sentry.capture(exception);
            }
        }
    }

    /**
     * The interface Log level.
     */
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({OFF, SEVERE, WARNING, CONFIG, INFO, ALL})
    public @interface LogLevel {
    }
}
