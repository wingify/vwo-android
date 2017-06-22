package com.vwo.mobile.network;

import android.os.AsyncTask;

import com.vwo.mobile.Vwo;
import com.vwo.mobile.data.VwoData;
import com.vwo.mobile.listeners.VwoActivityLifeCycle;
import com.vwo.mobile.utils.NetworkUtils;
import com.vwo.mobile.utils.VWOLogger;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by abhishek on 17/09/15 at 11:39 PM.
 */
public class VwoDownloader {
    private static final Logger LOGGER = VWOLogger.getLogger(VwoDownloader.class.getCanonicalName());

    private final Vwo mVwo;

    public VwoDownloader(Vwo vwo) {
        mVwo = vwo;
    }

    public void fetchFromServer(final DownloadResult downloadResult) {

        String url = mVwo.getVwoUrlBuilder().getDownloadUrl();
        DownloadData downloadData = new DownloadData(url, downloadResult);
        downloadData.execute();

        if (mVwo.isSyncMode()) {

            try {
                if (downloadData.getStatus() != AsyncTask.Status.FINISHED) {
                    downloadData.get(2, TimeUnit.SECONDS);
                }
            } catch (InterruptedException exception) {
                downloadResult.onDownloadError(exception);
                LOGGER.fine("**** Data Download Interrupted ****");
                LOGGER.throwing(VwoDownloader.class.getSimpleName(), "fetchFromServer(DownloadResult", exception);
            } catch (ExecutionException exception) {
                LOGGER.fine("**** Data Download Execution Exception ****");
                LOGGER.throwing(VwoDownloader.class.getSimpleName(), "fetchFromServer(DownloadResult", exception);
                downloadResult.onDownloadError(exception);
            } catch (TimeoutException exception) {
                LOGGER.fine("**** Data Download Timeout ****");
                LOGGER.throwing(VwoDownloader.class.getSimpleName(), "fetchFromServer(DownloadResult", exception);
                downloadResult.onDownloadError(exception);
            }

        }

    }

    public class DownloadData extends AsyncTask<Void, Void, Void> {

        private String mUrl;
        private DownloadResult mDownloadResult;

        public DownloadData(String url, DownloadResult downloadResult) {
            this.mUrl = url;
            this.mDownloadResult = downloadResult;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            if (!NetworkUtils.shouldAttemptNetworkCall(mVwo)) {
                mDownloadResult.onDownloadError(new Exception("No internet"));
                return null;
            }
            final OkHttpClient client = new OkHttpClient.Builder().build();

            Request httpRequest = new Request.Builder().url(mUrl).build();
            try {
                Response response = client.newCall(httpRequest).execute();
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
            return null;
        }
    }

    public void startUpload() {
        Timer timer = new Timer();
        final OkHttpClient client = new OkHttpClient();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                final ArrayList<String> urls = mVwo.getVwoPreference().getListString(VwoData.VWO_QUEUE);

                if (urls.size() != 0) {
                    LOGGER.info(String.format(Locale.ENGLISH, "%d pending URLS", urls.size()));
                }

                if (!VwoActivityLifeCycle.isApplicationInForeground() || !NetworkUtils.shouldAttemptNetworkCall(mVwo)) {
                    LOGGER.fine("Either no network, or application is not in foreground");
                    return;
                }

                for (final String url : urls) {

                    Request request = new Request.Builder()
                            .url(url)
                            .build();

                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {

                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            LOGGER.info("Completed: " + response.request().url().toString());
                            urls.remove(url);
                            mVwo.getVwoPreference().putListString(VwoData.VWO_QUEUE, urls);
                        }
                    });

                }

            }
        }, 15000, 15000);
    }

    public interface DownloadResult {
        void onDownloadSuccess(JSONArray data);

        void onDownloadError(Exception ex);

    }


}
