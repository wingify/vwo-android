package com.vwo.mobile;

import android.Manifest;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresPermission;

import com.vwo.mobile.events.VWOStatusListener;
import com.vwo.mobile.utils.VWOLog;

/**
 * Created by aman on 28/06/17.
 */

public class Initializer {
    private final VWO vwo;
    private final String apiKey;
    @Nullable
    private final Boolean optOut;

    Initializer(VWO vwo, String apiKey, @Nullable Boolean optOut) {
        this.vwo = vwo;
        this.apiKey = apiKey;
        this.optOut = optOut;
    }

    /**
     * Launches VWO sdk in Async mode with callback
     * <p>
     * This method will initialize the SDK either by fetching data from server or
     * from data of previous launch or from defaults(in case of network failure)
     * </p>
     *
     * @param statusListener is the listener for receiving callback launch status update. i.e. Failure
     *                       or success.
     */
    @RequiresPermission(allOf = {
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE})
    public void launch(@Nullable VWOStatusListener statusListener) {
        if (statusListener != null) {
            if(vwo.getConfig() == null) {
                VWOConfig vwoConfig = new VWOConfig.Builder().setVWOStatusListener(statusListener).build();
                vwo.setConfig(vwoConfig);
            } else {
                vwo.getConfig().setStatusListener(statusListener);
            }
        }
        setup(null);
        vwo.startVwoInstance();
    }

    /**
     * Start VWO sdk in sync mode(Not recommended. because it blocks UI thread for fetching data).
     * <p>
     * This method will initialize the sdk either by fetching data from server or
     * from data of previous launch or from defaults(in case of network failure)
     * </p>
     *
     * @param timeout is the timeout(in Milliseconds) for the HTTP call made to server.
     */
    @RequiresPermission(allOf = {
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE})
    public void launchSynchronously(long timeout) {
        setup(timeout);
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

    private void setup(@Nullable Long timeout) {
        if (this.vwo.getConfig() == null) {
            VWOConfig vwoConfig = new VWOConfig
                    .Builder()
                    .apiKey(apiKey)
                    .setOptOut(optOut != null && optOut)
                    .build();
            this.vwo.setConfig(vwoConfig);
        } else {
            VWOConfig vwoConfig = this.vwo.getConfig();
            vwoConfig.setApiKey(apiKey);
            if(optOut != null) {
                vwoConfig.setOptOut(optOut);
            }
        }

        this.vwo.getConfig().setTimeout(timeout);
    }
}