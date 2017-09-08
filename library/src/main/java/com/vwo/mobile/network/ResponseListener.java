package com.vwo.mobile.network;

import android.support.annotation.NonNull;

/**
 * Created by aman on 05/09/17.
 */

public interface ResponseListener {
    void onResponse(@NonNull NetworkResponse response);
    void onFailure(Exception exception);
}
