package com.vwo.mobile.analytics;

import com.google.android.gms.analytics.Tracker;
import com.vwo.mobile.VWO;

/**
 * Created by aman on 17/07/17.
 */

public class VWOTracker {
    private Tracker mTracker;
    private VWO mVwo;

    public VWOTracker(Object tracker, VWO mVwo) {

        if (tracker instanceof Tracker) {
            mTracker = (Tracker) tracker;
        }
        this.mVwo = mVwo;
    }

    public Tracker getTracker() {
        return mTracker;
    }
}
