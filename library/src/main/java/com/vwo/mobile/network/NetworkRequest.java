package com.vwo.mobile.network;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;

import com.vwo.mobile.utils.NetworkUtils;
import com.vwo.mobile.utils.VWOLog;
import com.vwo.mobile.utils.VWOUtils;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by aman on 05/09/17.
 */

public abstract class NetworkRequest<T> {
    private static final int DEFAULT_READ_TIMEOUT = 15000;
    private static final int DEFAULT_CONNECT_TIMEOUT = 15000;
    @NonNull
    private URL url;
    @HttpMethod
    @NonNull
    private String method;
    @Nullable
    private Response.Listener<T> mResponseListener;
    @Nullable
    private Response.ErrorListener mErrorListener;
    private String body;

    public static final String GET = "GET";
    public static final String PUT = "PUT";
    public static final String POST = "POST";
    public static final String DELETE = "DELETE";

    public static final String DEFAULT_CONTENT_ENCODING = "UTF-8";

    public NetworkRequest(@NonNull String url,
                          @NonNull @HttpMethod String method) throws MalformedURLException {
        this.url = new URL(url);
        this.method = method;
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

    @Nullable
    public Response.Listener<T> getResponseListener() {
        return mResponseListener;
    }

    @Nullable
    public Response.ErrorListener getErrorListener() {
        return mErrorListener;
    }

    public void setResponseListener(Response.Listener<T> responseListener) {
        this.mResponseListener = responseListener;
    }

    public void setErrorListener(@Nullable Response.ErrorListener mErrorListener) {
        this.mErrorListener = mErrorListener;
    }

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

    public void execute() {
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
            urlConnection.setReadTimeout(getReadTimeout());
            urlConnection.setConnectTimeout(getConnectTimeout());
            urlConnection.setDoInput(true);

            // Set request headers
            for (String headerKey : getHeaders().keySet()) {
                urlConnection.setRequestProperty(headerKey, getHeaders().get(headerKey));
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

            // Connect
            urlConnection.connect();

            // Check if we get any response.
            if (urlConnection.getResponseCode() >= 200 && urlConnection.getResponseCode() < 299) {
                NetworkResponse.Builder responseBuilder = new NetworkResponse
                        .Builder(urlConnection.getResponseCode(), true);

                Map<String, String> headers = new HashMap<>();
                for (String key : urlConnection.getHeaderFields().keySet()) {
                    headers.put(key, urlConnection.getHeaderField(key));
                }

                responseBuilder.headers(headers);

                if (urlConnection.getInputStream() != null && hasResponseBody(urlConnection.getResponseCode())) {
                    InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                    responseBuilder.body(readFromStream(inputStream));
                }

                NetworkResponse response = responseBuilder.build();

                if (mResponseListener != null) {
                    mResponseListener.onResponse(parseResponse(response));
                }
            } else {
                NetworkResponse.Builder responseBuilder = new NetworkResponse
                        .Builder(urlConnection.getResponseCode(), true);

                Map<String, String> headers = new HashMap<>();
                for (String key : urlConnection.getHeaderFields().keySet()) {
                    headers.put(key, urlConnection.getHeaderField(key));
                }

                responseBuilder.headers(headers);

                if (urlConnection.getErrorStream() != null) {

                    InputStream inputStream = new BufferedInputStream(urlConnection.getErrorStream());
                    byte[] data = readFromStream(inputStream);
                    responseBuilder.body(data);
                }

                NetworkResponse response = responseBuilder.build();

                if (mErrorListener != null) {
                    mErrorListener.onFailure(getErrorResponse(new ErrorResponse(response)));
                }
            }
        } catch (ProtocolException exception) {
            if (mErrorListener != null) {
                mErrorListener.onFailure(new ErrorResponse(exception));
            }
            VWOLog.e(VWOLog.DATA_LOGS, exception, false, true);
        } catch (IOException exception) {
            if (mErrorListener != null) {
                mErrorListener.onFailure(new ErrorResponse(exception));
            }
            VWOLog.e(VWOLog.DATA_LOGS, exception, true, false);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
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
        return !(responseCode < HttpURLConnection.HTTP_OK && responseCode > 299)
                && responseCode != HttpURLConnection.HTTP_NO_CONTENT
                && responseCode != HttpURLConnection.HTTP_NOT_MODIFIED;
    }

    @Nullable
    public abstract T parseResponse(NetworkResponse response);

    protected ErrorResponse getErrorResponse(ErrorResponse response) {
        return response;
    }
}
