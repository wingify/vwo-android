package com.vwo.mobile;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.util.Log;

import com.vwo.mobile.events.VwoStatusListener;
import com.vwo.mobile.utils.VwoLog;

/**
 * Created by aman on 28/06/17.
 */

public class Initializer {
    private final Vwo vwo;
    private final String apiKey;

    Initializer(Vwo vwo, String apiKey) {
        this.vwo = vwo;
        this.apiKey = apiKey;
    }

    /**
     * Start VWO sdk in Async mode.
     *
     * This method will initialize the sdk either by fetching data from server or
     * from data of previous launch or from defaults(in case of network failure)
     */
    public void launchAsync() {
        if (vwo == null) {
            throw new IllegalArgumentException("You need to initialize vwo instance first");
        }
        setup(vwo.getConfig(), false);
        vwo.startVwoInstance();
    }

    /**
     * Start VWO sdk in Async mode.
     *
     * This method will initialize the sdk either by fetching data from server or
     * from data of previous launch or from defaults(in case of network failure)
     */
    public void launchAsync(VwoStatusListener statusListener) {
        setup(vwo.getConfig(), false);
        vwo.startVwoInstance();
        vwo.setStatusListener(statusListener);
    }

    /**
     * Start VWO sdk in sync mode(Not recommended. because it blocks UI thread for fetching data).
     *
     * This method will initialize the sdk either by fetching data from server or
     * from data of previous launch or from defaults(in case of network failure)
     *
     */
    public void launch() {
        setup(vwo.getConfig(), true);
        vwo.startVwoInstance();
    }

    public Initializer config(@NonNull VwoConfig vwoConfig) {
        if(vwo.getConfig() != null) {
            if(Log.isLoggable(VwoLog.INITIALIZATION_LOGS, Log.WARN)) {
                Log.w(VwoLog.INITIALIZATION_LOGS, "Configuration already set");
            }
        }
        this.vwo.setConfig(vwoConfig);
        return this;
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    private void setup(@Nullable VwoConfig vwoConfig, boolean syncMode) {
        if(vwoConfig == null) {
            vwoConfig = new VwoConfig.Builder().apiKey(apiKey).build();
        } else {
            vwoConfig.setApiKey(apiKey);
        }

        vwoConfig.setSync(syncMode);
        vwo.setConfig(vwoConfig);
    }
}
