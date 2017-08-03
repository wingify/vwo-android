package com.vwo.mobile.listeners;

/**
 * Created by aman on 03/08/17.
 */

public class ActivityLifecycleListener {
    private int sResumed;
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
