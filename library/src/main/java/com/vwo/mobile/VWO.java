package com.vwo.mobile;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.vwo.mobile.constants.AppConstants;
import com.vwo.mobile.data.VWOData;
import com.vwo.mobile.data.VWOLocalData;
import com.vwo.mobile.data.VWOMessageQueue;
import com.vwo.mobile.enums.VWOStartState;
import com.vwo.mobile.events.VWOStatusListener;
import com.vwo.mobile.listeners.VWOActivityLifeCycle;
import com.vwo.mobile.logging.VWOLoggingClient;
import com.vwo.mobile.models.Campaign;
import com.vwo.mobile.network.VWODownloader;
import com.vwo.mobile.logging.VWOLog;
import com.vwo.mobile.utils.VWOPreference;
import com.vwo.mobile.utils.VWOUrlBuilder;
import com.vwo.mobile.utils.VWOUtils;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import io.sentry.Sentry;
import io.sentry.SentryClient;
import io.sentry.SentryClientFactory;
import io.sentry.android.AndroidSentryClientFactory;

/**
 * Created by abhishek on 17/09/15 at 10:02 PM.
 */
public class VWO {
    /**
     * Constants exposed to developers
     */
    public static class Constants {
        /**
         *  Key for local broadcast Receiver
         */
        public static final String NOTIFY_USER_TRACKING_STARTED = "VWOUserStartedTrackingInCampaignNotification";
        public static final String ARG_CAMPAIGN_ID = "vwo_campaign_id";
        public static final String ARG_CAMPAIGN_NAME = "vwo_campaign_name";
        public static final String ARG_VARIATION_ID = "vwo_variation_id";
        public static final String ARG_VARIATION_NAME = "vwo_variation_name";
    }

    private static final String MESSAGE_QUEUE_NAME = "queue_v1.vwo";
    private static final String FAILURE_QUEUE_NAME = "failure_queue_v1.vwo";

    @SuppressLint("StaticFieldLeak")
    private static VWO sSharedInstance;
    @NonNull
    private final Context mContext;
    private boolean mIsEditMode;
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
    private VWOMessageQueue messageQueue;
    private VWOMessageQueue failureQueue;

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
     * Get variation for a given key. returns null if key does not exist in any
     * Campaigns.
     * <p>
     * This function will return a variation for a given key. This function will search for key in
     * all the currently active campaigns.
     * <p>
     * If key exists in multiple campaigns it will return the value for the key in the latest
     * {@link Campaign}.
     * <p>
     * If user is not already part of a the {@link Campaign} in which the key exists. User automatically
     * becomes part of all the campaign for which that key exists.
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
     * Get variation for a given key. returns control if key does not exist in any
     * Campaigns.
     *
     * <p>
     * This function will return a variation for a given key. This function will search for key in
     * all the currently active campaigns.
     * </p>
     *
     * <p>
     * If key exists in multiple campaigns it will return the value for the key of the latest
     * {@link Campaign}.
     * </p>
     *
     * <p>
     * If user is not already part of a the {@link Campaign} in which the key exists. User automatically
     * becomes part of all the campaign for which that key exists.
     * </p>
     *
     * @param key     is the key for which variation is to be requested
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
            VWOLog.e(VWOLog.UPLOAD_LOGS, "SDK not initialized completely", false, false);
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
            VWOLog.e(VWOLog.UPLOAD_LOGS, "SDK not initialized completely", false, false);
        }
    }

    /**
     * This function is to set up a listener for listening to the initialization event of VWO sdk.
     * i.e. VWO sdk is connected to server and all setting are received.
     *
     * @param listener This listener to be passed to SDK
     */
    public static void setVWOStatusListener(VWOStatusListener listener) {
        if (sSharedInstance != null) {
            sSharedInstance.mStatusListener = listener;
        } else {
            VWOLog.e(VWOLog.CONFIG_LOGS, "SDK not initialized, unable to setup VWOStatusListener",
                    false, false);
        }
    }

    /**
     * Sets custom key value pair for user segmentation.
     * <p>
     * This function can be used to segment users based on this key value pair.
     * This will decide whether user will be a part of campaign or not.
     * </p>
     * @param key   is given key
     * @param value is the value corresponding to the given key.
     */
    public static void setCustomVariable(@NonNull String key, @NonNull String value) {
        if (sSharedInstance == null) {
            throw new IllegalStateException("You need to initialize VWO SDK first and the try calling this function.");
        }

        sSharedInstance.getConfig().addCustomSegment(key, value);
    }

    public static String version() {
        return BuildConfig.VERSION_NAME;
    }

    public static int versionCode() {
        return BuildConfig.VERSION_CODE;
    }

    @SuppressWarnings("SpellCheckingInspection")
    boolean startVwoInstance() {
        VWOLog.v(VWOLog.INITIALIZATION_LOGS, "**** Starting VWO ver " + VWO.version() + " ****");
        if (!VWOUtils.checkForInternetPermissions(mContext)) {
            String errMsg = "Internet permission not added to Manifest. Please add" +
                    "\n\n<uses-permission android:name=\"android.permission.INTERNET\"/> \n\npermission to your app Manifest file.";
            VWOLog.e(VWOLog.INITIALIZATION_LOGS, errMsg,
                    false, false);
            onLoadFailure("Missing internet permission");
            return false;
        } else if (!isAndroidSDKSupported()) {
            String errMsg = "Minimum SDK version required is 14";
            initializeSentry();
            VWOLog.e(VWOLog.INITIALIZATION_LOGS, errMsg, false, false);
            onLoadFailure(errMsg);
            return false;
        } else if (!VWOUtils.isValidVwoAppKey(vwoConfig.getApiKey())) {
            initializeSentry();
            VWOLog.e(VWOLog.INITIALIZATION_LOGS, "Invalid API Key: " + vwoConfig.getAppKey(), false, false);
            onLoadFailure("Invalid API Key.");
            return false;
        } else if (this.mVWOStartState == VWOStartState.STARTING) {
            VWOLog.w(VWOLog.INITIALIZATION_LOGS, "VWO is already in intialization state.", true);
            return true;
        } else {
            // Everything is good so far
            this.mVWOStartState = VWOStartState.STARTING;
            ((Application) (mContext)).registerActivityLifecycleCallbacks(new VWOActivityLifeCycle());
            try {
                this.initializeComponents();
            } catch (IOException exception) {
                String message = "Unable to initialize vwo message queue";
                onLoadFailure(message);
                return false;
            }

            int vwoSession = this.mVWOPreference.getInt(AppConstants.DEVICE_SESSION, 0) + 1;
            this.mVWOPreference.putInt(AppConstants.DEVICE_SESSION, vwoSession);

            this.mVWODownloader.fetchFromServer(new VWODownloader.DownloadResult() {
                @Override
                public void onDownloadSuccess(JSONArray data) {
                    initializeSentry();
                    if (data.length() == 0) {
                        VWOLog.e(VWOLog.DOWNLOAD_DATA_LOGS, "Empty data downloaded : " + data, true, true);
                        // FIXME: Handle this. Can crash here.
                    } else {
                        try {
                            VWOLog.i(VWOLog.INITIALIZATION_LOGS, data.toString(4), true);
                        } catch (JSONException exception) {
                            VWOLog.e(VWOLog.DOWNLOAD_DATA_LOGS, "Data not Downloaded", exception, true, true);
                        }
                    }
                    mVWOData.parseData(data);
                    mVWODownloader.initializeMessageQueue();
                    mVWODownloader.initializeFailureQueue();
                    initializeSocket();
                    mVWOLocalData.saveData(data);
                    mVWOStartState = VWOStartState.STARTED;
                    onLoadSuccess();
                }

                @Override
                public void onDownloadError(Exception ex) {
                    initializeSentry();
                    if (ex instanceof JSONException) {
                        VWOLog.e(VWOLog.DOWNLOAD_DATA_LOGS, ex, false, true);
                    }
                    mVWODownloader.initializeMessageQueue();
                    mVWODownloader.initializeFailureQueue();
                    initializeSocket();
                    if (mVWOLocalData.isLocalDataPresent()) {
                        mVWOData.parseData(mVWOLocalData.getData());
                        mVWOStartState = VWOStartState.STARTED;
                        VWOLog.w(VWOLog.INITIALIZATION_LOGS, "Failed to fetch data serving cached data.", false);
                        onLoadSuccess();
                    } else {
                        String errMsg = "Either slow or not internet";
                        mVWOStartState = VWOStartState.NO_INTERNET;
                        onLoadFailure(errMsg);
                    }
                }
            });

            return true;
        }
    }

    private void initializeSocket() {
        if(getVwoUtils().isDebugMode()) {
            if (VWOUtils.checkIfClassExists("io.socket.client.Socket")) {
                this.mVWOSocket = new VWOSocket(sSharedInstance);
                mVWOSocket.connectToSocket();
            } else {
                VWOLog.e(VWOLog.INITIALIZATION_LOGS, "You need to add following dependency " +
                        "\n\t\tcompile 'io.socket:socket.io-client:1.0.0\n" +
                        " to your build.gradle file in order to use VWO's preview mode.",
                        false, false);
            }
        } else {
            VWOLog.i(VWOLog.INITIALIZATION_LOGS, "Skipping socket connection." +
                    " Application is not in debug mode", true);
        }
    }

    private void initializeSentry() {
        VWOLoggingClient.getInstance().init();

        if(VWOUtils.checkIfClassExists("io.sentry.Sentry")) {
            Map<String, String> extras = new HashMap<>();
            extras.put("VWO-SDK-Version", version());
            extras.put("VWO-SDK-Version-Code", String.valueOf(versionCode()));
            extras.put("VWO-Account-ID", vwoConfig.getAccountId());
            extras.put("Package-name", mContext.getPackageName());
            SentryClient sentryClient = Sentry.init(BuildConfig.SENTRY,
                    new AndroidSentryClientFactory(mContext));
            sentryClient.setTags(extras);
        } else {
            VWOLog.e(VWOLog.INITIALIZATION_LOGS, "you need to add following dependency " +
                    "\"compile 'io.sentry:sentry-android:1.4.0'\"" +
                    " to your build.gradle file", false, false);
        }
    }

    private void initializeComponents() throws IOException {
        this.mVWOLocalData = new VWOLocalData(sSharedInstance);
        this.mVWOUtils = new VWOUtils(sSharedInstance);
        this.mVWODownloader = new VWODownloader(sSharedInstance);
        this.mVWOUrlBuilder = new VWOUrlBuilder(sSharedInstance);
        this.mVWOData = new VWOData(sSharedInstance);
        this.mVWOPreference = new VWOPreference(sSharedInstance);
        this.messageQueue = VWOMessageQueue.getInstance(getCurrentContext(), MESSAGE_QUEUE_NAME);
        this.failureQueue = VWOMessageQueue.getInstance(getCurrentContext(), FAILURE_QUEUE_NAME);
    }

    private void onLoadFailure(final String reason) {
        if (mStatusListener != null) {
            new Handler(mContext.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    mStatusListener.onVWOLoadFailure(reason);
                }
            });
        }
    }

    private void onLoadSuccess() {
        if (mStatusListener != null) {
            new Handler(mContext.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    mStatusListener.onVWOLoaded();
                }
            });
        }
    }

    private boolean isAndroidSDKSupported() {
        try {
            int sdkVersion = Build.VERSION.SDK_INT;
            if (sdkVersion >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                return true;
            }
        } catch (Exception exception) {
            // Not able to fetch Android Version. Ignoring and returning false.
            VWOLog.e(VWOLog.INITIALIZATION_LOGS, exception, false, false);
        }

        return false;
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

    public VWOStartState getState() {
        return this.mVWOStartState;
    }

    public VWOMessageQueue getMessageQueue() {
        return this.messageQueue;
    }

    public VWOMessageQueue getFailureQueue() {
        return this.failureQueue;
    }

    @Nullable
    public VWOStatusListener getStatusListener() {
        return mStatusListener;
    }

    VWOUtils getVwoUtils() {
        return mVWOUtils;
    }

    public VWOPreference getVwoPreference() {
        return mVWOPreference;
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
}