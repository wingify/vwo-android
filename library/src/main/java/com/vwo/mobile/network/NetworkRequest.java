package com.vwo.mobile.network;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;

import com.vwo.mobile.utils.NetworkUtils;
import com.vwo.mobile.utils.VWOLog;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

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
    private String requestBody;
    @Nullable
    private Response.Listener<T> mResponseListener;
    @Nullable
    private Response.ErrorListener mErrorListener;

    public static final String GET = "GET";
    public static final String PUT = "PUT";
    public static final String POST = "POST";
    public static final String DELETE = "DELETE";

    public NetworkRequest(@NonNull String url,
                          @NonNull @HttpMethod String method) throws MalformedURLException {
        this.url = new URL(url);
        this.method = method;
    }

    public void setResponseListener(Response.Listener<T> responseListener) {
        this.mResponseListener = responseListener;
    }

    public void body(String body) {
        this.requestBody = body;
    }

    public String getUrl() {
        return this.url.toString();
    }

    public Map<String, String> getParams() {
        return Collections.emptyMap();
    }

    public String getRequestBody() {
        return this.requestBody;
    }

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

    public void setErrorListener(@Nullable Response.ErrorListener mErrorListener) {
        this.mErrorListener = mErrorListener;
    }

    @StringDef({GET, PUT, POST, DELETE})
    @interface HttpMethod {
    }

    public void setRequestBody(@Nullable String requestBody) {
        this.requestBody = requestBody;
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

                if (requestBody != null) {
                    // Send the post body
                    OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
                    writer.write(requestBody);
                    writer.flush();
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
                    byte[] data = readFromStream(inputStream);
                    if (isGzipped(headers)) {
                        byte[] decompressedResponse = decompressResponse(data);
                        responseBuilder.body(decompressedResponse);
                    } else {
                        responseBuilder.body(data);
                    }
                }

                NetworkResponse response = responseBuilder.build();

                if (mResponseListener != null) {
                    mResponseListener.onResponse(getResponse(response));
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
                    if (isGzipped(headers)) {
                        byte[] decompressedResponse = decompressResponse(data);
                        responseBuilder.body(decompressedResponse);
                    } else {
                        responseBuilder.body(data);
                    }
                }

                NetworkResponse response = responseBuilder.build();

                if (mResponseListener != null) {
                    mResponseListener.onResponse(getResponse(response));
                }
            }
        } catch (ProtocolException exception) {
            if (mErrorListener != null) {
                mErrorListener.onFailure(exception);
            }
            VWOLog.e(VWOLog.DATA_LOGS, exception, false, true);
        } catch (IOException exception) {
            if (mErrorListener != null) {
                mErrorListener.onFailure(exception);
            }
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

    @Nullable
    public abstract T getResponse(NetworkResponse response);

    private boolean isGzipped(Map<String, String> headers) {
        return headers != null && !headers.isEmpty() && headers.containsKey(NetworkUtils.Headers.HEADER_CONTENT_ENCODING) &&
                headers.get(NetworkUtils.Headers.HEADER_CONTENT_ENCODING).equalsIgnoreCase(NetworkUtils.Headers.ENCODING_GZIP);
    }

    /**
     * @param compressed is compressed body to be decompressed
     * @return the decompressed body back to calling function.
     * @throws IOException is the exception that can be thrown during decompression
     */
    private byte[] decompressResponse(byte[] compressed) throws IOException {
        ByteArrayOutputStream baos = null;
        try {
            int size;
            ByteArrayInputStream memstream = new ByteArrayInputStream(compressed);
            GZIPInputStream gzip = new GZIPInputStream(memstream);
            final int buffSize = 8192;
            byte[] tempBuffer = new byte[buffSize];
            baos = new ByteArrayOutputStream();
            while ((size = gzip.read(tempBuffer, 0, buffSize)) != -1) {
                baos.write(tempBuffer, 0, size);
            }
            return baos.toByteArray();
        } finally {
            if (baos != null) {
                baos.close();
            }
        }
    }
}
