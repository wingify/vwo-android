package com.vwo.mobile.logging;

/**
 * Created by aman on Fri 13:12.
 */

public class VWOLoggingClient {
    private static VWOLoggingClient sentryClient;

    private VWOLoggingClient() {
    }

    public static VWOLoggingClient getInstance() {
        if(sentryClient == null) {
            sentryClient = new VWOLoggingClient();
        }

        return sentryClient;
    }

    private void setUncaughtExceptionHandler() {
        Thread.UncaughtExceptionHandler defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        if(!(defaultHandler instanceof VWOUncaughtExceptionHandler)) {
            Thread.setDefaultUncaughtExceptionHandler(new VWOUncaughtExceptionHandler());
        }
    }

    public void init() {
        setUncaughtExceptionHandler();
    }

}
