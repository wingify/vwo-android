package com.vwo.mobile.mock;

import com.vwo.mobile.TestUtils;
import com.vwo.mobile.VWO;
import com.vwo.mobile.network.VWODownloader;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;

import java.io.IOException;

/**
 * Created by aman on Thu 11/01/18 16:37.
 */

@Implements(value = VWODownloader.class)
public class ShadowVWODownloader {

    @Implementation
    public static void fetchFromServer(VWO vwo, final VWODownloader.DownloadResult downloadResult) {
        String data = "";
        try {
            data = TestUtils.readJsonFile(vwo.getCurrentContext().getClass(), "com/vwo/mobile/mock/campaigns.json");
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (vwo.getConfig().getTimeout() != null && vwo.getConfig().getTimeout() != VWODownloader.NO_TIMEOUT) {
            try {
                Thread.sleep(vwo.getConfig().getTimeout());
                downloadResult.onDownloadSuccess(data);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        } else {
            final String finalData = data;
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    downloadResult.onDownloadSuccess(finalData);
                }
            });
            thread.start();
        }
    }
}
