package com.vwo.mobile.network;

/**
 * Created by aman on 12/09/17.
 */

public class ErrorResponse extends Exception {
    private NetworkResponse networkResponse;

    public ErrorResponse() {
        this.networkResponse = null;
    }

    public ErrorResponse(NetworkResponse response) {
        networkResponse = response;
    }

    public ErrorResponse(String exceptionMessage) {
        super(exceptionMessage);
        networkResponse = null;
    }

    public ErrorResponse(String exceptionMessage, Throwable reason) {
        super(exceptionMessage, reason);
        networkResponse = null;
    }

    public ErrorResponse(Throwable cause) {
        super(cause);
        networkResponse = null;
    }

    public NetworkResponse getNetworkResponse() {
        return networkResponse;
    }
}
