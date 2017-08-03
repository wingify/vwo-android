package com.vwo.mobile;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;

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
     * Launches VWO sdk in Async mode.
     *
     * This method will initialize the SDK either by fetching data from server or
     * from data of previous launch or from defaults(in case of network failure)
     *
     */
    public void launch() {
        if (vwo == null) {
            throw new IllegalArgumentException("You need to initialize vwo instance first");
        }
        setup(vwo.getConfig(), false);
        vwo.startVwoInstance();
    }

    /**
     * Launches VWO sdk in Async mode with callback
     *
     * This method will initialize the SDK either by fetching data from server or
     * from data of previous launch or from defaults(in case of network failure)
     *
     *
     * @param statusListener is the listener for receiving callback launch status update. i.e. Failure
     *                       or success.
     */
    public void launch(@NonNull VWOStatusListener statusListener) {
        setup(vwo.getConfig(), false);
        vwo.startVwoInstance();
        VWO.setVWOStatusListener(statusListener);
    }

    /**
     * Start VWO sdk in sync mode(Not recommended. because it blocks UI thread for fetching data).
     *
     * This method will initialize the sdk either by fetching data from server or
     * from data of previous launch or from defaults(in case of network failure)
     *
     */
    public void launchSynchronously() {
        setup(vwo.getConfig(), true);
        vwo.startVwoInstance();
    }

    /**
     * Set initialization {@link VWOConfig} for the launch.
     *
     * @param vwoConfig is the SDK launch config
     * @return the current {@link Initializer} object.
     */
    public Initializer config(@NonNull VWOConfig vwoConfig) {
        if (vwo.getConfig() != null) {
            VWOLog.w(VWOLog.CONFIG_LOGS, "Configuration already set", true);
        }
        this.vwo.setConfig(vwoConfig);
        return this;
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    private void setup(@Nullable VWOConfig vwoConfig, boolean syncMode) {
        if (vwoConfig == null) {
            vwoConfig = new VWOConfig.Builder().apiKey(apiKey).build();
        } else {
            vwoConfig.setApiKey(apiKey);
        }

        vwoConfig.setSync(syncMode);
        vwo.setConfig(vwoConfig);
    }
}
