package com.vwo.mobile.network;

import com.vwo.mobile.utils.VWOLog;

import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * Created by aman on 14/09/17.
 */

public class ScheduledRequestQueue extends ScheduledThreadPoolExecutor {

    public ScheduledRequestQueue() {
        super(1);
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        VWOLog.e(VWOLog.UPLOAD_LOGS, "after execute", false, false);
    }

    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        super.beforeExecute(t, r);
        VWOLog.e(VWOLog.UPLOAD_LOGS, "before execute", false, false);
    }
}
