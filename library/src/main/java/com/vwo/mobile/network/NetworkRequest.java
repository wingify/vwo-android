package com.vwo.mobile.network;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;

import com.vwo.mobile.utils.VWOLog;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by aman on 05/09/17.
 */

public class NetworkRequest implements Runnable {
    private static final int DEFAULT_READ_TIMEOUT = 10000;
    private static final int DEFAULT_CONNECT_TIMEOUT = 15000;
    @NonNull
    private URL url;
    @HttpMethod
    @NonNull
    private String method;
    @Nullable
    private Map<String, String> params;
    @Nullable
    private String body;
    private Map<String, String> headers;
    private int readTimeout;
    private int connectTimeout;
    private ResponseListener mResponseListener;

    public static final String GET = "GET";
    public static final String PUT = "PUT";
    public static final String POST = "POST";
    public static final String DELETE = "DELETE";

    private NetworkRequest(Builder builder) {
        this.url = builder.url;
        this.method = builder.method;
        this.params = builder.params;
        this.body = builder.body;
        this.headers = builder.headers;
        this.readTimeout = builder.readTimeout;
        this.connectTimeout = builder.connectTimeout;
        this.mResponseListener = builder.listener;
    }

    public String getUrl() {
        return this.url.toString();
    }

    public String getMethod() {
        return this.method;
    }

    public Map<String, String> getParams() {
        return this.params;
    }

    public String getBody() {
        return this.body;
    }

    public Map<String, String> getHeaders() {
        return this.headers;
    }

    @StringDef({GET, PUT, POST, DELETE})
    @interface HttpMethod {
    }

    public void setParams(@Nullable Map<String, String> params) {
        this.params = params;
    }

    public void setBody(@Nullable String body) {
        this.body = body;
    }

    public void setHeaders(@Nullable Map<String, String> headers) {
        this.headers = headers;
    }

    public void execute() throws IOException {
        run();
    }

    private byte[] readFromStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();

        if (inputStream != null) {
            byte[] data = new byte[2048];
            int read;
            while ((read = inputStream.read(data, 0, data.length)) != -1) {
                byteStream.write(read);
            }
        }

        return byteStream.toByteArray();
    }

    public static class Builder {
        private URL url;
        private String method;
        private Map<String, String> params;
        private String body;
        private Map<String, String> headers;
        private int readTimeout;
        private int connectTimeout;
        private ResponseListener listener;

        public Builder(@NonNull String url,
                       @NonNull @HttpMethod String method) throws MalformedURLException {
            this.url = new URL(url);
            this.method = method;
        }

        public Builder params(Map<String, String> params) {
            this.params = params;
            return this;
        }

        public Builder body(String body) {
            this.body = body;
            return this;
        }

        public Builder headers(Map<String, String> headers) {
            this.headers = headers;
            return this;
        }

        public Builder addHeader(String key, String value) {
            if (this.headers == null) {
                this.headers = new HashMap<>();
            }

            this.headers.put(key, value);
            return this;
        }

        public Builder addParam(String key, String value) {
            if (this.params == null) {
                this.params = new HashMap<>();
            }

            this.params.put(key, value);
            return this;
        }

        public Builder setReadTimeout(int readTimeout) {
            this.readTimeout = readTimeout;
            return this;
        }

        public Builder setConnectTimeout(int connectTimeout) {
            this.connectTimeout = connectTimeout;
            return this;
        }

        public Builder setResponseListener(ResponseListener listener) {
            this.listener = listener;
            return this;
        }

        public NetworkRequest build() {
            if (readTimeout == 0) {
                this.readTimeout = DEFAULT_READ_TIMEOUT;
            }
            if (connectTimeout == 0) {
                this.connectTimeout = DEFAULT_CONNECT_TIMEOUT;
            }
            return new NetworkRequest(this);
        }
    }

    @Override
    public void run() {
        HttpURLConnection urlConnection;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
        } catch (IOException exception) {
            VWOLog.e(VWOLog.DATA_LOGS, exception, true, false);
            return;
        }
        try {

            //Set request properties
            urlConnection.setRequestMethod(method);
            urlConnection.setReadTimeout(readTimeout);
            urlConnection.setConnectTimeout(connectTimeout);
            urlConnection.setDoInput(true);

            // Set request headers
            if (headers != null) {
                for (String headerKey : headers.keySet()) {
                    urlConnection.setRequestProperty(headerKey, headers.get(headerKey));
                }
            }

            // Write body to output stream
            if (method.equals(NetworkRequest.POST) || method.equals(NetworkRequest.PUT)) {
                urlConnection.setDoOutput(true);

                if (body != null) {
                    // Send the post body
                    OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
                    writer.write(body);
                    writer.flush();
                }
            }

            // Connect
            urlConnection.connect();

            // Check if we get any response.
            if (urlConnection.getResponseCode() >= 200 && urlConnection.getResponseCode() < 299) {
                NetworkResponse.Builder responseBuilder = new NetworkResponse
                        .Builder(urlConnection.getResponseCode(), true);
                if (urlConnection.getInputStream() != null && hasResponseBody(urlConnection.getResponseCode())) {
                    InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                    byte[] data = readFromStream(inputStream);
                    responseBuilder.body(data);
                }

                Map<String, String> headers = new HashMap<>();
                for (String key : urlConnection.getHeaderFields().keySet()) {
                    headers.put(key, urlConnection.getHeaderField(key));
                }

                NetworkResponse response = responseBuilder.headers(headers).build();

                mResponseListener.onResponse(response);
            } else {
                NetworkResponse.Builder responseBuilder = new NetworkResponse
                        .Builder(urlConnection.getResponseCode(), true);

                if (urlConnection.getErrorStream() != null) {

                    InputStream inputStream = new BufferedInputStream(urlConnection.getErrorStream());
                    byte[] data = readFromStream(inputStream);

                    responseBuilder.body(data);
                }

                Map<String, String> headers = new HashMap<>();
                for (String key : urlConnection.getHeaderFields().keySet()) {
                    headers.put(key, urlConnection.getHeaderField(key));
                }

                NetworkResponse response = responseBuilder.headers(headers).build();

                mResponseListener.onResponse(response);
            }
        } catch (ProtocolException exception) {
            mResponseListener.onFailure(exception);
            VWOLog.e(VWOLog.DATA_LOGS, exception, false, true);
        } catch (IOException exception) {
            mResponseListener.onFailure(exception);
            VWOLog.e(VWOLog.DATA_LOGS, exception, true, false);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

    private static boolean hasResponseBody(int responseCode) {
        return !(responseCode < HttpURLConnection.HTTP_OK && responseCode > 299)
                && responseCode != HttpURLConnection.HTTP_NO_CONTENT
                && responseCode != HttpURLConnection.HTTP_NOT_MODIFIED;
    }
}
