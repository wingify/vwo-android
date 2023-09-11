package com.vwo.mobile.logging;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.vwo.mobile.VWO;
import com.vwo.mobile.data.VWOMessageQueue;
import com.vwo.mobile.models.VWOError;
import com.vwo.mobile.network.VWODownloader;
import com.vwo.mobile.utils.VWOLog;
import com.vwo.mobile.utils.VWOUrlBuilder;
import com.vwo.mobile.utils.VWOUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by aman on Fri 13:12.
 */

public class VWOLoggingClient {
    @SuppressLint("StaticFieldLeak")
    private static VWOLoggingClient vwoClient;
    private VWO mVWO;

    private Map<String, String> extraData;
    private VWOMessageQueue vwoLoggingQueue;

    private VWOLoggingClient() {
        this.extraData = new HashMap<>();
    }

    public static VWOLoggingClient getInstance() {
        if (vwoClient == null) {
            vwoClient = new VWOLoggingClient();
        }

        return vwoClient;
    }

    private void setUncaughtExceptionHandler() {
        Thread.UncaughtExceptionHandler defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        VWOUncaughtExceptionHandler handler;
        if (defaultHandler == null) {
            handler = new VWOUncaughtExceptionHandler();
            Thread.setDefaultUncaughtExceptionHandler(handler);
        } else {
            if (!(defaultHandler instanceof VWOUncaughtExceptionHandler)) {
                handler = new VWOUncaughtExceptionHandler(defaultHandler);
                Thread.setDefaultUncaughtExceptionHandler(handler);
            }
        }
    }

    public void init(@NonNull VWO vwo, @Nullable Map<String, String> extras) {
        this.mVWO = vwo;
        if (extras != null) {
            this.extraData.putAll(extras);
        }
        this.extraData.put(VWOError.MANUFACTURER, Build.MANUFACTURER);
        this.extraData.put(VWOError.BRAND, Build.BRAND);
        this.extraData.put(VWOError.MODEL, Build.MODEL);
        setUncaughtExceptionHandler();
        initializeLoggingQueue();
        VWODownloader.scheduleLoggingQueue(mVWO, getLoggingQueue());
    }

    public static void log(@NonNull Throwable throwable) {
        if (getInstance() == null || getInstance().mVWO == null) {
            showErrorWhileLoggingError("Exception: " + throwable.getMessage());
            return;
        }

        VWOUrlBuilder vwoUrlBuilder = new VWOUrlBuilder(getInstance().mVWO);
        String url = vwoUrlBuilder.getLoggingUrl();
        VWOError.Builder builder = new VWOError.Builder(url, System.currentTimeMillis());
        builder.exception(throwable);
        getInstance().sendData(builder);
    }

    public static void log(@NonNull String message) {
        if (getInstance() == null || getInstance().mVWO == null) {
            showErrorWhileLoggingError(message);
            return;
        }

        VWOUrlBuilder vwoUrlBuilder = new VWOUrlBuilder(getInstance().mVWO);
        String url = vwoUrlBuilder.getLoggingUrl();

        VWOError.Builder builder = new VWOError.Builder(url, System.currentTimeMillis());
        builder.message(message);

        getInstance().sendData(builder);
    }

    private static void showErrorWhileLoggingError(String message) {
        Log.e("VWO_ERROR", "** could not log -> \"" + message + "\", because VWO is not initialized. **");
    }

    public static void log(@NonNull String message, @NonNull Throwable throwable) {
        if (getInstance() == null || getInstance().mVWO == null) {
            showErrorWhileLoggingError(message);
            return;
        }

        try {
            VWOUrlBuilder vwoUrlBuilder = new VWOUrlBuilder(getInstance().mVWO);
            String url = vwoUrlBuilder.getLoggingUrl();

            VWOError.Builder builder = new VWOError.Builder(url, System.currentTimeMillis());
            builder.message(message);
            builder.exception(throwable);

            getInstance().sendData(builder);
        } catch (Exception exception) {
            String errorMessage = "VWO [ " + VWO.version() + " (" + VWO.versionCode() + ") ] " + "Could not send error to server. Reason: " + exception.getMessage();
            VWOLog.e(VWOLog.UNCAUGHT, errorMessage, true, false);
        }

    }

    private void sendData(VWOError.Builder builder) {
        Map<String, String> deviceInfoExtra = new HashMap<>();

        deviceInfoExtra.put(VWOError.EXTERNAL_STORAGE_SIZE, String.valueOf(LogUtils.getExternalStorageSize()));
        deviceInfoExtra.put(VWOError.AVAILABLE_EXTERNAL_STORAGE, String.valueOf(LogUtils.getUnusedExternalStorageSize()));
        deviceInfoExtra.put(VWOError.INTERNAL_STORAGE_SIZE, String.valueOf(LogUtils.getInternalStorageSize()));
        deviceInfoExtra.put(VWOError.AVAILABLE_INTERNAL_STORAGE, String.valueOf(LogUtils.getUnusedInternalStorageSize()));

        ActivityManager.MemoryInfo memoryInfo = LogUtils.getAppMemoryInfo(mVWO.getCurrentContext());
        if (memoryInfo != null) {
            deviceInfoExtra.put(VWOError.AVAILABLE_MEMORY, String.valueOf(memoryInfo.availMem));
            deviceInfoExtra.put(VWOError.IS_MEMORY_LOW, String.valueOf(memoryInfo.lowMemory));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                deviceInfoExtra.put(VWOError.TOTAL_MEMORY, String.valueOf(memoryInfo.totalMem));
            }
        }

        // Will help in identifying logs from same device
        String deviceUUID = VWOUtils.getDeviceUUID(mVWO.getVwoPreference());

        builder.version(VWO.version()).deviceUUID(deviceUUID).versionCode(VWO.versionCode()).extras(extraData).deviceInfoExtras(deviceInfoExtra);

        VWOError vwoError = builder.build();

        if (getLoggingQueue() != null) {
            getLoggingQueue().add(vwoError);
        }
    }

    private void initializeLoggingQueue() {
        try {
            vwoLoggingQueue = VWOMessageQueue.getInstance(mVWO.getCurrentContext(), "loggingQueue.vwo");
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    @Nullable
    public VWOMessageQueue getLoggingQueue() {
        return this.vwoLoggingQueue;
    }
}
