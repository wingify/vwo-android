package com.vwo.mobile.logging;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.vwo.mobile.utils.VWOLog;

/**
 * Created by aman on Fri 13:13.
 */

public class VWOUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
    @Nullable
    private Thread.UncaughtExceptionHandler existingHandler;

    VWOUncaughtExceptionHandler(@NonNull Thread.UncaughtExceptionHandler existingHandler) {
        this.existingHandler = existingHandler;
    }

    VWOUncaughtExceptionHandler() {
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        if(LogUtils.getCause(throwable, "com.vwo.mobile") != null) {
            VWOLog.wtf(VWOLog.UNCAUGHT, throwable, false);
        } else if(this.existingHandler != null) {
            this.existingHandler.uncaughtException(thread, throwable);
        }
    }
}
