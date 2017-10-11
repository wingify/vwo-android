package com.vwo.mobile.network;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by aman on 13/09/17.
 */

public class FutureNetworkRequest<T> implements Future<T>, Response.Listener<T>, Response.ErrorListener {
    private NetworkRequest<?> mNetworkRequest;
    private boolean resultReceived;
    private T mResult;
    private ErrorResponse mErrorResponse;

    public static <E> FutureNetworkRequest<E> getInstance() {
        return new FutureNetworkRequest<>();
    }

    private FutureNetworkRequest() {

    }

    public void setRequest(NetworkRequest<?> request) {
        this.mNetworkRequest = request;
    }

    @Override
    public synchronized boolean cancel(boolean interruptIfRunning) {
        if(mNetworkRequest == null) {
            return false;
        }

        if(!isDone()) {
            mNetworkRequest.cancel();
            return true;
        }
        return false;
    }

    @Override
    public boolean isCancelled() {
        return mNetworkRequest != null && mNetworkRequest.isCanceled();
    }

    @Override
    public synchronized boolean isDone() {
        return resultReceived || mErrorResponse != null || isCancelled();
    }

    @Override
    public T get() throws InterruptedException, ExecutionException {
        try {
            return getResult(null);
        } catch (TimeoutException exception) {
            throw new AssertionError(exception);
        }
    }

    @Override
    public T get(long timeout, @NonNull TimeUnit timeUnit) throws InterruptedException, ExecutionException, TimeoutException {
        return getResult(TimeUnit.MILLISECONDS.convert(timeout, timeUnit));
    }

    private synchronized T getResult(Long timeout) throws ExecutionException, InterruptedException, TimeoutException {
        if (mErrorResponse != null) {
            throw new ExecutionException(mErrorResponse);
        }

        if(resultReceived) {
            return mResult;
        }

        if(timeout == null) {
            wait(0);
        } else if(timeout > 0) {
            wait(timeout);
        }

        if(mErrorResponse != null) {
            throw new ExecutionException(mErrorResponse);
        }

        if(!resultReceived) {
            throw new TimeoutException();
        }

        return mResult;
    }

    @Override
    public synchronized void onResponse(@NonNull NetworkRequest<T> networkRequest, @Nullable T response) {
        resultReceived = true;
        mResult = response;
        notifyAll();
    }

    @Override
    public synchronized void onFailure(ErrorResponse exception) {
        mErrorResponse = exception;
        notifyAll();
    }
}
