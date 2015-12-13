package com.vwo.mobile.network;

import android.os.AsyncTask;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.vwo.mobile.Vwo;
import com.vwo.mobile.data.VwoData;
import com.vwo.mobile.listeners.VwoActivityLifeCycle;
import com.vwo.mobile.utils.NetworkUtils;
import com.vwo.mobile.utils.VwoLog;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by abhishek on 17/09/15 at 11:39 PM.
 */
public class VwoDownloader {

    private static final String TAG = "Vwo Downloader";
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
            } catch (InterruptedException e) {
                downloadResult.onDownloadError(e);
                VwoLog.d("**** Data Download Interrupted ****");
            } catch (ExecutionException e) {
                VwoLog.d("**** Data Download Execution Exception ****");
                downloadResult.onDownloadError(e);
            } catch (TimeoutException e) {
                VwoLog.d("**** Data Download Timeout ****");
                downloadResult.onDownloadError(e);
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
            final OkHttpClient client = new OkHttpClient();

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
                    VwoLog.d(TAG, "Pending URLS: " + urls.size());
                }

                if (!VwoActivityLifeCycle.isApplicationInForeground() || !NetworkUtils.shouldAttemptNetworkCall(mVwo)) {
                    VwoLog.d("Either no network, or application is not in foreground");
                    return;
                }

                for (final String url : urls) {

                    Request request = new Request.Builder()
                            .url(url)
                            .build();

                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Request request, IOException e) {
                            VwoLog.e(TAG, e);
                        }

                        @Override
                        public void onResponse(Response response) throws IOException {
                            VwoLog.d("Completed: " + response.request().urlString());
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
