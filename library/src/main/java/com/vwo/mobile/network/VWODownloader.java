package com.vwo.mobile.network;

import android.support.annotation.Nullable;

import com.vwo.mobile.VWO;
import com.vwo.mobile.data.VWOMessageQueue;
import com.vwo.mobile.listeners.VWOActivityLifeCycle;
import com.vwo.mobile.models.Entry;
import com.vwo.mobile.utils.NetworkUtils;
import com.vwo.mobile.utils.VWOLog;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


/**
 * Created by abhishek on 17/09/15 at 11:39 PM.
 * Modified by aman on 17/09/15 at 3:27 PM.
 */
public class VWODownloader {
    private final VWO mVWO;

    private static final int WARN_THRESHOLD = 2;
    private static final int DISCARD_THRESHOLD = 5;

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

    public void initializeMessageQueue() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                VWOMessageQueue messageQueue = mVWO.getMessageQueue();

                Entry entry = messageQueue.peek();

                while (entry != null) {
                    try {
                        if (!VWOActivityLifeCycle.isApplicationInForeground() || !NetworkUtils.shouldAttemptNetworkCall(mVWO)) {
                            VWOLog.e(VWOLog.UPLOAD_LOGS, "Either no network, or application is not in foreground", true, false);
                            break;
                        }
                        FutureNetworkRequest<String> futureNetworkRequest = FutureNetworkRequest.getInstance();
                        NetworkStringRequest request = new NetworkStringRequest(entry.getUrl(),
                                NetworkRequest.GET, NetworkUtils.Headers.getBasicHeaders(),
                                futureNetworkRequest, futureNetworkRequest);
                        request.setGzipEnabled(true);
                        PriorityRequestQueue.getInstance().addToQueue(request);
                        String data = futureNetworkRequest.get();
                        VWOLog.v(VWOLog.UPLOAD_LOGS, String.format("Completed Upload Request with url : %s \ndata : %s", entry.getUrl(), data));
                        messageQueue.remove();
                        entry = messageQueue.peek();
                    } catch (MalformedURLException exception) {
                        VWOLog.e(VWOLog.UPLOAD_LOGS, "Malformed url: " + entry.getUrl(),
                                exception, true, true);
                        messageQueue.remove();
                        entry = messageQueue.peek();
                    } catch (InterruptedException exception) {
                        VWOLog.e(VWOLog.UPLOAD_LOGS, exception, true, true);
                        entry.incrementRetryCount();
                        if (entry.getRetryCount() < WARN_THRESHOLD) {
                            messageQueue.add(entry);
                        } else {
                            mVWO.getFailureQueue().add(entry);
                        }
                        messageQueue.remove();
                        entry = messageQueue.peek();
                    } catch (ExecutionException exception) {
                        if(exception.getCause() != null && exception.getCause() instanceof ErrorResponse) {
                            ErrorResponse errorResponse = (ErrorResponse) exception.getCause();
                            if(errorResponse.getCause() != null && (errorResponse.getCause() instanceof IOException ||
                                    errorResponse.getCause() instanceof ConnectException)) {
                                VWOLog.e(VWOLog.UPLOAD_LOGS, "Throwing IO exception and exiting", exception, true, false);
                                break;
                            } else {
                                VWOLog.e(VWOLog.UPLOAD_LOGS, exception, true, true);
                                checkMessageQueueEntryStatus(entry, messageQueue, mVWO.getFailureQueue());
                                entry = messageQueue.peek();
                            }
                        } else {
                            VWOLog.e(VWOLog.UPLOAD_LOGS, exception, true, true);
                            checkMessageQueueEntryStatus(entry, messageQueue, mVWO.getFailureQueue());
                            entry = messageQueue.peek();
                        }
                    }
                }
            }
        };

        ScheduledRequestQueue scheduledRequestQueue =  new ScheduledRequestQueue();
        scheduledRequestQueue.scheduleWithFixedDelay(runnable, 5,
                5, TimeUnit.SECONDS);
    }

    private void checkMessageQueueEntryStatus(Entry entry, VWOMessageQueue messageQueue, VWOMessageQueue failureQueue) {
        entry.incrementRetryCount();
        if (entry.getRetryCount() < WARN_THRESHOLD) {
            messageQueue.add(entry);
        } else {
            failureQueue.add(entry);
        }
        messageQueue.remove();
    }

    public void initializeFailureQueue() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                VWOMessageQueue failureQueue = mVWO.getFailureQueue();
                int taskCount = failureQueue.size();
                VWOLog.i(VWOLog.UPLOAD_LOGS, "Flushing failure message queue of size : " + taskCount, true);
                for (int i = 0; i < taskCount; i++) {
                    Entry entry = failureQueue.peek();
                    if (entry == null) {
                        break;
                    }
                    try {
                        if (!VWOActivityLifeCycle.isApplicationInForeground() || !NetworkUtils.shouldAttemptNetworkCall(mVWO)) {
                            VWOLog.e(VWOLog.UPLOAD_LOGS, "Either no network, or application is not in foreground", true, false);
                            break;
                        }
                        FutureNetworkRequest<String> futureNetworkRequest = FutureNetworkRequest.getInstance();
                        NetworkStringRequest request = new NetworkStringRequest(entry.getUrl(),
                                NetworkRequest.GET, NetworkUtils.Headers.getBasicHeaders(),
                                futureNetworkRequest, futureNetworkRequest);
                        request.setGzipEnabled(true);
                        PriorityRequestQueue.getInstance().addToQueue(request);
                        String data = futureNetworkRequest.get();
                        VWOLog.v(VWOLog.UPLOAD_LOGS, String.format("Completed Upload Request with url : %s \ndata : %s", entry.getUrl(), data));
                        failureQueue.remove();
                    } catch (MalformedURLException exception) {
                        VWOLog.e(VWOLog.UPLOAD_LOGS, "Malformed url: " + entry.getUrl(),
                                exception, true, true);
                        failureQueue.remove();
                    } catch (InterruptedException | ExecutionException exception) {
                        if(exception.getCause() != null && exception.getCause() instanceof ErrorResponse) {
                            ErrorResponse errorResponse = (ErrorResponse) exception.getCause();
                            if(errorResponse.getCause() != null && (errorResponse.getCause() instanceof IOException ||
                                    errorResponse.getCause() instanceof ConnectException)) {
                                VWOLog.e(VWOLog.UPLOAD_LOGS, "Throwing IO exception and exiting", exception, true, false);
                                break;
                            } else {
                                VWOLog.e(VWOLog.UPLOAD_LOGS, exception, true, true);
                                checkFailureQueueEntryStatus(entry, failureQueue);
                            }
                        } else {
                            VWOLog.e(VWOLog.UPLOAD_LOGS, exception, true, true);
                            checkFailureQueueEntryStatus(entry, failureQueue);
                        }
                    }
                }
            }
        };

        ScheduledThreadPoolExecutor scheduledRequestQueue = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(1);
        scheduledRequestQueue.scheduleWithFixedDelay(runnable, 5,
                5, TimeUnit.SECONDS);
    }

    private void checkFailureQueueEntryStatus(Entry entry, VWOMessageQueue failureQueue) {
        entry.incrementRetryCount();

        if (entry.getRetryCount() < DISCARD_THRESHOLD) {
            failureQueue.add(entry);
        } else {
            VWOLog.e(VWOLog.UPLOAD_LOGS, "discarding entry : " + entry.toString(),
                    true, true);
        }
        failureQueue.remove();
    }

    public interface DownloadResult {
        void onDownloadSuccess(JSONArray data);

        void onDownloadError(Exception ex);

    }
}
