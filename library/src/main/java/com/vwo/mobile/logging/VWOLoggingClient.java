package com.vwo.mobile.logging;

/**
 * Created by aman on Fri 13:12.
 */

public class VWOLoggingClient {
    private static VWOLoggingClient sentryClient;

    private VWOLoggingClient() {
    }

    public static VWOLoggingClient getInstance() {
        if (sentryClient == null) {
            sentryClient = new VWOLoggingClient();
        }

        return sentryClient;
    }

    private void setUncaughtExceptionHandler() {
        Thread.UncaughtExceptionHandler defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        VWOUncaughtExceptionHandler handler;
        if (defaultHandler == null) {
            handler = new VWOUncaughtExceptionHandler();
            Thread.setDefaultUncaughtExceptionHandler(handler);
        } else {
            if (!(defaultHandler instanceof VWOUncaughtExceptionHandler)) {
                handler = new VWOUncaughtExceptionHandler(defaultHandler);
                Thread.setDefaultUncaughtExceptionHandler(handler);
            }
        }
    }

    public void init() {
        setUncaughtExceptionHandler();
    }

}
