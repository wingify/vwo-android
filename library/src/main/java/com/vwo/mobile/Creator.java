package com.vwo.mobile;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.vwo.mobile.events.VwoStatusListener;

/**
 * Created by aman on 28/06/17.
 */

public class Creator {
    private final Vwo vwo;

    Creator(Vwo vwo) {
        this.vwo = vwo;
    }

    @SuppressWarnings("unused")
    public void startAsync() {
        if (vwo == null) {
            throw new IllegalArgumentException("You need to initialize vwo instance first");
        }
        setup(vwo.getConfig(), false);
        vwo.startVwoInstance();
    }

    @SuppressWarnings("unused")
    public void startAsync(VwoStatusListener statusListener) {
        vwo.startVwoInstance();
        vwo.setStatusListener(statusListener);
        setup(vwo.getConfig(), false);
    }

    @SuppressWarnings("unused")
    public void start() {
        setup(vwo.getConfig(), true);
        vwo.startVwoInstance();
    }

    public Creator config(VwoConfig vwoConfig) {
        this.vwo.setConfig(vwoConfig);
        return this;
    }

    private void setup(@Nullable VwoConfig vwoConfig, boolean syncMode) {
        try {
            @SuppressWarnings("ConstantConditions") ApplicationInfo applicationInfo = vwo.getCurrentContext().getPackageManager().getApplicationInfo(vwo.getCurrentContext().getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = applicationInfo.metaData;
            String apiKey = bundle.getString("com.vwo.API_KEY");

            if(vwoConfig == null) {
                vwoConfig = new VwoConfig.Builder().apiKey(apiKey).build();
            } else if(vwoConfig.getApiKey() == null) {
                vwoConfig.setApiKey(apiKey);
            }
            vwoConfig.setSync(syncMode);

            vwo.setConfig(vwoConfig);
        } catch (Exception e) {
            throw new IllegalArgumentException("Dear developer. " +
                    "Don't forget to configure <meta-data android:name=\"com.vwo.API_KEY\" " +
                    "android:value=\"YOUR_API_KEY\"/> in your AndroidManifest.xml file.");
        }
    }
}
