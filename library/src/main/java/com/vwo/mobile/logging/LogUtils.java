package com.vwo.mobile.logging;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.vwo.mobile.utils.VWOLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Map;
import java.util.Set;

/**
 * Created by aman on Fri 13:14.
 */

public class LogUtils {

    /**
     * Get stacktrace in {@link String} format
     *
     * @param throwable is the {@link Throwable} to convert to {@link String}
     *
     * @return stacktrace in {@link String}
     */
    @Nullable
    public static String getStackTrace(@NonNull Throwable throwable) {
        return Log.getStackTraceString(throwable);
    }

    /**
     * Get stacktrace if the crash is generated from the given packageName
     * or null if packageName is not found in the stacktrace
     *
     * @param throwable is the {@link Throwable} to check for
     * @param packageName is the package name to search in the throwable
     *
     * @return {@link String} stacktrace if the crash is generated from the given packageName,
     *          or null if packageName is not found in the stacktrace
     */
    @Nullable
    static String getCause(Throwable throwable, String packageName) {
        String stackTrace = getStackTrace(throwable);
        if (stackTrace != null && stackTrace.contains(packageName)) {
            return stackTrace;
        }

        return null;
    }

    private static boolean isExternalStorageMounted() {
        return Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)
                && !Environment.isExternalStorageEmulated();
    }

    /**
     * Get the unused amount of internal storage, in bytes.
     *
     * @return the unused amount of internal storage, in bytes
     */
    @Nullable
    static Long getUnusedInternalStorageSize() {
        try {
            File path = Environment.getDataDirectory();
            StatFs statFs = new StatFs(path.getPath());
            long blockSize;
            long availableBlocks;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
                availableBlocks = statFs.getAvailableBlocksLong();
                blockSize = statFs.getBlockSizeLong();
            } else {
                availableBlocks = statFs.getAvailableBlocks();
                blockSize = statFs.getAvailableBlocks();
            }
            return availableBlocks * blockSize;
        } catch (Exception exception) {
            VWOLog.e(VWOLog.UNCAUGHT, "Unable to fetch unused internal storage size.", exception, true, false);
            return null;
        }
    }

    /**
     * Get the total amount of internal storage, in bytes.
     *
     * @return the total amount of internal storage, in bytes
     */
    @Nullable
    static Long getInternalStorageSize() {
        try {
            File path = Environment.getDataDirectory();
            StatFs statFs = new StatFs(path.getPath());
            long blocksCount;
            long availableBlocks;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
                availableBlocks = statFs.getAvailableBlocksLong();
                blocksCount = statFs.getBlockSizeLong();
            } else {
                availableBlocks = statFs.getAvailableBlocks();
                blocksCount = statFs.getAvailableBlocks();
            }
            return blocksCount * availableBlocks;
        } catch (Exception exception) {
            VWOLog.e(VWOLog.UNCAUGHT, "Unable to fetch internal storage size.", exception,
                    true, false);
            return null;
        }
    }

    /**
     * Get the unused amount of external storage, in bytes, or null if no external storage
     * is mounted.
     *
     * @return the unused amount of external storage, in bytes, or null if no external storage
     * is mounted
     */
    @Nullable
    static Long getUnusedExternalStorageSize() {
        try {
            if (isExternalStorageMounted()) {
                File path = Environment.getExternalStorageDirectory();
                StatFs statFs = new StatFs(path.getPath());
                long blocksCount;
                long availableBlocks;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    availableBlocks = statFs.getAvailableBlocksLong();
                    blocksCount = statFs.getBlockSizeLong();
                } else {
                    availableBlocks = statFs.getAvailableBlocks();
                    blocksCount = statFs.getAvailableBlocks();
                }
                return blocksCount * availableBlocks;
            }
        } catch (Exception exception) {
            VWOLog.e(VWOLog.UNCAUGHT, "Unable to fetch unused external storage size.", exception,
                    true, false);
        }

        return null;
    }

    /**
     * Get the total amount of external storage, in bytes, or null if no external storage
     * is mounted.
     *
     * @return the total amount of external storage, in bytes, or null if no external storage
     * is mounted
     */
    @Nullable
    static Long getExternalStorageSize() {
        try {
            if (isExternalStorageMounted()) {
                File path = Environment.getExternalStorageDirectory();
                StatFs statFs = new StatFs(path.getPath());
                long blocksCount;
                long availableBlocks;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    availableBlocks = statFs.getAvailableBlocksLong();
                    blocksCount = statFs.getBlockSizeLong();
                } else {
                    availableBlocks = statFs.getAvailableBlocks();
                    blocksCount = statFs.getAvailableBlocks();
                }
                return blocksCount * availableBlocks;
            }
        } catch (Exception exception) {
            VWOLog.e(VWOLog.UNCAUGHT, "Unable to fetch external storage size.", exception,
                    true, false);
        }

        return null;
    }

    /**
     *
     * @param context the application {@link Context}
     * @return {@link ActivityManager.MemoryInfo} instance
     */
    @Nullable
    static ActivityManager.MemoryInfo getAppMemoryInfo(Context context) {
        try {
            ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            if (activityManager != null) {
                activityManager.getMemoryInfo(memoryInfo);
                return memoryInfo;
            }
        } catch (Exception exception) {
            VWOLog.e(VWOLog.UNCAUGHT, "Unable to fetch device memory info.", exception,
                    true, false);
        }

        return null;
    }

    public static JSONObject getJsonFromStringMap(Map<String, String> map) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        Set<String> keys = map.keySet();
        String data;
        for(String key : keys) {
            data = map.get(key);
            if(data != null) {
                jsonObject.put(key, map.get(key));
            }
        }

        return jsonObject;
    }
}
