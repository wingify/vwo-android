package com.vwo.mobile.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.vwo.mobile.VWO;

import java.util.logging.Logger;

/**
 * Created by abhishek on 19/10/15 at 12:46 AM.
 */
public class NetworkUtils {
    private static final int TYPE_WIFI = 1;
    private static final int TYPE_MOBILE = 2;
    private static final int TYPE_NOT_CONNECTED = 0;

    public static int getConnectivityStatus(VWO vwo) {

        if (!checkAccessNetworkStatePermission(vwo.getCurrentContext())) {
            VWOLog.e(VWOLog.CONFIG_LOGS, "Network Access permission not granted. Returning connected to Wifi",
                    true, false);
            return TYPE_WIFI;
        }

        ConnectivityManager cm = (ConnectivityManager) vwo.getCurrentContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return TYPE_WIFI;

            if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return TYPE_MOBILE;
        }
        return TYPE_NOT_CONNECTED;
    }

    public static boolean checkAccessNetworkStatePermission(Context context) {

        String permission = Manifest.permission.ACCESS_NETWORK_STATE;
        int res = context.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    public static boolean shouldAttemptNetworkCall(VWO vwo) {
        PackageManager pm = vwo.getCurrentContext().getPackageManager();
        int hasPerm = pm.checkPermission(android.Manifest.permission.ACCESS_NETWORK_STATE, vwo.getCurrentContext().getPackageName());
        if (hasPerm == PackageManager.PERMISSION_DENIED) {
            return true;
        }

        ConnectivityManager connectivityManager = (ConnectivityManager) vwo.getCurrentContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
