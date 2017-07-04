package com.vwo.mobile;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.util.Log;

import com.vwo.mobile.events.VWOStatusListener;
import com.vwo.mobile.utils.VWOLog;

/**
 * Created by aman on 28/06/17.
 */

public class Initializer {
    private final VWO vwo;
    private final String apiKey;

    Initializer(VWO vwo, String apiKey) {
        this.vwo = vwo;
        this.apiKey = apiKey;
    }

    /**
     * Start VWO sdk in Async mode.
     * <p>
     * This method will initialize the sdk either by fetching data from server or
     * from data of previous launch or from defaults(in case of network failure)
     */
    public void launch() {
        if (vwo == null) {
            throw new IllegalArgumentException("You need to initialize vwo instance first");
        }
        setup(vwo.getConfig(), false);
        vwo.startVwoInstance();
    }

    /**
     * Start VWO sdk in Async mode.
     * <p>
     * This method will initialize the sdk either by fetching data from server or
     * from data of previous launch or from defaults(in case of network failure)
     */
    public void launch(VWOStatusListener statusListener) {
        setup(vwo.getConfig(), false);
        vwo.startVwoInstance();
        vwo.setStatusListener(statusListener);
    }

    /**
     * Start VWO sdk in sync mode(Not recommended. because it blocks UI thread for fetching data).
     * <p>
     * This method will initialize the sdk either by fetching data from server or
     * from data of previous launch or from defaults(in case of network failure)
     */
    public void launchSynchronously() {
        setup(vwo.getConfig(), true);
        vwo.startVwoInstance();
    }

    public Initializer config(@NonNull VWOConfig VWOConfig) {
        if (vwo.getConfig() != null) {
            VWOLog.w(VWOLog.CONFIG_LOGS, "Configuration already set", true);
        }
        this.vwo.setConfig(VWOConfig);
        return this;
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    private void setup(@Nullable VWOConfig VWOConfig, boolean syncMode) {
        if (VWOConfig == null) {
            VWOConfig = new VWOConfig.Builder().apiKey(apiKey).build();
        } else {
            VWOConfig.setApiKey(apiKey);
        }

        VWOConfig.setSync(syncMode);
        vwo.setConfig(VWOConfig);
    }
}
