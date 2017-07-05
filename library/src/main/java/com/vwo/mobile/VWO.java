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
    private VWOConfig vwoConfig;

    private VWOStatusListener mStatusListener;
    private VWOStartState mVWOStartState;

    private VWO(@NonNull Context context, @NonNull VWOConfig vwoConfig) {
        this.mContext = context;
        this.mIsEditMode = false;
        this.vwoConfig = vwoConfig;
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

    @SuppressWarnings("unused")
    public static Object getVariationForKey(String key) {

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
        VWOLog.e(VWOLog.DATA_LOGS, new IllegalStateException("Cannot call this method before VWO SDK is completely initialized."), false);
        return null;
    }

    @SuppressWarnings("unused")
    public static Object getVariationForKey(String key, Object control) {

        Object data = getVariationForKey(key);
        if (data == null) {
            VWOLog.e(VWOLog.DATA_LOGS, "No data found for key: " + key, false);
            return control;
        } else {
            return data;
        }

    }

    /**
     * Function for mark a when a goal is achieved
     *
     * @param goalIdentifier is name of the goal set in VWO dashboard
     */
    public static void markConversionForGoal(String goalIdentifier) {

        if (sSharedInstance != null && sSharedInstance.mVWOStartState.getValue() >= VWOStartState.STARTED.getValue()) {

            VWO vwo = new Builder().build();

            if (vwo.isEditMode()) {
                vwo.getVwoSocket().triggerGoal(goalIdentifier);
            } else {
                sSharedInstance.mVWOData.saveGoal(goalIdentifier);
            }
        }
        VWOLog.w(VWOLog.UPLOAD_LOGS, "SDK not initialized completely", true);
    }

    /**
     * Function to mark revenue goal when it is achieved
     *
     * @param goalIdentifier is name of the goal set in VWO dashboard
     * @param value          is the value in double
     */
    public static void markConversionForGoal(String goalIdentifier, double value) {

        if (sSharedInstance != null && sSharedInstance.mVWOStartState.getValue() >= VWOStartState.STARTED.getValue()) {

            if (sSharedInstance.isEditMode()) {
                sSharedInstance.getVwoSocket().triggerGoal(goalIdentifier);
            } else {
                // Check if already present in persisting data
                sSharedInstance.mVWOData.saveGoal(goalIdentifier, value);
            }
        }
        VWOLog.w(VWOLog.UPLOAD_LOGS, "SDK not initialized completely", true);
    }

    @SuppressWarnings("SpellCheckingInspection")
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    boolean startVwoInstance() {
        VWOLog.v(VWOLog.INITIALIZATION_LOGS, "**** Starting VWO ver " + VWOUtils.getVwoSdkVersion() + " ****");

        assert mContext != null;

        final AndroidSentryClientFactory factory = new AndroidSentryClientFactory(mContext);
//        factory.createSentryClient(new Dsn(ApiConstant.SENTRY));

        if (!VWOUtils.checkForInternetPermissions(mContext)) {
            return false;
        } else if (!VWOUtils.checkIfClassExists("io.socket.client.Socket") && !VWOUtils.checkIfClassExists("com.squareup.okhttp.OkHttpClient")) {
            String errMsg = "VWO is dependent on Socket.IO library.\n" +
                    "In application level build.gradle file add\t" +
                    "compile 'io.socket:socket.io-client:0.8.3'";
            VWOLog.e(VWOLog.INITIALIZATION_LOGS, errMsg, false);
            return false;
        } else if (!isAndroidSDKSupported()) {
            Sentry.init(ApiConstant.SENTRY, factory);
            VWOLog.e(VWOLog.INITIALIZATION_LOGS, "Minimum SDK version should be 14", false);
            return false;
        } else if (!validateVwoAppKey(vwoConfig.getApiKey())) {
            Sentry.init(ApiConstant.SENTRY, factory);
            VWOLog.e(VWOLog.INITIALIZATION_LOGS, "Invalid App Key: " + vwoConfig.getAppKey(), false);
            return false;
        } else if (this.mVWOStartState != VWOStartState.NOT_STARTED) {
            VWOLog.w(VWOLog.INITIALIZATION_LOGS, "VWO already started", true);
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
                        VWOLog.w(VWOLog.DOWNLOAD_DATA_LOGS, "Empty data downloaded", true);
                        // FIXME: Handle this. Can crash here.
                    } else {
                        try {
                            VWOLog.i(VWOLog.INITIALIZATION_LOGS, data.toString(4), true);
                        } catch (JSONException exception) {
                            VWOLog.e(VWOLog.DOWNLOAD_DATA_LOGS, "Data not Downloaded", exception, true);
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
        private VWOConfig vwoConfig;

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
            return new VWO(context, vwoConfig);
        }

        Builder setConfig(VWOConfig vwoConfig) {
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

    public VWOStatusListener getStatusListener() {
        return mStatusListener;
    }

    void setStatusListener(VWOStatusListener mStatusListener) {
        this.mStatusListener = mStatusListener;
    }

    public static void addCustomVariable(String name, String value) {
        if(sSharedInstance == null) {
            throw new IllegalStateException("You need to initialize VWO SDK first and the try calling this function.");
        }

        sSharedInstance.getConfig().addCustomSegment(name, value);
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
        return this.vwoConfig;
    }

    void setConfig(VWOConfig config) {
        this.vwoConfig = config;
    }

    public VWOUtils getVwoUtils() {
        return mVWOUtils;
    }

    public VWOPreference getVwoPreference() {
        return mVWOPreference;
    }

    public static String sdkVersion() {
        return BuildConfig.VERSION_NAME;
    }

}
