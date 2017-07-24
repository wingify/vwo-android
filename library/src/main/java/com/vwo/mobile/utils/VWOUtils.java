package com.vwo.mobile.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.vwo.mobile.BuildConfig;
import com.vwo.mobile.VWO;
import com.vwo.mobile.constants.AppConstants;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

/**
 * Created by abhishek on 18/09/15 at 1:34 AM.
 */
public class VWOUtils {

    public static Boolean mIsAppStoreApp;
    private static final boolean FORCE_APP_STORE = false;
    private VWO mVWO;

    public VWOUtils(VWO vwo) {
        mVWO = vwo;
    }

    public static String deviceId() {
        return Build.DISPLAY.replaceAll(" ", "_");
    }

    public static String deviceName() {
        return String.format("%s %s", Build.MANUFACTURER.toUpperCase(Locale.ENGLISH), Build.MODEL);
    }

    public static boolean isValidVwoAppKey(String appKey) {
        return !TextUtils.isEmpty(appKey) && appKey.contains("-");
    }

    public static String getVwoSdkVersion() {
        return BuildConfig.VERSION_NAME;
    }

    public static String getDeviceUUID(VWO vwo) {
        String deviceUuid = vwo.getVwoPreference().getString(AppConstants.DEVICE_UUID);

        if (deviceUuid == null || deviceUuid.equals("")) {
            deviceUuid = UUID.randomUUID().toString();
            deviceUuid = deviceUuid.replaceAll("-", "");
            vwo.getVwoPreference().putString(AppConstants.DEVICE_UUID, deviceUuid);
        }

        return deviceUuid;
    }

    public static String deviceModel() {
        return Build.MODEL;
    }

    public static String applicationName(Context context) {
        if (context != null) {
            ApplicationInfo applicationInfo = context.getApplicationInfo();
            if (applicationInfo != null && applicationInfo.packageName != null) {
                return applicationInfo.packageName;
            }
        }

        return "com.UnknownApp";
    }

    public static String androidVersion() {
        return Build.VERSION.CODENAME.equals("REL") ? Integer.toString(Build.VERSION.SDK_INT) : Build.VERSION.CODENAME;
    }

    public static boolean isTablet(@NonNull Context context) {

        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public static Map<String, Integer> getScreenSizeMap(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();

        assert metrics != null;

        HashMap<String, Integer> screenMap = new HashMap<>();
        screenMap.put("height", metrics.heightPixels);
        screenMap.put("width", metrics.widthPixels);
        return screenMap;
    }

    public static Map<String, Integer> getScaledScreenSizeMap(Context context) {
        Map<String, Integer> toReturn = getScreenSizeMap(context);
        double scaling = (double) getScreenshotScaling(context);
        toReturn.put("height", (int) ((double) (Integer) toReturn.get("height") * scaling));
        toReturn.put("width", (int) ((double) (Integer) toReturn.get("width") * scaling));
        return toReturn;
    }

    public static int applicationVersion(VWO vwo) {
        Context context = vwo.getCurrentContext().getApplicationContext();
        if (context != null) {
            try {
                PackageInfo packageInfo = context.getPackageManager().getPackageInfo(applicationName(context), 0);
                if (packageInfo != null) {
                    return packageInfo.versionCode;
                }
            } catch (PackageManager.NameNotFoundException exception) {
                VWOLog.e(VWOLog.CONFIG_LOGS, "Failed to get packaging info", exception, true, true);
            }
        }

        return -1;
    }

    public static double getRandomNumber() {
        Random rn = new Random(System.currentTimeMillis() / 1000L + new Random().nextInt());
        return (rn.nextInt(100)) / 100.0;

    }


    public static String getLanguage() {
        return Locale.getDefault().getLanguage();
    }

    public static String getLocale() {
        return Locale.getDefault().toString();
    }

    public static String getLocaleTag() {
        return Locale.getDefault().toString();
    }

    public static boolean isAppStoreApp(Context context) {
        if (mIsAppStoreApp == null) {
            PackageManager packageManager = context.getPackageManager();
            String installer = packageManager.getInstallerPackageName(context.getPackageName());
            mIsAppStoreApp = FORCE_APP_STORE || installer != null && !installer.isEmpty();
        }

        return mIsAppStoreApp;
    }

    public static float getScreenshotScaling(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();

        assert metrics != null;

        return metrics.densityDpi >= 240 ? 0.5F : 1.0F;
    }

    public boolean isDebugMode() {
        assert mVWO.getCurrentContext() != null;
        return (0 != (mVWO.getCurrentContext().getApplicationInfo().flags &= ApplicationInfo.FLAG_DEBUGGABLE));
    }

    public static boolean checkForInternetPermissions(Context context) {
        PackageManager pm = context.getPackageManager();
        int hasPerm = pm.checkPermission(Manifest.permission.INTERNET, context.getPackageName());
        if (hasPerm == PackageManager.PERMISSION_DENIED) {
            String errorMsg = "VWO requires Internet permission.\n" +
                    "Add <uses-permission android:name=\"android.permission.INTERNET\"/> in AndroidManifest.xml";
            VWOLog.e(VWOLog.CONFIG_LOGS, errorMsg, false, false);
            return false;
        }

        hasPerm = pm.checkPermission(Manifest.permission.ACCESS_NETWORK_STATE, context.getPackageName());

        if (hasPerm == PackageManager.PERMISSION_DENIED) {
            String errorMsg = "Granting ACCESS_NETWORK_STATE permission makes VWO work smarter.\n" +
                    "Add <uses-permission android:name=\"android.permission.ACCESS_NETWORK_STATE\"/> in AndroidManifest.xml";
            VWOLog.e(VWOLog.CONFIG_LOGS, errorMsg, false, false);
        }
        return true;
    }

    public static boolean checkIfClassExists(String className) {
        try {
            Class.forName(className);
        } catch (ClassNotFoundException e) {
            return false;
        }
        return true;
    }
}
