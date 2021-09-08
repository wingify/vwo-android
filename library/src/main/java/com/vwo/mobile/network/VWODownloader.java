package com.vwo.mobile.network;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.vwo.mobile.VWO;
import com.vwo.mobile.data.VWOMessageQueue;
import com.vwo.mobile.listeners.VWOActivityLifeCycle;
import com.vwo.mobile.models.Entry;
import com.vwo.mobile.models.VWOError;
import com.vwo.mobile.utils.NetworkUtils;
import com.vwo.mobile.utils.VWOLog;

import org.json.JSONException;

import java.io.IOException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class VWODownloader {

    private static final int WARN_THRESHOLD = 3;
    private static final int DISCARD_THRESHOLD = 10;
    private static final int LOGGING_DISCARD_THRESHOLD = 5;

    public static final long NO_TIMEOUT = -1;

    private VWODownloader() {
    }

    public static void fetchFromServer(VWO vwo, final DownloadResult downloadResult) {

        String url = vwo.getVwoUrlBuilder().getDownloadUrl();
        VWOLog.v(VWOLog.URL_LOGS, "Fetching data from: " + url);

        if (vwo.getConfig().getTimeout() != null) {
            try {
                downloadResult.onDownloadSuccess(downloadDataSynchronous(url, downloadResult, vwo));
            } catch (InterruptedException exception) {
                String message = "Request timed out";
                downloadResult.onDownloadError(exception, message);
                VWOLog.e(VWOLog.NETWORK_LOGS, "**** Request timed out or thread interrupted ****", true, false);
            } catch (ExecutionException exception) {
                String message;
                if (exception.getCause() != null && exception.getCause() instanceof ErrorResponse) {
                    ErrorResponse errorResponse = (ErrorResponse) exception.getCause();
                    if (errorResponse.getCause() != null && (errorResponse.getCause() instanceof IOException ||
                            errorResponse.getCause() instanceof ConnectException)) {
                        message = "Either no internet connectivity or internet is very slow";
                        VWOLog.e(VWOLog.NETWORK_LOGS, "Either no internet connectivity or internet is very slow",
                                exception, true, false);
                    } else {
                        message = "Something went wrong";
                        VWOLog.e(VWOLog.NETWORK_LOGS, "**** Data Download Execution Exception ****", true, false);
                    }
                } else {
                    message = "Something went wrong";
                    VWOLog.e(VWOLog.NETWORK_LOGS, "**** Data Download Execution Exception ****", true, false);
                }
                downloadResult.onDownloadError(exception, message);
            } catch (TimeoutException exception) {
                String message = "Request timed out";
                VWOLog.e(VWOLog.NETWORK_LOGS, "**** Data Download Timeout ****", false, false);
                downloadResult.onDownloadError(exception, message);
            } catch (MalformedURLException exception) {
                VWOLog.e(VWOLog.NETWORK_LOGS, "**** Invalid Url : " + url, true, true);
                downloadResult.onDownloadError(exception, "Invalid download url");
            }
        } else {
            downloadData(url, downloadResult, vwo);
        }
    }

    private static String downloadDataSynchronous(String url, final DownloadResult downloadResult, VWO vwo)
            throws MalformedURLException, InterruptedException, ExecutionException, TimeoutException {
        if (!NetworkUtils.shouldAttemptNetworkCall(vwo.getCurrentContext())) {
            downloadResult.onDownloadError(new ConnectException("No internet connectivity"), "No Internet Connectivity");
            return null;
        }

        FutureNetworkRequest<String> futureNetworkRequest = FutureNetworkRequest.getInstance();
        NetworkStringRequest request = new NetworkStringRequest(url, NetworkRequest.GET,
                NetworkUtils.Headers.getBasicHeaders(), futureNetworkRequest, futureNetworkRequest);
        request.setGzipEnabled(true);
        PriorityRequestQueue.getInstance().addToQueue(request);
        assert vwo.getConfig().getTimeout() != null;
        return futureNetworkRequest.get(vwo.getConfig().getTimeout(), TimeUnit.MILLISECONDS);
    }

    private static void downloadData(String url, final DownloadResult downloadResult, VWO vwo) {
        if (!NetworkUtils.shouldAttemptNetworkCall(vwo.getCurrentContext())) {
            downloadResult.onDownloadError(new ConnectException("No internet connectivity"), "No Internet Connectivity");
            return;
        }
        try {
            NetworkStringRequest request = new NetworkStringRequest(url, NetworkRequest.GET,
                    NetworkUtils.Headers.getBasicHeaders(), new Response.Listener<String>() {
                @Override
                public void onResponse(@NonNull NetworkRequest<String> request, @Nullable String response) {
                    downloadResult.onDownloadSuccess(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onFailure(ErrorResponse errorResponse) {
                    String message;
                    if (errorResponse.getCause() != null && (errorResponse.getCause() instanceof IOException ||
                            errorResponse.getCause() instanceof ConnectException)) {
                        message = "Either no internet connectivity or internet is very slow";
                        VWOLog.e(VWOLog.NETWORK_LOGS, message,
                                errorResponse, true, false);
                    } else {
                        message = "Something went wrong";
                        VWOLog.e(VWOLog.NETWORK_LOGS, message, errorResponse, true, false);
                    }
                    downloadResult.onDownloadError(new Exception(errorResponse), message);
                }
            });
            request.setGzipEnabled(true);
            PriorityRequestQueue.getInstance().addToQueue(request);
        } catch (MalformedURLException exception) {
            VWOLog.e(VWOLog.NETWORK_LOGS, exception, true, true);
            downloadResult.onDownloadError(exception, "Invalid download url");
        }
    }

    public static void initializeMessageQueue(final VWO vwo) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final VWOMessageQueue messageQueue = vwo.getMessageQueue();
                final VWOMessageQueue failureQueue = vwo.getFailureQueue();
                Entry entry = messageQueue.peek();

                while (entry != null) {
                    try {
                        if (!VWOActivityLifeCycle.isApplicationInForeground() || !NetworkUtils.shouldAttemptNetworkCall(vwo.getCurrentContext())) {
                            VWOLog.e(VWOLog.NETWORK_LOGS, "Either no network, or application is not in foreground", true, false);
                            break;
                        }
                        FutureNetworkRequest<String> futureNetworkRequest = FutureNetworkRequest.getInstance();
                        NetworkStringRequest request = new NetworkStringRequest(entry.getUrl(),
                                NetworkRequest.GET, NetworkUtils.Headers.getBasicHeaders(),
                                futureNetworkRequest, futureNetworkRequest);
                        request.setGzipEnabled(true);
                        PriorityRequestQueue.getInstance().addToQueue(request);
                        String data = futureNetworkRequest.get();
                        VWOLog.v(VWOLog.NETWORK_LOGS, String.format("Completed Upload Request with url : %s \ndata : %s", entry.getUrl(), data));
                        messageQueue.remove();
                        entry = messageQueue.peek();
                    } catch (MalformedURLException exception) {
                        VWOLog.e(VWOLog.NETWORK_LOGS, "Malformed url: " + entry.getUrl(),
                                exception, true, true);
                        messageQueue.remove();
                        entry = messageQueue.peek();
                    } catch (InterruptedException exception) {
                        VWOLog.e(VWOLog.NETWORK_LOGS, exception, true, false);
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
                                VWOLog.e(VWOLog.NETWORK_LOGS, "Either no internet connectivity or internet is very slow",
                                        exception, true, false);
                                break;
                            } else {
                                VWOLog.e(VWOLog.NETWORK_LOGS, exception, true, true);
                                checkMessageQueueEntryStatus(entry, messageQueue, failureQueue);
                                entry = messageQueue.peek();
                            }
                        } else {
                            VWOLog.e(VWOLog.NETWORK_LOGS, exception, true, true);
                            checkMessageQueueEntryStatus(entry, messageQueue, failureQueue);
                            entry = messageQueue.peek();
                        }
                    }
                }
            }
        };

        ScheduledRequestQueue scheduledRequestQueue = ScheduledRequestQueue.getInstance("message queue");
        if (!scheduledRequestQueue.isRunning()) {
            VWOLog.w(VWOLog.NETWORK_LOGS, "Starting new Scheduler", true);
            scheduledRequestQueue.scheduleWithFixedDelay(runnable, 5,
                    15, TimeUnit.SECONDS);
            scheduledRequestQueue.setRunning(true);
        } else {
            VWOLog.w(VWOLog.NETWORK_LOGS, "Scheduler already running", true);
        }
    }

    private static void checkMessageQueueEntryStatus(Entry entry, VWOMessageQueue messageQueue, VWOMessageQueue failureQueue) {
        entry.incrementRetryCount();
        messageQueue.remove();
        if (entry.getRetryCount() < WARN_THRESHOLD) {
            messageQueue.add(entry);
        } else {
            failureQueue.add(entry);
        }
    }

    public static void initializeFailureQueue(final VWO vwo) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final VWOMessageQueue failureQueue = vwo.getFailureQueue();
                int taskCount = failureQueue.size();
                VWOLog.i(VWOLog.NETWORK_LOGS, "Flushing failure message queue of size : " + taskCount, true);
                for (int i = 0; i < taskCount; i++) {
                    Entry entry = failureQueue.peek();
                    if (entry == null) {
                        break;
                    }
                    try {
                        if (!VWOActivityLifeCycle.isApplicationInForeground() || !NetworkUtils.shouldAttemptNetworkCall(vwo.getCurrentContext())) {
                            VWOLog.e(VWOLog.NETWORK_LOGS, "Either no network, or application is not in foreground", true, false);
                            break;
                        }
                        FutureNetworkRequest<String> futureNetworkRequest = FutureNetworkRequest.getInstance();
                        NetworkStringRequest request = new NetworkStringRequest(entry.getUrl(),
                                NetworkRequest.GET, NetworkUtils.Headers.getBasicHeaders(),
                                futureNetworkRequest, futureNetworkRequest);
                        request.setGzipEnabled(true);
                        PriorityRequestQueue.getInstance().addToQueue(request);
                        String data = futureNetworkRequest.get();
                        VWOLog.v(VWOLog.NETWORK_LOGS, String.format("Completed Upload Request with url : %s \ndata : %s", entry.getUrl(), data));
                        failureQueue.remove();
                    } catch (MalformedURLException exception) {
                        VWOLog.e(VWOLog.NETWORK_LOGS, "Malformed url: " + entry.getUrl(),
                                exception, true, true);
                        failureQueue.remove();
                    } catch (InterruptedException | ExecutionException exception) {
                        if (exception.getCause() != null && exception.getCause() instanceof ErrorResponse) {
                            ErrorResponse errorResponse = (ErrorResponse) exception.getCause();
                            if (errorResponse.getCause() != null && (errorResponse.getCause() instanceof IOException ||
                                    errorResponse.getCause() instanceof ConnectException)) {
                                VWOLog.e(VWOLog.NETWORK_LOGS, "Either no internet connectivity or internet is very slow",
                                        exception, true, false);
                                break;
                            } else {
                                VWOLog.e(VWOLog.NETWORK_LOGS, exception, true, true);
                                checkFailureQueueEntryStatus(entry, failureQueue);
                            }
                        } else {
                            VWOLog.e(VWOLog.NETWORK_LOGS, exception, true, true);
                            checkFailureQueueEntryStatus(entry, failureQueue);
                        }
                    }
                }
            }
        };

        ScheduledRequestQueue scheduledRequestQueue = ScheduledRequestQueue.getInstance("failure queue");

        if (!scheduledRequestQueue.isRunning()) {
            VWOLog.v(VWOLog.NETWORK_LOGS, "Starting failed message queue scheduler");
            scheduledRequestQueue.scheduleWithFixedDelay(runnable, 5,
                    60, TimeUnit.SECONDS);
            scheduledRequestQueue.setRunning(true);
        } else {
            VWOLog.v(VWOLog.NETWORK_LOGS, "Failed message queue scheduler already running");
        }
    }

    private static void checkFailureQueueEntryStatus(Entry entry, VWOMessageQueue failureQueue) {
        entry.incrementRetryCount();
        failureQueue.remove();
        if (entry.getRetryCount() < DISCARD_THRESHOLD) {
            failureQueue.add(entry);
        } else {
            VWOLog.e(VWOLog.NETWORK_LOGS, "discarding entry : " + entry.toString(),
                    true, false);
        }
    }

    public static void scheduleLoggingQueue(final VWO vwo, final VWOMessageQueue loggingQueue) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                int taskCount = loggingQueue.size();
                VWOLog.i(VWOLog.NETWORK_LOGS, "Flushing logging queue of size : " + taskCount, true);
                Entry entry = loggingQueue.peek();

                while (entry != null) {
                    try {
                        if (!VWOActivityLifeCycle.isApplicationInForeground() || !NetworkUtils.shouldAttemptNetworkCall(vwo.getCurrentContext())) {
                            VWOLog.e(VWOLog.NETWORK_LOGS, "Either no network, or application is not in foreground", true, false);
                            break;
                        }

                        VWOError error = (VWOError) entry;
                        FutureNetworkRequest<String> futureNetworkRequest = FutureNetworkRequest.getInstance();
                        NetworkStringRequest request = new NetworkStringRequest(error.getUrl(),
                                NetworkRequest.POST, NetworkUtils.Headers.getAuthHeaders(vwo.getConfig().getAccountId(),
                                vwo.getConfig().getAppKey()), error.getErrorAsJSON().toString(),
                                futureNetworkRequest, futureNetworkRequest);
                        request.setGzipEnabled(true);
                        PriorityRequestQueue.getInstance().addToQueue(request);
                        String response = futureNetworkRequest.get();
                        VWOLog.v(VWOLog.NETWORK_LOGS, String.format("Logging error completed Request with data : %s \nand Response: %s",
                                error.getErrorAsJSON().toString(), response));
                        loggingQueue.remove();
                        entry = loggingQueue.peek();
                    } catch (MalformedURLException exception) {
                        VWOLog.e(VWOLog.NETWORK_LOGS, "Malformed url: " + entry.getUrl(),
                                exception, true, false);
                        loggingQueue.remove();
                        entry = loggingQueue.peek();
                    } catch (InterruptedException exception) {
                        VWOLog.e(VWOLog.NETWORK_LOGS, exception, true, false);
                        entry.incrementRetryCount();
                        break;
                    } catch (ExecutionException exception) {
                        if (exception.getCause() != null && exception.getCause() instanceof ErrorResponse) {
                            ErrorResponse errorResponse = (ErrorResponse) exception.getCause();
                            if (errorResponse.getCause() != null && (errorResponse.getCause() instanceof IOException ||
                                    errorResponse.getCause() instanceof ConnectException)) {
                                VWOLog.e(VWOLog.NETWORK_LOGS, "Either no internet connectivity or internet is very slow",
                                        exception, true, false);
                                break;
                            } else {
                                VWOLog.e(VWOLog.NETWORK_LOGS, exception, true, false);
                                checkLoggingQueueEntryStatus(entry, loggingQueue);
                                entry = loggingQueue.peek();
                            }
                        } else {
                            VWOLog.e(VWOLog.NETWORK_LOGS, exception, true, false);
                            checkLoggingQueueEntryStatus(entry, loggingQueue);
                            entry = loggingQueue.peek();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        loggingQueue.remove();
                        entry = loggingQueue.peek();
                    }
                }
            }
        };

        ScheduledRequestQueue scheduledRequestQueue = ScheduledRequestQueue.getInstance("Logging queue");

        if (!scheduledRequestQueue.isRunning()) {
            VWOLog.v(VWOLog.NETWORK_LOGS, "Starting Logging message queue scheduler");
            scheduledRequestQueue.scheduleWithFixedDelay(runnable, 5,
                    60, TimeUnit.SECONDS);
            scheduledRequestQueue.setRunning(true);
        } else {
            VWOLog.v(VWOLog.NETWORK_LOGS, "Logging message queue scheduler already running");
        }
    }

    private static void checkLoggingQueueEntryStatus(Entry entry, VWOMessageQueue loggingQueue) {
        entry.incrementRetryCount();
        loggingQueue.remove();
        if (entry.getRetryCount() < LOGGING_DISCARD_THRESHOLD) {
            loggingQueue.add(entry);
        } else {
            VWOLog.e(VWOLog.NETWORK_LOGS, "discarding entry : " + entry.toString(),
                    true, false);
        }
    }

    public interface DownloadResult {
        void onDownloadSuccess(@Nullable String data);

        void onDownloadError(@Nullable Exception ex, @Nullable String message);

    }
}
