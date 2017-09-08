package com.vwo.mobile.network.exception;

/**
 * Created by aman on 08/09/17.
 */

public class ServerError extends Exception {
    public ServerError() {
        super();
    }

    public ServerError(String message) {
        super(message);
    }

    public ServerError(String message, Throwable cause) {
        super(message, cause);
    }

    public ServerError(Throwable cause) {
        super(cause);
    }
}
