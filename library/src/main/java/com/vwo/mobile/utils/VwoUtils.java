package com.vwo.mobile.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.DisplayMetrics;

import com.vwo.mobile.Vwo;
import com.vwo.mobile.constants.AppConstants;
import com.vwo.mobile.constants.GlobalConstants;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

/**
 * Created by abhishek on 18/09/15 at 1:34 AM.
 */
public class VwoUtils {
    private static final String TAG = "VWO Utils";
    public static Boolean mIsAppStoreApp;
    private static boolean FORCE_APP_STORE = false;
    private Vwo mVwo;

    public VwoUtils(Vwo vwo) {
        mVwo = vwo;
    }

    public static String deviceId() {
        return Build.DISPLAY.replaceAll(" ", "_");
    }

    public static String deviceName() {
        return String.format("%s %s", new Object[]{Build.MANUFACTURER.toUpperCase(Locale.ENGLISH), Build.MODEL});
    }

    public static String getVwoSdkVersion() {
        return GlobalConstants.SDK_VERSION;
    }

    public static String getDeviceUUID(Vwo vwo) {
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

    public static boolean isTablet(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();

        assert metrics != null;

        float yInches = (float) metrics.heightPixels / metrics.ydpi;
        float xInches = (float) metrics.widthPixels / metrics.xdpi;
        double diagonalInches = Math.sqrt((double) (xInches * xInches + yInches * yInches));
        return diagonalInches >= 6.0D;
    }

    public static Map<String, Integer> getScreenSizeMap(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();

        assert metrics != null;

        HashMap screenMap = new HashMap();
        screenMap.put("height", Integer.valueOf(metrics.heightPixels));
        screenMap.put("width", Integer.valueOf(metrics.widthPixels));
        return screenMap;
    }

    public static Map<String, Integer> getScaledScreenSizeMap(Context context) {
        Map toReturn = getScreenSizeMap(context);
        double scaling = (double) getScreenshotScaling(context);
        toReturn.put("height", Integer.valueOf((int) ((double) ((Integer) toReturn.get("height")).intValue() * scaling)));
        toReturn.put("width", Integer.valueOf((int) ((double) ((Integer) toReturn.get("width")).intValue() * scaling)));
        return toReturn;
    }

    public static String applicationVersion(Vwo vwo) {
        Context context = vwo.getCurrentContext().getApplicationContext();
        if (context != null) {
            try {
                PackageInfo packageInfo = context.getPackageManager().getPackageInfo(applicationName(context), 0);
                if (packageInfo != null && packageInfo.versionName != null) {
                    return packageInfo.versionName;
                }
            } catch (PackageManager.NameNotFoundException e) {
                VwoLog.e(TAG, "Failed to get packaging info");
            }
        }

        return "0.0.0";
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
            mIsAppStoreApp = Boolean.valueOf(FORCE_APP_STORE || installer != null && !installer.isEmpty());
        }

        return mIsAppStoreApp.booleanValue();
    }

    public static float getScreenshotScaling(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();

        assert metrics != null;

        return metrics.densityDpi >= 240 ? 0.5F : 1.0F;
    }

    public boolean isDebudMode() {
        boolean isDebuggable = (0 != (mVwo.getApplication().getApplicationInfo().flags &= ApplicationInfo.FLAG_DEBUGGABLE));
        return isDebuggable;
    }

    public static boolean checkForInternetPermissions(Context context) {
        PackageManager pm = context.getPackageManager();
        int hasPerm = pm.checkPermission(Manifest.permission.INTERNET, context.getPackageName());
        if (hasPerm == PackageManager.PERMISSION_DENIED) {
            String errorMsg = "VWO requires Internet permission.\n" +
                    "Add <uses-permission android:name=\"android.permission.INTERNET\"/> in AndroidManifest.xml";
            VwoLog.log(errorMsg, VwoLog.ERROR);
            return false;
        }

        hasPerm = pm.checkPermission(Manifest.permission.ACCESS_NETWORK_STATE, context.getPackageName());

        if (hasPerm == PackageManager.PERMISSION_DENIED) {
            String errorMsg = "Granting ACCESS_NETWORK_STATE permission makes VWO work smarter.\n" +
                    "Add <uses-permission android:name=\"android.permission.ACCESS_NETWORK_STATE\"/> in AndroidManifest.xml";
            VwoLog.log(errorMsg, VwoLog.WARNING);
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
