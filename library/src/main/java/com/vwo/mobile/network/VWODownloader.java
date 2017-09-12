package com.vwo.mobile.network;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.vwo.mobile.VWO;
import com.vwo.mobile.data.VWOData;
import com.vwo.mobile.listeners.VWOActivityLifeCycle;
import com.vwo.mobile.utils.NetworkUtils;
import com.vwo.mobile.utils.VWOLog;
import com.vwo.mobile.utils.VWOUtils;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;


/**
 * Created by abhishek on 17/09/15 at 11:39 PM.
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
        DownloadData downloadData = new DownloadData(url, downloadResult, mVWO);
        downloadData.execute();

        if (mVWO.getConfig().getTimeout() != NO_TIMEOUT) {

            try {
                if (downloadData.getStatus() != AsyncTask.Status.FINISHED) {
                    downloadData.get(mVWO.getConfig().getTimeout(), TimeUnit.MILLISECONDS);
                }
            } catch (InterruptedException exception) {
                downloadResult.onDownloadError(exception);
                VWOLog.e(VWOLog.DOWNLOAD_DATA_LOGS, "**** Data Download Interrupted ****", true, false);
            } catch (ExecutionException exception) {
                VWOLog.e(VWOLog.DOWNLOAD_DATA_LOGS, "**** Data Download Execution Exception ****", true, false);
                downloadResult.onDownloadError(exception);
            } catch (TimeoutException exception) {
                VWOLog.e(VWOLog.DOWNLOAD_DATA_LOGS, "**** Data Download Timeout ****", true, false);
                downloadResult.onDownloadError(exception);
            }

        }
    }

    public static class DownloadData extends AsyncTask<Void, Void, Void> {

        private String mUrl;
        private DownloadResult mDownloadResult;
        private VWO lVWO;

        public DownloadData(String url, DownloadResult downloadResult, VWO vwo) {
            this.mUrl = url;
            this.mDownloadResult = downloadResult;
            this.lVWO = vwo;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            if (!NetworkUtils.shouldAttemptNetworkCall(lVWO)) {
                mDownloadResult.onDownloadError(new Exception("No internet"));
                return null;
            }

            if(!VWOUtils.checkIfClassExists("okhttp3.OkHttpClient")) {
                try {
                    NetworkStringRequest request = new NetworkStringRequest(mUrl, NetworkRequest.GET,
                            NetworkUtils.Headers.getBasicHeaders(), null);
                    request.setResponseListener(new Response.Listener<String>() {
                        @Override
                        public void onResponse(@Nullable String response) {
                            try {
                                mDownloadResult.onDownloadSuccess(new JSONArray(response));
                            } catch (JSONException exception) {
                                VWOLog.e(VWOLog.DOWNLOAD_DATA_LOGS, exception, false, true);
                                mDownloadResult.onDownloadError(exception);
                            }
                        }
                    });
                    request.setErrorListener(new Response.ErrorListener() {
                        @Override
                        public void onFailure(ErrorResponse exception) {
                            VWOLog.e(VWOLog.DOWNLOAD_DATA_LOGS, exception, false, true);
                            mDownloadResult.onDownloadError(exception);
                        }
                    });
                    request.setGzipEnabled(true);
                    request.execute();
                } catch (MalformedURLException exception) {
                    VWOLog.e(VWOLog.DOWNLOAD_DATA_LOGS, exception, false, true);
                    mDownloadResult.onDownloadError(exception);
                }
            } else {
                final OkHttpClient client = new OkHttpClient.Builder().build();

                Request httpRequest = new Request.Builder().url(mUrl).build();
                try {
                    okhttp3.Response response = client.newCall(httpRequest).execute();
                    if (response.isSuccessful()) {
                        String data = response.body().string();
                        try {
                            mDownloadResult.onDownloadSuccess(new JSONArray(data));
                        } catch (JSONException e) {
                            mDownloadResult.onDownloadError(e);
                        }
                    } else {
                        mDownloadResult.onDownloadError(new IOException("Unexpected code " + response));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    mDownloadResult.onDownloadError(e);
                }
            }
            return null;
        }
    }

    public void startUpload() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
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
                    if(!VWOUtils.checkIfClassExists("okhttp3.OkHttpClient")) {
                        try {
                            NetworkStringRequest request = new NetworkStringRequest(url,
                                    NetworkRequest.GET, NetworkUtils.Headers.getBasicHeaders(), null);
                            request.setResponseListener(new Response.Listener<String>() {
                                @Override
                                public void onResponse(@Nullable String response) {
                                    VWOLog.v(VWOLog.UPLOAD_LOGS, "Completed: " + url);
                                    urls.remove(url);
                                    mVWO.getVwoPreference().putListString(VWOData.VWO_QUEUE, urls);
                                }
                            });
                            request.setErrorListener(new Response.ErrorListener() {
                                @Override
                                public void onFailure(ErrorResponse exception) {
                                    VWOLog.e(VWOLog.UPLOAD_LOGS, exception, false, true);
                                }
                            });
                            request.setGzipEnabled(true);
                            request.execute();
                        } catch (MalformedURLException exception) {
                            VWOLog.e(VWOLog.UPLOAD_LOGS, exception, false, true);
                        }
                    } else {
                        OkHttpClient client = new OkHttpClient();

                        Request request = new Request.Builder()
                                .url(url)
                                .build();

                        client.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(@NonNull Call call, @NonNull IOException exception) {
                                VWOLog.e(VWOLog.UPLOAD_LOGS, exception, true, true);
                            }

                            @Override
                            public void onResponse(@NonNull Call call, @NonNull okhttp3.Response response) throws IOException {
                                VWOLog.v(VWOLog.UPLOAD_LOGS, "Completed: " + response.request().url().toString());
                                urls.remove(url);
                                mVWO.getVwoPreference().putListString(VWOData.VWO_QUEUE, urls);
                            }
                        });
                    }
                }

            }
        }, 15000, 15000);
    }

    public interface DownloadResult {
        void onDownloadSuccess(JSONArray data);

        void onDownloadError(Exception ex);

    }


}
