package com.vwo.mobile;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.util.Log;

import com.vwo.mobile.constants.ApiConstant;
import com.vwo.mobile.constants.AppConstants;
import com.vwo.mobile.data.VwoData;
import com.vwo.mobile.data.VwoLocalData;
import com.vwo.mobile.enums.VwoStartState;
import com.vwo.mobile.events.VwoStatusListener;
import com.vwo.mobile.listeners.VwoActivityLifeCycle;
import com.vwo.mobile.network.VwoDownloader;
import com.vwo.mobile.utils.VwoLog;
import com.vwo.mobile.utils.VwoPreference;
import com.vwo.mobile.utils.VwoUrlBuilder;
import com.vwo.mobile.utils.VwoUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.sentry.Sentry;
import io.sentry.android.AndroidSentryClientFactory;

/**
 * Created by abhishek on 17/09/15 at 10:02 PM.
 */
public class Vwo {
    @SuppressLint("StaticFieldLeak")
    private static Vwo sSharedInstance;

    private boolean mIsEditMode;
    @Nullable
    private final Context mContext;
    private VwoDownloader mVwoDownloader;
    private VwoUrlBuilder mVwoUrlBuilder;
    private VwoUtils mVwoUtils;
    private VwoPreference mVwoPreference;
    private VwoSocket mVwoSocket;

    private VwoData mVwoData;
    private VwoLocalData mVwoLocalData;
    private VwoConfig vwoConfig;

    private VwoStatusListener mStatusListener;
    private VwoStartState mVwoStartState;

    private Vwo(@NonNull Context context, @NonNull VwoConfig vwoConfig) {
        this.mContext = context;
        this.mIsEditMode = false;
        this.vwoConfig = vwoConfig;
        this.mVwoStartState = VwoStartState.NOT_STARTED;
    }

    private Vwo() {
        this.mContext = null;
        this.mIsEditMode = false;
        this.mVwoDownloader = new VwoDownloader(this);
        this.mVwoUrlBuilder = new VwoUrlBuilder(this);
        this.mVwoLocalData = new VwoLocalData(this);
    }

    /*private Vwo() {

    }*/

    public static Initializer with(@NonNull Context context, @NonNull String apiKey) {
        if (context == null) {
            throw new IllegalArgumentException("context == null");
        }
        if (apiKey == null) {
            throw new IllegalArgumentException("key cannot be null");
        }
        if (sSharedInstance == null) {
            synchronized (Vwo.class) {
                if (sSharedInstance == null) {
                    sSharedInstance = new Builder(context)
                            .build();
                }
            }
        }
        return new Initializer(sSharedInstance, apiKey);
    }

    /*private static synchronized Vwo sharedInstance() {
        if (sSharedInstance != null) {
            return sSharedInstance;
        } else {
            sSharedInstance = new Vwo();
            return sSharedInstance;
        }
    }*/

    @SuppressWarnings("unused")
    public static Object getObjectForKey(String key) {

        if (sSharedInstance != null && sSharedInstance.mVwoStartState.getValue() >= VwoStartState.STARTED.getValue()) {
            // Only when the VWO has completely started or loaded from disk
            Object object;

            if (sSharedInstance.isEditMode()) {
                object = sSharedInstance.getVwoSocket().getObjectForKey(key);
            } else {
                object = sSharedInstance.getVwoData().getVariationForKey(key);
            }
            return object;

        }
        throw new IllegalStateException("Cannot call this method before initializing VWO sdk first.");
    }

    @SuppressWarnings("unused")
    public static Object getObjectForKey(String key, Object control) {

        Object data = getObjectForKey(key);
        if (data == null) {
            Log.e(VwoLog.DOWNLOAD_DATA_LOGS, "No data found for key: " + key);
            return control;
        } else {
            return data;
        }

    }

    @SuppressWarnings("unused")
    public static Object getAllObject() {

        if (sSharedInstance != null && sSharedInstance.mVwoStartState.getValue() >= VwoStartState.STARTED.getValue()) {
            // Only when the VWO has completely started or loaded from disk
            Object object;

            if (sSharedInstance.isEditMode()) {
                object = sSharedInstance.getVwoSocket().getVariation();
            } else {
                object = sSharedInstance.getVwoData().getAllVariations();
            }
            return object;
        }
        Log.w(VwoLog.INITIALIZATION_LOGS, "SDK not initialized completely");
        return new JSONObject();
    }

    public static void markConversionForGoal(String goalIdentifier) {

        if (sSharedInstance != null && sSharedInstance.mVwoStartState.getValue() >= VwoStartState.STARTED.getValue()) {

            Vwo vwo = new Builder().build();

            if (vwo.isEditMode()) {
                vwo.getVwoSocket().triggerGoal(goalIdentifier);
            } else {
                sSharedInstance.mVwoData.saveGoal(goalIdentifier);
            }
        }
        Log.w(VwoLog.INITIALIZATION_LOGS, "SDK not initialized completely");
    }

    public static void markConversionForGoal(String goalIdentifier, double value) {

        if (sSharedInstance != null && sSharedInstance.mVwoStartState.getValue() >= VwoStartState.STARTED.getValue()) {

            if (sSharedInstance.isEditMode()) {
                sSharedInstance.getVwoSocket().triggerGoal(goalIdentifier);
            } else {
                // Check if already present in persisting data
                sSharedInstance.mVwoData.saveGoal(goalIdentifier, value);
            }
        }
        Log.w(VwoLog.INITIALIZATION_LOGS, "SDK not initialized completely");
    }

    @SuppressWarnings("SpellCheckingInspection")
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    boolean startVwoInstance() {
        if(Log.isLoggable(VwoLog.INITIALIZATION_LOGS, Log.INFO)) {
            Log.i(VwoLog.INITIALIZATION_LOGS, "**** Starting VWO ver " + VwoUtils.getVwoSdkVersion() + " ****");
        }

        assert mContext != null;

        final AndroidSentryClientFactory factory = new AndroidSentryClientFactory(mContext);
//        factory.createSentryClient(new Dsn(ApiConstant.SENTRY));

        if (!VwoUtils.checkForInternetPermissions(mContext)) {
            return false;
        } else if (!VwoUtils.checkIfClassExists("io.socket.client.Socket") && !VwoUtils.checkIfClassExists("com.squareup.okhttp.OkHttpClient")) {
            String errMsg = "VWO is dependent on Socket.IO library.\n" +
                    "In application level build.gradle file add\t" +
                    "compile 'io.socket:socket.io-client:0.8.3'";
            Log.e(VwoLog.INITIALIZATION_LOGS, errMsg);
            return false;
        } else if (!isAndroidSDKSupported()) {
            Sentry.init(ApiConstant.SENTRY, factory);
            Log.e(VwoLog.INITIALIZATION_LOGS, "Minimum SDK version should be 14");
            return false;
        } else if (!validateVwoAppKey(vwoConfig.getApiKey())) {
            Sentry.init(ApiConstant.SENTRY, factory);
            Log.e(VwoLog.INITIALIZATION_LOGS, "Invalid App Key: " + vwoConfig.getAppKey());
            return false;
        } else if (this.mVwoStartState != VwoStartState.NOT_STARTED) {
            if(Log.isLoggable(VwoLog.INITIALIZATION_LOGS, Log.WARN)) {
                Log.w(VwoLog.INITIALIZATION_LOGS, "VWO already started");
            }
            return true;
        } else {
            // Everything is good so far
            this.mVwoStartState = VwoStartState.STARTING;
            ((Application) (mContext)).registerActivityLifecycleCallbacks(new VwoActivityLifeCycle());
            this.initializeComponents();

            int vwoSession = this.mVwoPreference.getInt(AppConstants.DEVICE_SESSION, 0) + 1;
            this.mVwoPreference.putInt(AppConstants.DEVICE_SESSION, vwoSession);

            this.mVwoDownloader.fetchFromServer(new VwoDownloader.DownloadResult() {
                @Override
                public void onDownloadSuccess(JSONArray data) {
                    Sentry.init(ApiConstant.SENTRY, factory);
                    if (data.length() == 0) {
                        if(Log.isLoggable(VwoLog.INITIALIZATION_LOGS, Log.WARN)) {
                            Log.w(VwoLog.INITIALIZATION_LOGS, "Empty data downloaded");
                        }
                        // FIXME: Handle this. Can crash here.
                    } else {
                        try {
                            if(Log.isLoggable(VwoLog.INITIALIZATION_LOGS, Log.INFO)) {
                                Log.i(VwoLog.INITIALIZATION_LOGS, data.toString(4));
                            }
                        } catch (JSONException exception) {
                            if(Log.isLoggable(VwoLog.INITIALIZATION_LOGS, Log.ERROR)) {
                                exception.printStackTrace();
                                Log.e(VwoLog.INITIALIZATION_LOGS, "Data not Downloaded: " + exception.getLocalizedMessage());
                            }
                        }
                    }
                    mVwoData.parseData(data);
                    mVwoDownloader.startUpload();
                    mVwoSocket.connectToSocket();
                    mVwoLocalData.saveData(data);
                    mVwoStartState = VwoStartState.STARTED;
                    if (mStatusListener != null) {
                        Looper.prepare();
                        new Handler().post(new Runnable() {
                            @Override
                            public void run() {
                                mStatusListener.onVwoLoaded();
                            }
                        });

                        Looper.loop();

                    }
                }

                @Override
                public void onDownloadError(Exception ex) {
                    Sentry.init(ApiConstant.SENTRY, factory);
                    mVwoDownloader.startUpload();
                    mVwoSocket.connectToSocket();
                    if (mVwoLocalData.isLocalDataPresent()) {
                        mVwoData.parseData(mVwoLocalData.getData());
                        mVwoStartState = VwoStartState.STARTED;
                        if (mStatusListener != null) {
                            Looper.prepare();
                            new Handler().post(new Runnable() {
                                @Override
                                public void run() {
                                    mStatusListener.onVwoLoaded();
                                }
                            });

                            Looper.loop();
                        }
                    } else {
                        mVwoStartState = VwoStartState.NO_INTERNET;
                        if (mStatusListener != null) {
                            Looper.prepare();
                            new Handler().post(new Runnable() {
                                @Override
                                public void run() {
                                    mStatusListener.onVwoLoadFailure();
                                }
                            });

                            Looper.loop();
                        }
                    }

                }
            });

            return true;
        }
    }

    private void initializeComponents() {
        this.mVwoLocalData = new VwoLocalData(this);
        this.mVwoUtils = new VwoUtils(this);
        this.mVwoDownloader = new VwoDownloader(this);
        this.mVwoUrlBuilder = new VwoUrlBuilder(this);
        this.mVwoData = new VwoData(this);
        this.mVwoSocket = new VwoSocket(this);
        this.mVwoPreference = new VwoPreference(this);

    }

    private boolean isAndroidSDKSupported() {
        try {
            int sdkVersion = Build.VERSION.SDK_INT;
            if (sdkVersion >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                return true;
            }
        } catch (Exception var2) {
            // Not able to fetch Android Version. Ignoring and returning false.
        }

        return false;
    }

    private boolean validateVwoAppKey(String appKey) {

        return appKey.contains("-");
    }

    @SuppressWarnings("unused")
    public static void addVwoStatusListener(VwoStatusListener listener) {
        if (sSharedInstance != null) {
            sSharedInstance.mStatusListener = listener;
        }
    }

    public static class Builder {
        private final Context context;
        private VwoConfig vwoConfig;

        Builder(@NonNull Context context) {
            if (context == null) {
                throw new IllegalArgumentException("Context must not be null.");
            }
            this.context = context.getApplicationContext();
        }

        Builder() {
            context = null;
        }

        public Vwo build() {
            return new Vwo(context, vwoConfig);
        }

        Builder setConfig(VwoConfig vwoConfig) {
            if (vwoConfig == null) {
                throw new IllegalArgumentException("Config must not be null.");
            }
            if (this.vwoConfig != null) {
                throw new IllegalStateException("Config already set.");
            }

            this.vwoConfig = vwoConfig;
            return this;
        }

        public Context getContext() {
            return this.context;
        }

    }

    public VwoStatusListener getStatusListener() {
        return mStatusListener;
    }

    void setStatusListener(VwoStatusListener mStatusListener) {
        this.mStatusListener = mStatusListener;
    }

    @Nullable
    public Context getCurrentContext() {
        return this.mContext;
    }

    public boolean isEditMode() {
        return mIsEditMode;
    }

    public void setIsEditMode(boolean isEditMode) {
        mIsEditMode = isEditMode;
    }

    public VwoSocket getVwoSocket() {
        return mVwoSocket;
    }

    public VwoData getVwoData() {
        return mVwoData;
    }

    public VwoUrlBuilder getVwoUrlBuilder() {
        return mVwoUrlBuilder;
    }

    public VwoConfig getConfig() {
        return this.vwoConfig;
    }

    void setConfig(VwoConfig config) {
        this.vwoConfig = config;
    }

    public VwoUtils getVwoUtils() {
        return mVwoUtils;
    }

    public VwoPreference getVwoPreference() {
        return mVwoPreference;
    }

}
