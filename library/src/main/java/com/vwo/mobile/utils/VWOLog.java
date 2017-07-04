package com.vwo.mobile.utils;

import android.util.Log;

import java.util.logging.Level;

import io.sentry.Sentry;

/**
 * Created by abhishek on 29/07/15 at 1:41 PM.
 */
public class VWOLog {

    public static final String INITIALIZATION_LOGS = "com.vwo.init";
    public static final String SOCKET_LOGS = "com.vwo.socket";
    public static final String INIT_SOCKET_LOGS = "com.vwo.socket.init";
    public static final String DOWNLOAD_DATA_LOGS = "com.vwo.data.download";
    public static final String UPLOAD_LOGS = "com.vwo.data.upload";
    public static final String DATA_LOGS = "com.vwo.data";
    public static final String CONFIG_LOGS = "com.vwo.config";
    public static final String PREFERENCE_LOGS = "com.vwo.preference";
    public static final String URL_LOGS = "com.vwo.url";
    public static final String TEST_LOGS = "com.vwo.test";
    public static final String SEGMENTATION_LOGS = "com.vwo.segmentation";
    public static final String CAMPAIGN_LOGS = "com.vwo.campaign";

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


    private static final int LEVEL = ALL;

    /**
     * Always check if loggable
     */


    @SuppressWarnings("ConstantConditions")
    public static void v(String tag, String msg) {
        if (LEVEL <= WARNING) {
            if (Log.isLoggable(tag, Log.VERBOSE)) {
                Log.v(tag, msg);
            }
        }
    }

    @SuppressWarnings("ConstantConditions")
    public static void v(String tag, String msg, Throwable exception) {
        if (LEVEL <= WARNING) {
            if (Log.isLoggable(tag, Log.VERBOSE)) {
                Log.v(tag, msg, exception);
            }
        }
    }


    /**
     * Check loggable based on flag
     */


    @SuppressWarnings("ConstantConditions")
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

    @SuppressWarnings("ConstantConditions")
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

    @SuppressWarnings("ConstantConditions")
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

    @SuppressWarnings("ConstantConditions")
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

    @SuppressWarnings("ConstantConditions")
    public static void e(String tag, Exception ex, boolean checkLoggable) {
        if (LEVEL <= SEVERE) {
            if (checkLoggable) {
                if (Log.isLoggable(tag, Log.ERROR)) {
                    Sentry.capture(ex);
                    try {
                        Log.e(tag, ex.getLocalizedMessage());
                        ex.printStackTrace();
                    } catch (Exception e) {
                        e(tag, e.getLocalizedMessage(), checkLoggable);
                    }
                }
            } else {
                Sentry.capture(ex);
                try {
                    Log.e(tag, ex.getLocalizedMessage());
                    ex.printStackTrace();
                } catch (Exception e) {
                    e(tag, e.getLocalizedMessage(), checkLoggable);
                }
            }
        }
    }

    @SuppressWarnings("ConstantConditions")
    public static void e(String tag, String msg, boolean checkLoggable) {
        if (LEVEL <= SEVERE) {
            if (checkLoggable) {
                if (Log.isLoggable(tag, Log.ERROR)) {
                    Sentry.capture(new Exception(tag + ": " + msg));
                    Log.e(tag, msg);
                }
            } else {
                Sentry.capture(new Exception(tag + ": " + msg));
                Log.e(tag, msg);
            }
        }
    }

    @SuppressWarnings("ConstantConditions")
    public static void e(String tag, String msg, Throwable exception, boolean checkLoggable) {
        if (LEVEL <= SEVERE) {
            if (checkLoggable) {
                if (Log.isLoggable(tag, Log.ERROR)) {
                    Sentry.capture(new Exception(tag + ": " + msg));
                    Log.e(tag, msg, exception);
                }
            } else {
                Sentry.capture(new Exception(tag + ": " + msg));
                Log.e(tag, msg, exception);
            }
        }
    }

    @SuppressWarnings("ConstantConditions")
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

    @SuppressWarnings("ConstantConditions")
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


    @SuppressWarnings("ConstantConditions")
    public static void wtf(String tag, String msg, boolean checkLoggable) {
        if (LEVEL <= WARNING) {
            if (checkLoggable) {
                if (Log.isLoggable(tag, Log.ERROR)) {
                    Log.wtf(tag, msg);
                }
            } else {
                Log.wtf(tag, msg);
            }
        }
    }

    @SuppressWarnings("ConstantConditions")
    public static void wtf(String tag, Throwable exception, boolean checkLoggable) {
        if (LEVEL <= WARNING) {
            if (checkLoggable) {
                if (Log.isLoggable(tag, Log.ERROR)) {
                    Sentry.capture(exception);
                    Log.wtf(tag, exception);
                }
            } else {
                Sentry.capture(exception);
                Log.wtf(tag, exception);
            }
        }
    }

    @SuppressWarnings("ConstantConditions")
    public static void wtf(String tag, String msg, Throwable exception, boolean checkLoggable) {
        if (LEVEL <= WARNING) {
            if (checkLoggable) {
                if (Log.isLoggable(tag, Log.ERROR)) {
                    Sentry.capture(msg);
                    Sentry.capture(exception);
                    Log.wtf(tag, msg, exception);
                }
            } else {
                Sentry.capture(msg);
                Sentry.capture(exception);
                Log.wtf(tag, msg, exception);
            }
        }
    }
}
