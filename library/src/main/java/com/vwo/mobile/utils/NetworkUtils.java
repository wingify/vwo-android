package com.vwo.mobile.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import com.vwo.mobile.VWO;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

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

        @SuppressLint("MissingPermission") NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
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

    public static boolean shouldAttemptNetworkCall(Context context) {
        PackageManager pm = context.getPackageManager();
        int hasPerm = pm.checkPermission(android.Manifest.permission.ACCESS_NETWORK_STATE, context.getPackageName());
        if (hasPerm == PackageManager.PERMISSION_DENIED) {
            return true;
        }

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        @SuppressLint("MissingPermission") NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static class Headers {
        public static final String HEADER_CONTENT_TYPE = "Content-type";
        public static final String ACCEPT_CONTENT_TYPE = "Accept";
        public static final String CONTENT_TYPE_JSON = "application/json; charset=utf-8";
        public static final String CONTENT_TYPE_PLAIN = "text/plain";
        public static final String CONTENT_TYPE_FORM_URL_ENCODED = "application/x-www-form-urlencoded";

        public static final String HEADER_ACCOUNT_ID = "Account-ID";
        public static final String HEADER_APP_KEY = "App-Key";
        public static final String HEADER_DEVICE_TYPE = "Device-Type";

        public static final String DEVICE_TYPE_VALUE = "Android";

        public static final String HEADER_CHARSET = "charset";
        public static final String CHARSET_DEFAULT = "UTF-8";

        public static final String HEADER_CONTENT_ENCODING = "Content-Encoding";
        public static final String HEADER_ACCEPT_ENCODING = "Accept-Encoding";
        public static final String ENCODING_GZIP = "gzip";

        public static final String HEADER_CACHE_CONTROL = "Cache-Control";
        public static final String CACHE_NO = "no-cache";

        public static final String HEADER_USER_AGENT = "User-Agent";
        public static final String USER_AGENT_VALUE = "vwo-android-sdk";

        private static String parseCharset(Map<String, String> headers, String defaultCharset) {
            String contentType = headers.get(HEADER_CONTENT_TYPE);
            if (contentType != null) {
                String[] params = contentType.split(";");
                for (int i = 1; i < params.length; i++) {
                    String[] pair = params[i].trim().split("=");
                    if (pair.length == 2) {
                        if (pair[0].equals("charset")) {
                            return pair[1];
                        }
                    }
                }
            }

            return defaultCharset;
        }

        public static String parseCharset(Map<String, String> headers) {
            return parseCharset(headers, CHARSET_DEFAULT);
        }

        public static Map<String, String> getBasicHeaders() {
            Map<String, String> headers = new HashMap<>();
            headers.put(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON);
            headers.put(ACCEPT_CONTENT_TYPE, CONTENT_TYPE_JSON);
            headers.put(HEADER_DEVICE_TYPE, DEVICE_TYPE_VALUE);
            return headers;
        }

        public static Map<String, String> getPostHeaders() {
            Map<String, String> headers = getBasicHeaders();
            headers.put(HEADER_USER_AGENT, USER_AGENT_VALUE);
            return headers;
        }

        public static Map<String, String> getAuthHeaders(String accountID, String appKey, boolean isEventArchEnabled) {
            Map<String, String> headers;
            if (isEventArchEnabled)
                headers = getPostHeaders();
            else
                headers = getBasicHeaders();

            headers.put(HEADER_ACCOUNT_ID, accountID);
            headers.put(HEADER_APP_KEY, appKey);
            return headers;
        }
    }


    /**
     * Get the network info
     *
     * @param context
     * @return
     */
    private static NetworkInfo getNetworkInfo(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo();
    }

    /**
     * Get the network type & subtype
     *
     * @param context
     * @return
     */
    public static String getNetworkType(Context context) {
        NetworkInfo info = getNetworkInfo(context);
        if (info == null) return ("Type :NA SubType:NA");
        return ("Type :" + info.getType() + " SubType: " + info.getSubtype());
    }


    /**
     * Check if the connection is fast
     *
     * @param type
     * @param subType
     * @return
     */
    public static boolean isConnectionFast(int type, int subType) {
        if (type == ConnectivityManager.TYPE_WIFI) {
            return true;
        } else if (type == ConnectivityManager.TYPE_MOBILE) {
            switch (subType) {
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                    return false; // ~ 50-100 kbps
                case TelephonyManager.NETWORK_TYPE_CDMA:
                    return false; // ~ 14-64 kbps
                case TelephonyManager.NETWORK_TYPE_EDGE:
                    return false; // ~ 50-100 kbps
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    return true; // ~ 400-1000 kbps
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    return true; // ~ 600-1400 kbps
                case TelephonyManager.NETWORK_TYPE_GPRS:
                    return false; // ~ 100 kbps
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                    return true; // ~ 2-14 Mbps
                case TelephonyManager.NETWORK_TYPE_HSPA:
                    return true; // ~ 700-1700 kbps
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                    return true; // ~ 1-23 Mbps
                case TelephonyManager.NETWORK_TYPE_UMTS:
                    return true; // ~ 400-7000 kbps
                /*
                 * Above API level 7, make sure to set android:targetSdkVersion
                 * to appropriate level to use these
                 */
                case TelephonyManager.NETWORK_TYPE_EHRPD: // API level 11
                    return true; // ~ 1-2 Mbps
                case TelephonyManager.NETWORK_TYPE_EVDO_B: // API level 9
                    return true; // ~ 5 Mbps
                case TelephonyManager.NETWORK_TYPE_HSPAP: // API level 13
                    return true; // ~ 10-20 Mbps
                case TelephonyManager.NETWORK_TYPE_IDEN: // API level 8
                    return false; // ~25 kbps
                case TelephonyManager.NETWORK_TYPE_LTE: // API level 11
                    return true; // ~ 10+ Mbps
                // Unknown
                case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                default:
                    return false;
            }
        } else {
            return false;
        }
    }

}
