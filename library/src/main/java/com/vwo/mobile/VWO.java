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
import com.vwo.mobile.data.VWOData;
import com.vwo.mobile.data.VWOLocalData;
import com.vwo.mobile.enums.VWOStartState;
import com.vwo.mobile.events.VWOStatusListener;
import com.vwo.mobile.listeners.VWOActivityLifeCycle;
import com.vwo.mobile.network.VWODownloader;
import com.vwo.mobile.utils.VWOLog;
import com.vwo.mobile.utils.VWOPreference;
import com.vwo.mobile.utils.VWOUrlBuilder;
import com.vwo.mobile.utils.VWOUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.sentry.Sentry;
import io.sentry.android.AndroidSentryClientFactory;

/**
 * Created by abhishek on 17/09/15 at 10:02 PM.
 */
public class VWO {
    @SuppressLint("StaticFieldLeak")
    private static VWO sSharedInstance;

    private boolean mIsEditMode;
    @Nullable
    private final Context mContext;
    private VWODownloader mVWODownloader;
    private VWOUrlBuilder mVWOUrlBuilder;
    private VWOUtils mVWOUtils;
    private VWOPreference mVWOPreference;
    private VWOSocket mVWOSocket;

    private VWOData mVWOData;
    private VWOLocalData mVWOLocalData;
    private VWOConfig VWOConfig;

    private VWOStatusListener mStatusListener;
    private VWOStartState mVWOStartState;

    private VWO(@NonNull Context context, @NonNull VWOConfig VWOConfig) {
        this.mContext = context;
        this.mIsEditMode = false;
        this.VWOConfig = VWOConfig;
        this.mVWOStartState = VWOStartState.NOT_STARTED;
    }

    private VWO() {
        this.mContext = null;
        this.mIsEditMode = false;
        this.mVWODownloader = new VWODownloader(this);
        this.mVWOUrlBuilder = new VWOUrlBuilder(this);
        this.mVWOLocalData = new VWOLocalData(this);
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
            synchronized (VWO.class) {
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

        if (sSharedInstance != null && sSharedInstance.mVWOStartState.getValue() >= VWOStartState.STARTED.getValue()) {
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
            Log.e(VWOLog.DOWNLOAD_DATA_LOGS, "No data found for key: " + key);
            return control;
        } else {
            return data;
        }

    }

    @SuppressWarnings("unused")
    public static Object getAllObject() {

        if (sSharedInstance != null && sSharedInstance.mVWOStartState.getValue() >= VWOStartState.STARTED.getValue()) {
            // Only when the VWO has completely started or loaded from disk
            Object object;

            if (sSharedInstance.isEditMode()) {
                object = sSharedInstance.getVwoSocket().getVariation();
            } else {
                object = sSharedInstance.getVwoData().getAllVariations();
            }
            return object;
        }
        Log.w(VWOLog.INITIALIZATION_LOGS, "SDK not initialized completely");
        return new JSONObject();
    }

    public static void markConversionForGoal(String goalIdentifier) {

        if (sSharedInstance != null && sSharedInstance.mVWOStartState.getValue() >= VWOStartState.STARTED.getValue()) {

            VWO vwo = new Builder().build();

            if (vwo.isEditMode()) {
                vwo.getVwoSocket().triggerGoal(goalIdentifier);
            } else {
                sSharedInstance.mVWOData.saveGoal(goalIdentifier);
            }
        }
        Log.w(VWOLog.INITIALIZATION_LOGS, "SDK not initialized completely");
    }

    public static void markConversionForGoal(String goalIdentifier, double value) {

        if (sSharedInstance != null && sSharedInstance.mVWOStartState.getValue() >= VWOStartState.STARTED.getValue()) {

            if (sSharedInstance.isEditMode()) {
                sSharedInstance.getVwoSocket().triggerGoal(goalIdentifier);
            } else {
                // Check if already present in persisting data
                sSharedInstance.mVWOData.saveGoal(goalIdentifier, value);
            }
        }
        Log.w(VWOLog.INITIALIZATION_LOGS, "SDK not initialized completely");
    }

    @SuppressWarnings("SpellCheckingInspection")
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    boolean startVwoInstance() {
        if(Log.isLoggable(VWOLog.INITIALIZATION_LOGS, Log.INFO)) {
            Log.i(VWOLog.INITIALIZATION_LOGS, "**** Starting VWO ver " + VWOUtils.getVwoSdkVersion() + " ****");
        }

        assert mContext != null;

        final AndroidSentryClientFactory factory = new AndroidSentryClientFactory(mContext);
//        factory.createSentryClient(new Dsn(ApiConstant.SENTRY));

        if (!VWOUtils.checkForInternetPermissions(mContext)) {
            return false;
        } else if (!VWOUtils.checkIfClassExists("io.socket.client.Socket") && !VWOUtils.checkIfClassExists("com.squareup.okhttp.OkHttpClient")) {
            String errMsg = "VWO is dependent on Socket.IO library.\n" +
                    "In application level build.gradle file add\t" +
                    "compile 'io.socket:socket.io-client:0.8.3'";
            Log.e(VWOLog.INITIALIZATION_LOGS, errMsg);
            return false;
        } else if (!isAndroidSDKSupported()) {
            Sentry.init(ApiConstant.SENTRY, factory);
            Log.e(VWOLog.INITIALIZATION_LOGS, "Minimum SDK version should be 14");
            return false;
        } else if (!validateVwoAppKey(VWOConfig.getApiKey())) {
            Sentry.init(ApiConstant.SENTRY, factory);
            Log.e(VWOLog.INITIALIZATION_LOGS, "Invalid App Key: " + VWOConfig.getAppKey());
            return false;
        } else if (this.mVWOStartState != VWOStartState.NOT_STARTED) {
            if(Log.isLoggable(VWOLog.INITIALIZATION_LOGS, Log.WARN)) {
                Log.w(VWOLog.INITIALIZATION_LOGS, "VWO already started");
            }
            return true;
        } else {
            // Everything is good so far
            this.mVWOStartState = VWOStartState.STARTING;
            ((Application) (mContext)).registerActivityLifecycleCallbacks(new VWOActivityLifeCycle());
            this.initializeComponents();

            int vwoSession = this.mVWOPreference.getInt(AppConstants.DEVICE_SESSION, 0) + 1;
            this.mVWOPreference.putInt(AppConstants.DEVICE_SESSION, vwoSession);

            this.mVWODownloader.fetchFromServer(new VWODownloader.DownloadResult() {
                @Override
                public void onDownloadSuccess(JSONArray data) {
                    Sentry.init(ApiConstant.SENTRY, factory);
                    if (data.length() == 0) {
                        if(Log.isLoggable(VWOLog.INITIALIZATION_LOGS, Log.WARN)) {
                            Log.w(VWOLog.INITIALIZATION_LOGS, "Empty data downloaded");
                        }
                        // FIXME: Handle this. Can crash here.
                    } else {
                        try {
                            if(Log.isLoggable(VWOLog.INITIALIZATION_LOGS, Log.INFO)) {
                                Log.i(VWOLog.INITIALIZATION_LOGS, data.toString(4));
                            }
                        } catch (JSONException exception) {
                            if(Log.isLoggable(VWOLog.INITIALIZATION_LOGS, Log.ERROR)) {
                                exception.printStackTrace();
                                Log.e(VWOLog.INITIALIZATION_LOGS, "Data not Downloaded: " + exception.getLocalizedMessage());
                            }
                        }
                    }
                    mVWOData.parseData(data);
                    mVWODownloader.startUpload();
                    mVWOSocket.connectToSocket();
                    mVWOLocalData.saveData(data);
                    mVWOStartState = VWOStartState.STARTED;
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
                    mVWODownloader.startUpload();
                    mVWOSocket.connectToSocket();
                    if (mVWOLocalData.isLocalDataPresent()) {
                        mVWOData.parseData(mVWOLocalData.getData());
                        mVWOStartState = VWOStartState.STARTED;
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
                        mVWOStartState = VWOStartState.NO_INTERNET;
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
        this.mVWOLocalData = new VWOLocalData(this);
        this.mVWOUtils = new VWOUtils(this);
        this.mVWODownloader = new VWODownloader(this);
        this.mVWOUrlBuilder = new VWOUrlBuilder(this);
        this.mVWOData = new VWOData(this);
        this.mVWOSocket = new VWOSocket(this);
        this.mVWOPreference = new VWOPreference(this);

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
    public static void addVwoStatusListener(VWOStatusListener listener) {
        if (sSharedInstance != null) {
            sSharedInstance.mStatusListener = listener;
        }
    }

    public static class Builder {
        private final Context context;
        private VWOConfig VWOConfig;

        Builder(@NonNull Context context) {
            if (context == null) {
                throw new IllegalArgumentException("Context must not be null.");
            }
            this.context = context.getApplicationContext();
        }

        Builder() {
            context = null;
        }

        public VWO build() {
            return new VWO(context, VWOConfig);
        }

        Builder setConfig(VWOConfig VWOConfig) {
            if (VWOConfig == null) {
                throw new IllegalArgumentException("Config must not be null.");
            }
            if (this.VWOConfig != null) {
                throw new IllegalStateException("Config already set.");
            }

            this.VWOConfig = VWOConfig;
            return this;
        }

        public Context getContext() {
            return this.context;
        }

    }

    public VWOStatusListener getStatusListener() {
        return mStatusListener;
    }

    void setStatusListener(VWOStatusListener mStatusListener) {
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

    public VWOSocket getVwoSocket() {
        return mVWOSocket;
    }

    public VWOData getVwoData() {
        return mVWOData;
    }

    public VWOUrlBuilder getVwoUrlBuilder() {
        return mVWOUrlBuilder;
    }

    public VWOConfig getConfig() {
        return this.VWOConfig;
    }

    void setConfig(VWOConfig config) {
        this.VWOConfig = config;
    }

    public VWOUtils getVwoUtils() {
        return mVWOUtils;
    }

    public VWOPreference getVwoPreference() {
        return mVWOPreference;
    }

}
