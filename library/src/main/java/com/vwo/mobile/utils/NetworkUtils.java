package com.vwo.mobile.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.vwo.mobile.Vwo;

import java.util.logging.Logger;

/**
 * Created by abhishek on 19/10/15 at 12:46 AM.
 */
public class NetworkUtils {
    private static final Logger LOGGER = VWOLogger.getLogger(NetworkUtils.class.getCanonicalName());

    public static int TYPE_WIFI = 1;
    public static int TYPE_MOBILE = 2;
    public static int TYPE_NOT_CONNECTED = 0;

    public static int getConnectivityStatus(Vwo vwo) {

        if (!checkAccessNetworkStatePermission(vwo.getCurrentContext())) {
            LOGGER.fine("Network Access permission not granted. Returning connected to Wifi");
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

    public static boolean shouldAttemptNetworkCall(Vwo vwo) {
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
