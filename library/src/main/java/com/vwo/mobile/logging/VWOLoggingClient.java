package com.vwo.mobile.logging;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.vwo.mobile.VWO;
import com.vwo.mobile.data.VWOMessageQueue;
import com.vwo.mobile.models.VWOError;
import com.vwo.mobile.network.VWODownloader;
import com.vwo.mobile.utils.VWOUrlBuilder;

import java.io.IOException;
import java.util.Map;

/**
 * Created by aman on Fri 13:12.
 */

public class VWOLoggingClient {
    @SuppressLint("StaticFieldLeak")
    private static VWOLoggingClient vwoClient;
    private VWO mVWO;

    @Nullable
    private Map<String, String> extraData;
    private VWOMessageQueue vwoLoggingQueue;

    private VWOLoggingClient() {
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
        this.extraData = extras;
        setUncaughtExceptionHandler();
        initializeLoggingQueue();
        VWODownloader.scheduleLoggingQueue(mVWO, vwoLoggingQueue);
    }

    public static void log(@NonNull Throwable throwable) {
        if(vwoClient == null) {
            throw new NullPointerException("Client not initialised");
        }

        VWOUrlBuilder vwoUrlBuilder = new VWOUrlBuilder(vwoClient.mVWO);
        String url = vwoUrlBuilder.getLoggingUrl();
        VWOError.Builder builder = new VWOError.Builder(url,
                System.currentTimeMillis());
        builder.exception(throwable);
        vwoClient.sendData(builder);
    }

    public static void log(@NonNull String message) {
        if(vwoClient == null) {
            throw new NullPointerException("Client not initialized");
        }

        VWOUrlBuilder vwoUrlBuilder = new VWOUrlBuilder(vwoClient.mVWO);
        String url = vwoUrlBuilder.getLoggingUrl();

        VWOError.Builder builder = new VWOError.Builder(url,
                System.currentTimeMillis());
        builder.message(message);

        vwoClient.sendData(builder);
    }

    private void sendData(VWOError.Builder builder) {
        builder.version(VWO.version())
                .versionCode(VWO.versionCode())
                .extras(extraData);

        VWOError vwoError = builder.build();

        if(getLoggingQueue() != null) {
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
    private VWOMessageQueue getLoggingQueue() {
        return this.vwoLoggingQueue;
    }
}
