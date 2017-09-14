package com.vwo.mobile.network;

import android.support.annotation.Nullable;

import com.vwo.mobile.VWO;
import com.vwo.mobile.data.VWOData;
import com.vwo.mobile.listeners.VWOActivityLifeCycle;
import com.vwo.mobile.utils.NetworkUtils;
import com.vwo.mobile.utils.VWOLog;

import org.json.JSONArray;
import org.json.JSONException;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


/**
 * Created by abhishek on 17/09/15 at 11:39 PM.
 * Modified by aman on 17/09/15 at 11:39 PM 3:27 PM.
 */
public class VWODownloader {
    private final VWO mVWO;
    public static final long NO_TIMEOUT = -1;

    public VWODownloader(VWO vwo) {
        mVWO = vwo;
    }

    public void fetchFromServer(final DownloadResult downloadResult) {

        String url = mVWO.getVwoUrlBuilder().getDownloadUrl();
        VWOLog.i(VWOLog.URL_LOGS, "Fetching data from: " + url, true);

        if (mVWO.getConfig().getTimeout() != NO_TIMEOUT) {
            try {
                downloadResult.onDownloadSuccess(new JSONArray(downloadDataSynchronous(url, downloadResult, mVWO)));
            } catch (InterruptedException exception) {
                downloadResult.onDownloadError(exception);
                VWOLog.e(VWOLog.DOWNLOAD_DATA_LOGS, "**** Data Download Interrupted ****", true, false);
            } catch (ExecutionException exception) {
                VWOLog.e(VWOLog.DOWNLOAD_DATA_LOGS, "**** Data Download Execution Exception ****", true, false);
                downloadResult.onDownloadError(exception);
            } catch (TimeoutException exception) {
                VWOLog.e(VWOLog.DOWNLOAD_DATA_LOGS, "**** Data Download Timeout ****", true, false);
                downloadResult.onDownloadError(exception);
            } catch (MalformedURLException exception) {
                VWOLog.e(VWOLog.DOWNLOAD_DATA_LOGS, "**** Invalid Url : " + url, false, true);
                downloadResult.onDownloadError(exception);
            } catch (JSONException exception) {
                VWOLog.e(VWOLog.DOWNLOAD_DATA_LOGS, "**** Unable to parse data.", false, true);
                downloadResult.onDownloadError(exception);
            }
        } else {
            downloadData(url, downloadResult, mVWO);
        }
    }

    private String downloadDataSynchronous(String url, final DownloadResult downloadResult, VWO vwo)
            throws MalformedURLException, InterruptedException, ExecutionException, TimeoutException {
        if (!NetworkUtils.shouldAttemptNetworkCall(vwo)) {
            downloadResult.onDownloadError(new Exception("No internet"));
            return null;
        }

        FutureNetworkRequest<String> futureNetworkRequest = FutureNetworkRequest.getInstance();
        NetworkStringRequest request = new NetworkStringRequest(url, NetworkRequest.GET,
                NetworkUtils.Headers.getBasicHeaders(), futureNetworkRequest, futureNetworkRequest);
        request.setGzipEnabled(true);
        PriorityRequestQueue.getInstance().addToQueue(request);
        return futureNetworkRequest.get(vwo.getConfig().getTimeout(), TimeUnit.MILLISECONDS);
    }

    private void downloadData(String url, final DownloadResult downloadResult, VWO vwo) {
        if (!NetworkUtils.shouldAttemptNetworkCall(vwo)) {
            downloadResult.onDownloadError(new Exception("No internet"));
            return;
        }
        try {
            NetworkStringRequest request = new NetworkStringRequest(url, NetworkRequest.GET,
                    NetworkUtils.Headers.getBasicHeaders(), new Response.Listener<String>() {
                @Override
                public void onResponse(@Nullable String response) {
                    try {
                        downloadResult.onDownloadSuccess(new JSONArray(response));
                    } catch (JSONException exception) {
                        VWOLog.e(VWOLog.DOWNLOAD_DATA_LOGS, exception, false, true);
                        downloadResult.onDownloadError(exception);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onFailure(ErrorResponse exception) {
                    VWOLog.e(VWOLog.DOWNLOAD_DATA_LOGS, exception, false, true);
                    downloadResult.onDownloadError(exception);
                }
            });
            request.setGzipEnabled(true);
            PriorityRequestQueue.getInstance().addToQueue(request);
        } catch (MalformedURLException exception) {
            VWOLog.e(VWOLog.DOWNLOAD_DATA_LOGS, exception, false, true);
            downloadResult.onDownloadError(exception);
        }
    }

    public void initializeVWOUploadScheduler() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final ArrayList<String> urls = mVWO.getVwoPreference().getListString(VWOData.VWO_QUEUE);

                if (urls.size() != 0) {
                    VWOLog.v(VWOLog.UPLOAD_LOGS, String.format(Locale.ENGLISH, "%d pending URLS", urls.size()));
                }

                if (!VWOActivityLifeCycle.isApplicationInForeground() || !NetworkUtils.shouldAttemptNetworkCall(mVWO)) {
                    VWOLog.e(VWOLog.UPLOAD_LOGS, "Either no network, or application is not in foreground", true, false);
                    return;
                }

                for (final String url : urls) {
                    try {
                        FutureNetworkRequest<String> futureNetworkRequest = FutureNetworkRequest.getInstance();
                        NetworkStringRequest request = new NetworkStringRequest(url,
                                NetworkRequest.GET, NetworkUtils.Headers.getBasicHeaders(),
                                futureNetworkRequest, futureNetworkRequest);
                        request.setGzipEnabled(true);
                        PriorityRequestQueue.getInstance().addToQueue(request);
                        String data = futureNetworkRequest.get();
                        VWOLog.v(VWOLog.UPLOAD_LOGS, String.format("Completed Upload Request with url : %s \ndata : %s", url, data));
                        urls.remove(url);
                        mVWO.getVwoPreference().putListString(VWOData.VWO_QUEUE, urls);
                    } catch (MalformedURLException | InterruptedException | ExecutionException exception) {
                        VWOLog.e(VWOLog.UPLOAD_LOGS, exception, false, true);
                    }
                }
            }
        };

        ScheduledRequestQueue scheduledRequestQueue = ScheduledRequestQueue.getInstance();
        scheduledRequestQueue.scheduleAtFixedRate(runnable,15,
                15, TimeUnit.SECONDS);

    }

    public interface DownloadResult {
        void onDownloadSuccess(JSONArray data);

        void onDownloadError(Exception ex);

    }
}
