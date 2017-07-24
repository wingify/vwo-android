package com.vwo.mobile;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.android.gms.analytics.Tracker;
import com.vwo.mobile.constants.AppConstants;
import com.vwo.mobile.data.VWOData;
import com.vwo.mobile.data.VWOLocalData;
import com.vwo.mobile.enums.VWOStartState;
import com.vwo.mobile.events.VWOStatusListener;
import com.vwo.mobile.listeners.VWOActivityLifeCycle;
import com.vwo.mobile.models.Campaign;
import com.vwo.mobile.network.VWODownloader;
import com.vwo.mobile.utils.VWOLog;
import com.vwo.mobile.utils.VWOPreference;
import com.vwo.mobile.utils.VWOUrlBuilder;
import com.vwo.mobile.utils.VWOUtils;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Method;

import io.sentry.Sentry;
import io.sentry.android.AndroidSentryClientFactory;

/**
 * Created by abhishek on 17/09/15 at 10:02 PM.
 */
public class VWO {
    @SuppressLint("StaticFieldLeak")
    private static VWO sSharedInstance;

    private boolean mIsEditMode;
    @NonNull
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

    /**
     *
     *  Get variation for a given key. returns null if key does not exist in any
     *  Campaigns.
     *
     *  This function will return a variation for a given key. This function will search for key in
     *  all the currently active campaigns.
     *
     *  If key exists in multiple campaigns it will return the value for the key in the latest
     *  {@link Campaign}.
     *
     *  If user is not already part of a the {@link Campaign} in which the key exists. User automatically
     *  becomes part of all the campaign for which that key exists.
     *
     *
     * @param key is the key for which variation is to be requested
     * @return an {@link Object} corresponding to given key.
     */
    @SuppressWarnings("unused")
    @Nullable
    public static Object getVariationForKey(@NonNull String key) {

        if (sSharedInstance != null && sSharedInstance.mVWOStartState.getValue() >= VWOStartState.STARTED.getValue()) {
            // Only when the VWO has completely started or loaded from disk
            Object object;

            if (sSharedInstance.isEditMode()) {
                object = sSharedInstance.getVwoSocket().getVariationForKey(key);
            } else {
                object = sSharedInstance.getVwoData().getVariationForKey(key);
            }
            return object;

        }
        VWOLog.e(VWOLog.DATA_LOGS, new IllegalStateException("Cannot call getVariationForKey(String key) " +
                "method before VWO SDK is completely initialized."), false, false);
        return null;
    }

    /**
     *
     *  Get variation for a given key. returns control if key does not exist in any
     *  Campaigns.
     *
     *  This function will return a variation for a given key. This function will search for key in
     *  all the currently active campaigns.
     *
     *  If key exists in multiple campaigns it will return the value for the key of the latest
     *  {@link Campaign}.
     *
     *  If user is not already part of a the {@link Campaign} in which the key exists. User automatically
     *  becomes part of all the campaign for which that key exists.
     *
     * @param key is the key for which variation is to be requested
     * @param control is the default value to be returned if key is not found in any campaigns.
     * @return an {@link Object} corresponding to given key.
     */
    @SuppressWarnings("unused")
    @NonNull
    public static Object getVariationForKey(@NonNull String key, @NonNull Object control) {
        Object data = getVariationForKey(key);
        if (data == null) {
            VWOLog.e(VWOLog.DATA_LOGS, "No data found for key: " + key, false, false);
            return control;
        } else {
            return data;
        }

    }

    /**
     * Function for marking a goal when it is achieved.
     *
     * NOTE: This function should be called only after initializing VWO SDK.
     *
     * @param goalIdentifier is name of the goal set in VWO dashboard
     */
    public static void markConversionForGoal(@NonNull String goalIdentifier) {

        if (sSharedInstance != null && sSharedInstance.mVWOStartState.getValue() >= VWOStartState.STARTED.getValue()) {

            if (sSharedInstance.isEditMode()) {
                sSharedInstance.getVwoSocket().triggerGoal(goalIdentifier);
            } else {
                sSharedInstance.mVWOData.saveGoal(goalIdentifier);
            }
        } else {
            VWOLog.w(VWOLog.UPLOAD_LOGS, "SDK not initialized completely", true);
        }
    }

    /**
     * Function for marking revenue goal when it is achieved.
     *
     * @param goalIdentifier is name of the goal that is set in VWO dashboard
     * @param value          is the revenue achieved by hitting this goal.
     */
    public static void markConversionForGoal(@NonNull String goalIdentifier, double value) {

        if (sSharedInstance != null && sSharedInstance.mVWOStartState.getValue() >= VWOStartState.STARTED.getValue()) {

            if (sSharedInstance.isEditMode()) {
                sSharedInstance.getVwoSocket().triggerGoal(goalIdentifier);
            } else {
                // Check if already present in persisting data
                sSharedInstance.mVWOData.saveGoal(goalIdentifier, value);
            }
        } else {
            VWOLog.w(VWOLog.UPLOAD_LOGS, "SDK not initialized completely", true);
        }
    }

    @SuppressWarnings("SpellCheckingInspection")
    boolean startVwoInstance() {
        VWOLog.v(VWOLog.INITIALIZATION_LOGS, "**** Starting VWO ver " + VWOUtils.getVwoSdkVersion() + " ****");

        final AndroidSentryClientFactory factory = new AndroidSentryClientFactory(mContext);

        if (!VWOUtils.checkForInternetPermissions(mContext)) {
            return false;
        } else if (!VWOUtils.checkIfClassExists("io.socket.client.Socket") && !VWOUtils.checkIfClassExists("com.squareup.okhttp.OkHttpClient")) {
            String errMsg = "VWO is dependent on Socket.IO library.\n" +
                    "In application level build.gradle file add\t" +
                    "compile 'io.socket:socket.io-client:0.8.3'";
            VWOLog.e(VWOLog.INITIALIZATION_LOGS, errMsg, false, false);
            return false;
        } else if (!isAndroidSDKSupported()) {
            Sentry.init(BuildConfig.SENTRY, factory);
            VWOLog.e(VWOLog.INITIALIZATION_LOGS, "Minimum SDK version should be 14", false, true);
            return false;
        } else if (!VWOUtils.isValidVwoAppKey(vwoConfig.getApiKey())) {
            Sentry.init(BuildConfig.SENTRY, factory);
            VWOLog.e(VWOLog.INITIALIZATION_LOGS, "Invalid App Key: " + vwoConfig.getAppKey(), false, false);
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
                    Sentry.init(BuildConfig.SENTRY, factory);
                    if (data.length() == 0) {
                        VWOLog.w(VWOLog.DOWNLOAD_DATA_LOGS, "Empty data downloaded", true);
                        // FIXME: Handle this. Can crash here.
                    } else {
                        try {
                            VWOLog.i(VWOLog.INITIALIZATION_LOGS, data.toString(4), true);
                        } catch (JSONException exception) {
                            VWOLog.e(VWOLog.DOWNLOAD_DATA_LOGS, "Data not Downloaded", exception, true, true);
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
                                mStatusListener.onVWOLoaded();
                            }
                        });

                        Looper.loop();

                    }
                }

                @Override
                public void onDownloadError(Exception ex) {
                    Sentry.init(BuildConfig.SENTRY, factory);
                    if(ex instanceof JSONException) {
                        VWOLog.e(VWOLog.DOWNLOAD_DATA_LOGS, ex, false, true);
                    }
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
                                    mStatusListener.onVWOLoaded();
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
                                    mStatusListener.onVWOLoadFailure();
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

    private void initializeSentry() {

    }

    private void initializeComponents() {
        this.mVWOLocalData = new VWOLocalData(sSharedInstance);
        this.mVWOUtils = new VWOUtils(sSharedInstance);
        this.mVWODownloader = new VWODownloader(sSharedInstance);
        this.mVWOUrlBuilder = new VWOUrlBuilder(sSharedInstance);
        this.mVWOData = new VWOData(sSharedInstance);
        this.mVWOSocket = new VWOSocket(sSharedInstance);
        this.mVWOPreference = new VWOPreference(sSharedInstance);

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

    /**
     * This function is to set up a listener for listening to the initialization event of VWO sdk.
     * i.e. VWO sdk is connected to server and all setting are received.
     *
     * @param listener This listener to be passed to SDK
     */
    public static void setVwoStatusListener(VWOStatusListener listener) {
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

    /**
     * Sets custom key value pair for user segmentation.
     *
     * This function can be used to segment users based on this key value pair.
     * This will decide whether user will be a part of campaign or not.
     *
     * @param key is given key
     * @param value is the value corresponding to the given key.
     */
    public static void setCustomVariable(@NonNull String key, @NonNull String value) {
        if(sSharedInstance == null) {
            throw new IllegalStateException("You need to initialize VWO SDK first and the try calling this function.");
        }

        sSharedInstance.getConfig().addCustomSegment(key, value);
    }

    public Tracker getGATracker() {
        Tracker tracker = null;
        try {
            Method m = mContext.getClass().getMethod("getDefaultTracker", (Class<?>[]) null);
            Object result;
            if (m != null) {
                result = m.invoke(mContext, (Object[]) null);
                if (result != null) {
                    tracker = (Tracker) result;
                }
            }
        } catch (Exception exception) {
            VWOLog.e(VWOLog.ANALYTICS, "Google Analytics enabled on dashboard but not integrated in Application",
                    exception, false, true);
        }

        return tracker;
    }



    public Context getCurrentContext() {
        return this.mContext;
    }

    private boolean isEditMode() {
        return mIsEditMode;
    }

    void setIsEditMode(boolean isEditMode) {
        mIsEditMode = isEditMode;
    }

    private VWOSocket getVwoSocket() {
        return mVWOSocket;
    }

    private VWOData getVwoData() {
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

    VWOUtils getVwoUtils() {
        return mVWOUtils;
    }

    public VWOPreference getVwoPreference() {
        return mVWOPreference;
    }

    public static String version() {
        return BuildConfig.VERSION_NAME;
    }

}
