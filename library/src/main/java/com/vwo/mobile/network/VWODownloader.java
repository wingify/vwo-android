package com.vwo.mobile.network;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.vwo.mobile.VWO;
import com.vwo.mobile.data.VWOMessageQueue;
import com.vwo.mobile.listeners.VWOActivityLifeCycle;
import com.vwo.mobile.models.Entry;
import com.vwo.mobile.utils.NetworkUtils;
import com.vwo.mobile.utils.VWOLog;

import java.io.IOException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


/**
 * Created by abhishek on 17/09/15 at 11:39 PM.
 */
public class VWODownloader {
    private final VWO mVWO;

    private static final int WARN_THRESHOLD = 3;
    private static final int DISCARD_THRESHOLD = 20;

    public static final long NO_TIMEOUT = -1;

    public VWODownloader(VWO vwo) {
        mVWO = vwo;
    }

    public void fetchFromServer(final DownloadResult downloadResult) {

        String url = mVWO.getVwoUrlBuilder().getDownloadUrl();
        VWOLog.i(VWOLog.URL_LOGS, "Fetching data from: " + url, true);

        if (mVWO.getConfig().getTimeout() != NO_TIMEOUT) {
            try {
                downloadResult.onDownloadSuccess(downloadDataSynchronous(url, downloadResult, mVWO));
            } catch (InterruptedException exception) {
                String message = "Request timed out";
                downloadResult.onDownloadError(exception, message);
                VWOLog.e(VWOLog.DOWNLOAD_DATA_LOGS, "**** Request timed out or thread interrupted ****", true, false);
            } catch (ExecutionException exception) {
                String message;
                if (exception.getCause() != null && exception.getCause() instanceof ErrorResponse) {
                    ErrorResponse errorResponse = (ErrorResponse) exception.getCause();
                    if (errorResponse.getCause() != null && (errorResponse.getCause() instanceof IOException ||
                            errorResponse.getCause() instanceof ConnectException)) {
                        message = "Either no internet connectivity or internet is very slow";
                        VWOLog.e(VWOLog.UPLOAD_LOGS, "Either no internet connectivity or internet is very slow",
                                exception, true, false);
                    } else {
                        message = "Something went wrong";
                        VWOLog.e(VWOLog.DOWNLOAD_DATA_LOGS, "**** Data Download Execution Exception ****", true, false);
                    }
                } else {
                    message = "Something went wrong";
                    VWOLog.e(VWOLog.DOWNLOAD_DATA_LOGS, "**** Data Download Execution Exception ****", true, false);
                }
                downloadResult.onDownloadError(exception, message);
            } catch (TimeoutException exception) {
                String message = "Request timed out";
                VWOLog.e(VWOLog.DOWNLOAD_DATA_LOGS, "**** Data Download Timeout ****", false, false);
                downloadResult.onDownloadError(exception, message);
            } catch (MalformedURLException exception) {
                VWOLog.e(VWOLog.DOWNLOAD_DATA_LOGS, "**** Invalid Url : " + url, true, true);
                downloadResult.onDownloadError(exception, "Invalid download url");
            }
        } else {
            downloadData(url, downloadResult, mVWO);
        }
    }

    private String downloadDataSynchronous(String url, final DownloadResult downloadResult, VWO vwo)
            throws MalformedURLException, InterruptedException, ExecutionException, TimeoutException {
        if (!NetworkUtils.shouldAttemptNetworkCall(vwo)) {
            downloadResult.onDownloadError(new ConnectException("No internet connectivity"), "No Internet Connectivity");
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
            downloadResult.onDownloadError(new ConnectException("No internet connectivity"), "No Internet Connectivity");
            return;
        }
        try {
            NetworkStringRequest request = new NetworkStringRequest(url, NetworkRequest.GET,
                    NetworkUtils.Headers.getBasicHeaders(), new Response.Listener<String>() {

                @Override
                public void onResponse(@NonNull NetworkRequest<String> networkRequest, @Nullable String response) {
                    downloadResult.onDownloadSuccess(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onFailure(ErrorResponse errorResponse) {
                    String message;
                    if (errorResponse.getCause() != null && (errorResponse.getCause() instanceof IOException ||
                            errorResponse.getCause() instanceof ConnectException)) {
                        message = "Either no internet connectivity or internet is very slow";
                        VWOLog.e(VWOLog.UPLOAD_LOGS, "Either no internet connectivity or internet is very slow",
                                errorResponse, true, false);
                    } else {
                        message = "Something went wrong";
                        VWOLog.e(VWOLog.DOWNLOAD_DATA_LOGS, "Something went wrong", errorResponse, true, false);
                    }
                    downloadResult.onDownloadError(errorResponse, message);
                }
            });
            request.setGzipEnabled(true);
            PriorityRequestQueue.getInstance().addToQueue(request);
        } catch (MalformedURLException exception) {
            VWOLog.e(VWOLog.DOWNLOAD_DATA_LOGS, exception, true, true);
            downloadResult.onDownloadError(exception, "Invalid download url");
        }
    }

    public void initializeMessageQueue() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final VWOMessageQueue messageQueue = mVWO.getMessageQueue();
                final VWOMessageQueue failureQueue = mVWO.getFailureQueue();
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
                        messageQueue.remove();
                        if (entry.getRetryCount() < WARN_THRESHOLD) {
                            messageQueue.add(entry);
                        } else {
                            failureQueue.add(entry);
                        }
                        entry = messageQueue.peek();
                    } catch (ExecutionException exception) {
                        if (exception.getCause() != null && exception.getCause() instanceof ErrorResponse) {
                            ErrorResponse errorResponse = (ErrorResponse) exception.getCause();
                            if (errorResponse.getCause() != null && (errorResponse.getCause() instanceof IOException ||
                                    errorResponse.getCause() instanceof ConnectException)) {
                                VWOLog.e(VWOLog.UPLOAD_LOGS, "Either no internet connectivity or internet is very slow",
                                        exception, true, false);
                                break;
                            } else {
                                VWOLog.e(VWOLog.UPLOAD_LOGS, exception, true, true);
                                checkMessageQueueEntryStatus(entry, messageQueue, failureQueue);
                                entry = messageQueue.peek();
                            }
                        } else {
                            VWOLog.e(VWOLog.UPLOAD_LOGS, exception, true, true);
                            checkMessageQueueEntryStatus(entry, messageQueue, failureQueue);
                            entry = messageQueue.peek();
                        }
                    }
                }
            }
        };

        ScheduledRequestQueue scheduledRequestQueue = ScheduledRequestQueue.getInstance("message queue");
        if (!scheduledRequestQueue.isRunning()) {
            VWOLog.w(VWOLog.UPLOAD_LOGS, "Starting new Scheduler", true);
            scheduledRequestQueue.scheduleWithFixedDelay(runnable, 15,
                    15, TimeUnit.SECONDS);
            scheduledRequestQueue.setRunning(true);
        } else {
            VWOLog.w(VWOLog.UPLOAD_LOGS, "Scheduler already running", true);
        }
    }

    private void checkMessageQueueEntryStatus(Entry entry, VWOMessageQueue messageQueue, VWOMessageQueue failureQueue) {
        entry.incrementRetryCount();
        messageQueue.remove();
        if (entry.getRetryCount() < WARN_THRESHOLD) {
            messageQueue.add(entry);
        } else {
            failureQueue.add(entry);
        }
    }

    public void initializeFailureQueue() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final VWOMessageQueue failureQueue = mVWO.getFailureQueue();
                int taskCount = failureQueue.size();
                VWOLog.i(VWOLog.UPLOAD_LOGS, "Flushing failure message queue of size : " + taskCount, true);
                for (int i = 0; i < taskCount; i++) {
                    Entry entry = failureQueue.peek();
                    if (entry == null) {
                        break;
                    }
                    try {
                        if (!NetworkUtils.shouldAttemptNetworkCall(mVWO)) {
                            VWOLog.e(VWOLog.UPLOAD_LOGS, "No internet connectivity", true, false);
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
                        if (exception.getCause() != null && exception.getCause() instanceof ErrorResponse) {
                            ErrorResponse errorResponse = (ErrorResponse) exception.getCause();
                            if (errorResponse.getCause() != null && (errorResponse.getCause() instanceof IOException ||
                                    errorResponse.getCause() instanceof ConnectException)) {
                                VWOLog.e(VWOLog.UPLOAD_LOGS, "Either no internet connectivity or internet is very slow",
                                        exception, true, false);
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

        ScheduledRequestQueue scheduledRequestQueue = ScheduledRequestQueue.getInstance("failure queue");

        if (!scheduledRequestQueue.isRunning()) {
            VWOLog.v(VWOLog.UPLOAD_LOGS, "Starting failed message queue scheduler");
            scheduledRequestQueue.scheduleWithFixedDelay(runnable, 30,
                    300, TimeUnit.SECONDS);
            scheduledRequestQueue.setRunning(true);
        } else {
            VWOLog.v(VWOLog.UPLOAD_LOGS, "Failed message queue scheduler already running");
        }
    }

    private void checkFailureQueueEntryStatus(Entry entry, VWOMessageQueue failureQueue) {
        entry.incrementRetryCount();
        failureQueue.remove();
        if (entry.getRetryCount() < DISCARD_THRESHOLD) {
            failureQueue.add(entry);
        } else {
            VWOLog.e(VWOLog.UPLOAD_LOGS, "discarding entry : " + entry.toString(),
                    true, true);
        }
    }

    public interface DownloadResult {
        void onDownloadSuccess(@Nullable String data);

        void onDownloadError(@Nullable Exception ex, @Nullable String message);

    }
}
