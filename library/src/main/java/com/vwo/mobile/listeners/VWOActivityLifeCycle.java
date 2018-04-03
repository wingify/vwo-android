package com.vwo.mobile.listeners;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

public class VWOActivityLifeCycle implements Application.ActivityLifecycleCallbacks {

    private static int sResumed;
    private static int sPaused;
    private static int sStarted;
    private static int sStopped;


    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) { }

    @Override
    public void onActivityStarted(Activity activity) {
        ++sStarted;
    }

    @Override
    public void onActivityResumed(Activity activity) {
        ++sResumed;
    }

    @Override
    public void onActivityPaused(Activity activity) {
        ++sPaused;
    }

    @Override
    public void onActivityStopped(Activity activity) {
        ++sStopped;
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) { }

    @Override
    public void onActivityDestroyed(Activity activity) { }

    @SuppressWarnings("unused")
    public static boolean isApplicationVisible() {
        return sStarted > sStopped;
    }

    public static boolean isApplicationInForeground() {
        return sResumed > sPaused;
    }
}
