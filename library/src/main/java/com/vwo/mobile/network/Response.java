package com.vwo.mobile.network;

import android.support.annotation.Nullable;

/**
 * Created by aman on 05/09/17.
 */

public class Response<T> {
    @Nullable
    private final T result;
    @Nullable
    public final Exception exception;


    public interface Listener<T> {
        void onResponse(@Nullable T response);
    }

    public interface ErrorListener {
        void onFailure(Exception exception);
    }

    /** Returns a successful response containing the parsed result. */
    public static <T> Response<T> success(T result) {
        return new Response<>(result);
    }

    /**
     * Returns a failed response containing the given error code and an optional
     * localized message displayed to the user.
     */
    public static <T> Response<T> error(Exception exception) {
        return new Response<>(exception);
    }

    private Response(@Nullable T result) {
        this.result = result;
        this.exception = null;
    }

    private Response(@Nullable Exception exception) {
        this.result = null;
        this.exception = exception;
    }

}
