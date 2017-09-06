package com.vwo.mobile.network;

/**
 * Created by aman on 05/09/17.
 */

public interface Downloader {
    void onFailure(Exception exception);
    void onRespone(VWOResponse response);
}
