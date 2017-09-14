package com.vwo.mobile.network;

/**
 * Created by aman on 12/09/17.
 */

public interface DownloadableThread {
    Thread getCurrentThread();
    void setCurrentThread(Thread thread);
}
