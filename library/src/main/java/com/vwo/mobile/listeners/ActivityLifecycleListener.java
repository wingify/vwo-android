package com.vwo.mobile.listeners;

/**
 * Created by aman on 03/08/17.
 */

public class ActivityLifecycleListener {
    private int sResumed = 1;
    private int sPaused;
    private int sStarted;
    private int sStopped;


    public void onStart() {
        ++sStarted;
    }

    public void onResume() {
        ++sResumed;
    }

    public void onPause() {
        ++sPaused;
        if (sResumed - sPaused >= 1) {
            sResumed = sPaused;
        }
    }

    public void onStop() {
        ++sStopped;
    }

    @SuppressWarnings("unused")
    public boolean isApplicationVisible() {
        return sStarted > sStopped;
    }

    public boolean isApplicationInForeground() {
        return sResumed > sPaused;
    }

}
