package com.vwo.mobile.network;

import android.support.annotation.NonNull;

/**
 * Created by aman on 12/09/17.
 */

public class DownloadTask implements DownloadableThread {

    private Thread currentThread;
    private NetworkRequest networkRequest;
    private DownloadTaskRunnable downloadTaskRunnable;

    public DownloadTask(NetworkRequest networkRequest) {
        this.networkRequest = networkRequest;
        this.downloadTaskRunnable = new DownloadTaskRunnable(this);
    }

    @Override
    public Thread getCurrentThread() {
        return currentThread;
    }

    @Override
    public void setCurrentThread(@NonNull Thread thread) {
        this.currentThread = thread;
    }

    public NetworkRequest getNetworkRequest() {
        return networkRequest;
    }

    public void setNetworkRequest(NetworkRequest networkRequest) {
        this.networkRequest = networkRequest;
    }

    public Runnable getRunnable() {
        return downloadTaskRunnable;
    }

    public class DownloadTaskRunnable implements Runnable {
        private DownloadTask downloadTask;
        public DownloadTaskRunnable(DownloadTask downloadTask) {
            this.downloadTask = downloadTask;
        }
        @Override
        public void run() {
            downloadTask.setCurrentThread(Thread.currentThread());
            networkRequest.execute();
        }
    }
}
