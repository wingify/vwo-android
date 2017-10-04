package com.vwo.mobile.network;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.vwo.mobile.utils.VWOLog;

import java.util.HashSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by aman on 12/09/17.
 */

public class PriorityRequestQueue implements RequestQueue {
    private static PriorityRequestQueue sPriorityRequestQueue;

    // Sets the amount of time an idle thread waits before terminating
    private static final int KEEP_ALIVE_TIME = 1;
    // Sets the Time Unit to seconds
    private static final TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;

    private static int NUMBER_OF_CORES =
            Runtime.getRuntime().availableProcessors();
    private final BlockingQueue<Runnable> mRequestQueue;
    private ThreadPoolExecutor mRequestThreadPool;

    protected PriorityRequestQueue() {
        mRequestQueue = new PriorityBlockingQueue<>();
        mRequestThreadPool = new ThreadPoolExecutor(NUMBER_OF_CORES, NUMBER_OF_CORES,
                KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, mRequestQueue);
    }

    @Override
    public void addToQueue(final NetworkRequest networkRequest) {
        mRequestThreadPool.execute(networkRequest);
    }

    public static PriorityRequestQueue getInstance() {
        if (sPriorityRequestQueue == null) {
            sPriorityRequestQueue = new PriorityRequestQueue();
        }
        return sPriorityRequestQueue;
    }
}
