package com.vwo.mobile.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.vwo.mobile.VWO;
import com.vwo.mobile.constants.AppConstants;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Pattern;

public class VWOUtils {
    private VWO mVWO;

    public VWOUtils(VWO vwo) {
        mVWO = vwo;
    }

    public static boolean isValidVwoAppKey(String appKey) {
        String regex = "[\\w]{32}-[0-9]+";
        Pattern pattern = Pattern.compile(regex);
        return !TextUtils.isEmpty(appKey) && pattern.matcher(appKey).matches();
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

    private static String applicationName(Context context) {
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

    public static int applicationVersion(Context context) {
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

    public static boolean checkForInternetPermissions(Context context) {

        boolean hasInternetPermission = checkForPermission(context, Manifest.permission.INTERNET);
        if (!hasInternetPermission) {
            String errorMsg = "VWO requires Internet permission.\n" +
                    "Add <uses-permission android:name=\"android.permission.INTERNET\"/> in AndroidManifest.xml";
            VWOLog.e(VWOLog.CONFIG_LOGS, errorMsg, false, false);
            return false;
        }

        boolean hasNetworkStatePermission = checkForPermission(context, Manifest.permission.ACCESS_NETWORK_STATE);

        if (!hasNetworkStatePermission) {
            String errorMsg = "Granting ACCESS_NETWORK_STATE permission makes VWO work smarter.\n" +
                    "Add <uses-permission android:name=\"android.permission.ACCESS_NETWORK_STATE\"/> in AndroidManifest.xml";
            VWOLog.e(VWOLog.CONFIG_LOGS, errorMsg, false, false);
            return false;
        }

        return true;
    }

    @CheckResult
    public static boolean checkForPermission(Context context, String permission) {
        PackageManager pm = context.getPackageManager();
        int hasPerm = pm.checkPermission(permission, context.getPackageName());
        if (hasPerm == PackageManager.PERMISSION_DENIED) {
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

    public static Calendar getCalendar() {
        return GregorianCalendar.getInstance();
    }

    public static boolean checkIfClassExists(String className) {
        try {
            Class.forName(className);
        } catch (ClassNotFoundException e) {
            return false;
        }
        return true;
    }

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        }
        if (manufacturer.equalsIgnoreCase("HTC")) {
            // make sure "HTC" is fully capitalized.
            return "HTC " + model;
        }
        return capitalize(manufacturer) + " " + model;
    }

    private static String capitalize(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        char[] arr = str.toCharArray();
        boolean capitalizeNext = true;
        StringBuilder phrase = new StringBuilder();
        for (char c : arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase.append(Character.toUpperCase(c));
                capitalizeNext = false;
                continue;
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true;
            }
            phrase.append(c);
        }
        return phrase.toString();
    }

    public boolean isDebugMode() {
        assert mVWO.getCurrentContext() != null;
        return (0 != (mVWO.getCurrentContext().getApplicationInfo().flags &= ApplicationInfo.FLAG_DEBUGGABLE));
    }

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    public static double getScale(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return 1.0f / DisplayMetrics.DENSITY_DEFAULT * displayMetrics.densityDpi;
    }


    /**
     * Get device ISO 3166 alpha-2 country code.
     * This function uses sim card or network to fetch country code, uses Device locale as a fallback.
     *
     * @param context is the application context.
     * @return the ISO 3166 alpha-2 country code eg. IN for India, US for United States of America,
     * AE for United Arab Emirates etc.
     */
    public static String getDeviceCountryCode(Context context) {
        try {
            TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (manager != null) {
                final String simCountry = manager.getSimCountryIso();
                if (simCountry != null && simCountry.length() == 2) {
                    return simCountry;
                } else if (manager.getPhoneType() != TelephonyManager.PHONE_TYPE_CDMA) {
                    String countryCode = manager.getNetworkCountryIso();
                    if (countryCode != null && countryCode.length() == 2) {
                        return countryCode;
                    }
                }
            }
        } catch (Exception exception) {
            VWOLog.e(VWOLog.CONFIG_LOGS, exception, true, false);
        }

        return Locale.getDefault().getCountry();
    }
}
