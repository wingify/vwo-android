package com.vwo.mobile.logging;

import com.vwo.mobile.utils.VWOLog;

/**
 * Created by aman on Fri 13:13.
 */

public class VWOUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        if(LogUtils.getCause(throwable, "com.vwo.mobile") != null) {
            VWOLog.e(VWOLog.UNCAUGHT, throwable, false, true);
        }
    }
}
