package com.vwo.mobile.network;

import com.vwo.mobile.utils.VWOLog;

import java.util.WeakHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * Created by aman on 14/09/17.
 */

public class ScheduledRequestQueue extends ScheduledThreadPoolExecutor {
    private static WeakHashMap<String, ScheduledRequestQueue> queueMap = new WeakHashMap<>();
    private boolean running;

    private ScheduledRequestQueue(String tag) {
        super(1);
        queueMap.put(tag, this);
        VWOLog.i(VWOLog.QUEUE, "Creating new Scheduler with tag " + tag, true);
        running = false;
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        VWOLog.e(VWOLog.QUEUE, "after execute", true, false);
    }

    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        super.beforeExecute(t, r);
        VWOLog.e(VWOLog.QUEUE, "before execute", true, false);
    }

    public static ScheduledRequestQueue getInstance(String tag) {
        if(queueMap.containsKey(tag) && queueMap.get(tag) != null) {
            VWOLog.i(VWOLog.NETWORK_LOGS, "Returning existing Scheduler with tag " + tag, true);
            return queueMap.get(tag);
        }
        return new ScheduledRequestQueue(tag);
    }

    public boolean isRunning() {
        return this.running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }
}
