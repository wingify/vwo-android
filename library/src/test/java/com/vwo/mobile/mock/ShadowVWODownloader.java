package com.vwo.mobile.mock;

import com.vwo.mobile.TestUtils;
import com.vwo.mobile.VWO;
import com.vwo.mobile.network.VWODownloader;

import org.robolectric.annotation.Implements;

import java.io.IOException;

/**
 * Created by aman on Thu 11/01/18 16:37.
 */

@Implements(value = VWODownloader.class, inheritImplementationMethods = true)
public class ShadowVWODownloader {

    private VWO vwo;

    public void __constructor__(VWO vwo) {
        this.vwo = vwo;
    }

    public void fetchFromServer(final VWODownloader.DownloadResult downloadResult) {
        String data = "";
        try {
            data = TestUtils.readJsonFile(getClass(), "com/vwo/mobile/mock/campaigns.json");
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (vwo.getConfig().getTimeout() != null && vwo.getConfig().getTimeout() != VWODownloader.NO_TIMEOUT) {
            final String finalData = data;
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(vwo.getConfig().getTimeout());
                        downloadResult.onDownloadSuccess(finalData);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });

            thread.start();
        } else {
            final String finalData = data;
            final Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    downloadResult.onDownloadSuccess(finalData);
                }
            });

            thread.start();
        }
    }
}
