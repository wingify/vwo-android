package com.vwo.mobile.network;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by aman on 14/09/17.
 */

public class ScheduledRequestQueue {

    private static ScheduledRequestQueue sRequestQueue;


    private static int NUMBER_OF_CORES =
            Runtime.getRuntime().availableProcessors();
    private ScheduledThreadPoolExecutor mRequestThreadPool;

    private ScheduledRequestQueue() {
        mRequestThreadPool = new ScheduledThreadPoolExecutor(NUMBER_OF_CORES);
    }

    public static ScheduledRequestQueue getInstance() {
        if(sRequestQueue == null) {
            sRequestQueue = new ScheduledRequestQueue();
        }
        return sRequestQueue;
    }

    public void scheduleAtFixedRate(Runnable runnable, long initialDelay, long period, TimeUnit timeUnit) {
        mRequestThreadPool.scheduleAtFixedRate(runnable, initialDelay, period, timeUnit);
    }
}
