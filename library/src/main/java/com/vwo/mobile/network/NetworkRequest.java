package com.vwo.mobile.network;

import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;

import com.vwo.mobile.utils.NetworkUtils;
import com.vwo.mobile.utils.VWOLog;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by aman on 05/09/17.
 */

public abstract class NetworkRequest<T> implements Runnable, Comparable<NetworkRequest<T>> {
    private static final String LOG_TAG = NetworkRequest.class.getSimpleName();

    private static final int HTTP_CONTINUE = 100;

    private static final int DEFAULT_READ_TIMEOUT = 15000;
    private static final int DEFAULT_CONNECT_TIMEOUT = 15000;
    @NonNull
    private URL url;
    @HttpMethod
    @NonNull
    private String method;
    @NonNull
    private List<Response.Listener<T>> mResponseListeners;
    @NonNull
    private List<Response.ErrorListener> mErrorListeners;
    private String requestTag;
    private boolean canceled;
    private Thread currentThread;
    private boolean executed;

    public static final String GET = "GET";
    public static final String PUT = "PUT";
    public static final String POST = "POST";
    public static final String DELETE = "DELETE";

    public static final String DEFAULT_CONTENT_ENCODING = "UTF-8";

    @RequestPriority
    private int priority;

    private static final int PRIORITY_VERY_LOW = 0;
    private static final int PRIORITY_LOW = 1;
    private static final int PRIORITY_NORMAL = 2;
    private static final int PRIORITY_HIGH = 3;
    private static final int PRIORITY_VERY_HIGH = 4;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({PRIORITY_VERY_LOW, PRIORITY_LOW, PRIORITY_NORMAL, PRIORITY_HIGH, PRIORITY_VERY_HIGH})
    @interface RequestPriority {
    }

    public NetworkRequest(@NonNull String url,
                          @NonNull @HttpMethod String method, @Nullable Response.Listener<T> listener,
                          @Nullable Response.ErrorListener errorListener, String requestTag) throws MalformedURLException {
        this.url = new URL(url);
        this.method = method;
        this.requestTag = requestTag;
        this.mResponseListeners = new ArrayList<>();
        this.mErrorListeners = new ArrayList<>();
        if(listener != null) {
            this.mResponseListeners.add(listener);
        }
        if(errorListener != null) {
            this.mErrorListeners.add(errorListener);
        }
        this.priority = PRIORITY_NORMAL;
    }

    public NetworkRequest(@NonNull String url,
                          @NonNull @HttpMethod String method, Response.Listener<T> listener,
                          Response.ErrorListener errorListener) throws MalformedURLException {
        this(url, method, listener, errorListener, LOG_TAG);
    }

    public void setUrl(String url) throws MalformedURLException {
        if(isExecuted() || isCanceled()) {
            throw new IllegalStateException("Cannot change URL after request is initiated.");
        }
        this.url = new URL(url);
    }

    public String getUrl() {
        return this.url.toString();
    }

    @Nullable
    protected Map<String, String> getParams() {
        return null;
    }

    @NonNull
    protected Map<String, String> getHeaders() {
        return Collections.emptyMap();
    }

    protected int getReadTimeout() {
        return DEFAULT_READ_TIMEOUT;
    }

    protected int getConnectTimeout() {
        return DEFAULT_CONNECT_TIMEOUT;
    }

    public void addResponseListener(Response.Listener<T> responseListener) {
        this.mResponseListeners.add(responseListener);
    }

    public void addErrorListener(@Nullable Response.ErrorListener mErrorListener) {
        this.mErrorListeners.add(mErrorListener);
    }

    public void removeResponseListener(Response.Listener<T> responseListener) {
        this.mResponseListeners.remove(responseListener);
    }

    public void removeErrorListener(Response.ErrorListener errorListener) {
        this.mErrorListeners.remove(errorListener);
    }

    Thread getCurrentThread() {
        return currentThread;
    }

    void setCurrentThread(Thread currentThread) {
        this.currentThread = currentThread;
    }

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({GET, PUT, POST, DELETE})
    @interface HttpMethod {
    }

    private byte[] readFromStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream(inputStream.available());

        byte[] data = new byte[2048];
        int read;
        while ((read = inputStream.read(data, 0, data.length)) != -1) {
            byteStream.write(data, 0, read);
        }

        return byteStream.toByteArray();
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

            if (Thread.interrupted() || isCanceled()) {
                throw new InterruptedException();
            }

            //Set request properties
            urlConnection.setRequestMethod(method);
            urlConnection.setReadTimeout(getReadTimeout());
            urlConnection.setConnectTimeout(getConnectTimeout());
            urlConnection.setUseCaches(false);
            urlConnection.setInstanceFollowRedirects(HttpURLConnection.getFollowRedirects());
            urlConnection.setDoInput(true);

            // Set request headers
            for (String headerKey : getHeaders().keySet()) {
                urlConnection.setRequestProperty(headerKey, getHeaders().get(headerKey));
            }

            if (Thread.interrupted() || isCanceled()) {
                throw new InterruptedException();
            }

            // Write body to output stream
            if (method.equals(NetworkRequest.POST) || method.equals(NetworkRequest.PUT)) {
                urlConnection.setDoOutput(true);
                getHeaders().put(NetworkUtils.Headers.HEADER_CONTENT_TYPE, getBodyContentType());

                byte[] requestBody = getBody();
                if (requestBody != null) {
                    // Send the post body
                    urlConnection.getOutputStream().write(requestBody);
                    urlConnection.getOutputStream().flush();
                }
            }

            if (Thread.interrupted() || isCanceled()) {
                throw new InterruptedException();
            }

            // Connect
            urlConnection.connect();

            if (Thread.interrupted() || isCanceled()) {
                throw new InterruptedException();
            }

            // Check if we get any response.
            if ((urlConnection.getResponseCode() >= 200 && urlConnection.getResponseCode() < 299) ||
                    urlConnection.getResponseCode() == HttpURLConnection.HTTP_NOT_MODIFIED) {
                NetworkResponse.Builder responseBuilder = new NetworkResponse
                        .Builder(urlConnection.getResponseCode(), true);

                Map<String, String> headers = new HashMap<>();
                for (String key : urlConnection.getHeaderFields().keySet()) {
                    headers.put(key, urlConnection.getHeaderField(key));
                }

                responseBuilder.headers(headers);

                if (urlConnection.getInputStream() != null && hasResponseBody(urlConnection.getResponseCode())) {
                    InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                    if (Thread.interrupted() || isCanceled()) {
                        throw new InterruptedException();
                    }
                    responseBuilder.body(readFromStream(inputStream));
                }

                final NetworkResponse response = responseBuilder.build();

                if (Thread.interrupted() || isCanceled()) {
                    throw new InterruptedException();
                }

                notifyResponseListeners(response);
            } else {
                NetworkResponse.Builder responseBuilder = new NetworkResponse
                        .Builder(urlConnection.getResponseCode(), false);

                Map<String, String> headers = new HashMap<>();
                for (String key : urlConnection.getHeaderFields().keySet()) {
                    headers.put(key, urlConnection.getHeaderField(key));
                }

                responseBuilder.headers(headers);

                if (urlConnection.getErrorStream() != null) {

                    InputStream inputStream = new BufferedInputStream(urlConnection.getErrorStream());
                    if (Thread.interrupted() || isCanceled()) {
                        throw new InterruptedException();
                    }
                    byte[] data = readFromStream(inputStream);
                    responseBuilder.body(data);
                }

                final NetworkResponse response = responseBuilder.build();

                if (Thread.interrupted() || isCanceled()) {
                    throw new InterruptedException();
                }

                notifyErrorListeners(getErrorResponse(new ErrorResponse(response)));
            }
        } catch (final ProtocolException exception) {
            notifyErrorListeners(new ErrorResponse(exception));

            VWOLog.e(VWOLog.DATA_LOGS, exception, false, true);
        }catch (final IOException exception) {
            notifyErrorListeners(new ErrorResponse(exception));

            VWOLog.e(VWOLog.DATA_LOGS, exception, true, false);
        } catch (final InterruptedException exception) {
            VWOLog.e(VWOLog.DATA_LOGS, "Either connection was closed or " +
                            "download thread was interrupted for request with tag : " + requestTag, exception,
                    true, false);
            notifyErrorListeners(new ErrorResponse(exception));
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

    private void notifyErrorListeners(ErrorResponse exception) {
        executed = true;
        for(Response.ErrorListener mErrorListener : mErrorListeners) {
            mErrorListener.onFailure(exception);
        }
    }

    private void notifyResponseListeners(NetworkResponse networkResponse) {
        executed = true;
        for (Response.Listener<T> mResponseListener : mResponseListeners) {
            mResponseListener.onResponse(this, parseResponse(networkResponse));
        }
    }

    public void setTag(String tag) {
        if (tag == null) {
            throw new NullPointerException("Request tag cannot be set to null");
        }
        this.requestTag = tag;
    }

    public String getTag() {
        return this.requestTag;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof NetworkRequest) {
            return ((NetworkRequest) obj).getTag().equals(getTag());
        }
        return super.equals(obj);
    }

    public boolean isCanceled() {
        return canceled;
    }

    public void cancel() {
        this.canceled = true;
    }

    public boolean isExecuted() {
        return executed;
    }

    protected String getParamsEncoding() {
        return DEFAULT_CONTENT_ENCODING;
    }

    /**
     * Do override this in case of overriding {@link NetworkRequest#getBody()}
     */
    public String getBodyContentType() {
        return "application/x-www-form-urlencoded; charset=" + getParamsEncoding();
    }

    public byte[] getBody() {
        Map<String, String> params = getParams();
        if (params != null && params.size() > 0) {
            return encodeParameters(params, getParamsEncoding());
        }
        return null;
    }

    /**
     * Converts <code>params</code> into an application/x-www-form-urlencoded encoded string.
     */
    private byte[] encodeParameters(Map<String, String> params, String paramsEncoding) {
        StringBuilder encodedParams = new StringBuilder();
        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                encodedParams.append(URLEncoder.encode(entry.getKey(), paramsEncoding));
                encodedParams.append('=');
                encodedParams.append(URLEncoder.encode(entry.getValue(), paramsEncoding));
                encodedParams.append('&');
            }
            return encodedParams.toString().getBytes(paramsEncoding);
        } catch (UnsupportedEncodingException uee) {
            throw new RuntimeException("Encoding not supported: " + paramsEncoding, uee);
        }
    }

    private static boolean hasResponseBody(int responseCode) {
        return !(HTTP_CONTINUE <= responseCode && responseCode < HttpURLConnection.HTTP_OK)
                && responseCode != HttpURLConnection.HTTP_NO_CONTENT
                && responseCode != HttpURLConnection.HTTP_NOT_MODIFIED;
    }

    @Override
    public int compareTo(@NonNull NetworkRequest<T> tNetworkRequest) {
        return tNetworkRequest.getPriority() - this.getPriority();
    }

    /**
     * @return the execution priority of the request
     */
    @RequestPriority
    public int getPriority() {
        return this.priority;
    }

    /**
     * Sets the priority of the request. This priority will decide the ordering of request executed.
     *
     * @param priority is the priority range of object from 1 to 10.
     */
    public void setPriority(@RequestPriority int priority) {
        this.priority = priority;
    }

    @Nullable
    public abstract T parseResponse(NetworkResponse response);

    protected ErrorResponse getErrorResponse(ErrorResponse response) {
        return response;
    }
}
