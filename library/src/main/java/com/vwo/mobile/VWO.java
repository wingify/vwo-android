package com.vwo.mobile;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.text.TextUtils;

import com.vwo.mobile.constants.AppConstants;
import com.vwo.mobile.data.VWOData;
import com.vwo.mobile.data.VWOLocalData;
import com.vwo.mobile.data.VWOMessageQueue;
import com.vwo.mobile.events.VWOStatusListener;
import com.vwo.mobile.listeners.VWOActivityLifeCycle;
import com.vwo.mobile.logging.VWOLoggingClient;
import com.vwo.mobile.models.Campaign;
import com.vwo.mobile.models.VWOError;
import com.vwo.mobile.network.ErrorResponse;
import com.vwo.mobile.network.VWODownloader;
import com.vwo.mobile.utils.VWOLog;
import com.vwo.mobile.utils.VWOPreference;
import com.vwo.mobile.utils.VWOUrlBuilder;
import com.vwo.mobile.utils.VWOUtils;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Aman on Wed 27/09/17 at 14:55.
 */
public class VWO implements VWODownloader.DownloadResult {
    /**
     * Constants exposed to developers
     */
    public static class Constants {
        /**
         * Key for local broadcast Receiver
         */
        public static final String NOTIFY_USER_TRACKING_STARTED = "VWOUserStartedTrackingInCampaignNotification";
        public static final String ARG_CAMPAIGN_ID = "vwo_campaign_id";
        public static final String ARG_CAMPAIGN_NAME = "vwo_campaign_name";
        public static final String ARG_VARIATION_ID = "vwo_variation_id";
        public static final String ARG_VARIATION_NAME = "vwo_variation_name";
    }

    private static final String MESSAGE_QUEUE_NAME = "queue_v2.vwo";
    private static final String FAILURE_QUEUE_NAME = "failure_queue_v2.vwo";

    private static final int STATE_FAILED = -1;
    private static final int STATE_NOT_STARTED = 0;
    private static final int STATE_OPTED_OUT = 1;
    private static final int STATE_STARTING = 2;
    private static final int STATE_STARTED = 3;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({
            STATE_NOT_STARTED,
            STATE_STARTING,
            STATE_STARTED,
            STATE_FAILED,
            STATE_OPTED_OUT
    })
    @interface VWOState {
    }

    @SuppressLint("StaticFieldLeak")
    private static VWO sSharedInstance;
    @NonNull
    private final Context mContext;
    private boolean mIsEditMode;
    private VWOUrlBuilder mVWOUrlBuilder;
    private VWOUtils mVWOUtils;
    private VWOPreference mVWOPreference;
    private VWOSocket mVWOSocket;

    private VWOData mVWOData;
    private VWOLocalData mVWOLocalData;
    private VWOConfig vwoConfig;

    private VWOStatusListener mStatusListener;
    private static boolean optOut;

    private static final Object lock = new Object();

    @VWOState
    private int mVWOStartState;
    private VWOMessageQueue messageQueue;
    private VWOMessageQueue failureQueue;

    @VisibleForTesting
    private VWO(@NonNull Context context, @NonNull VWOConfig vwoConfig) {
        this.mContext = context;
        this.mIsEditMode = false;
        this.vwoConfig = vwoConfig;
        this.mVWOStartState = STATE_NOT_STARTED;
    }

    public static Initializer with(@NonNull Context context, @NonNull String apiKey) {
        if (context == null) {
            throw new IllegalArgumentException("context == null");
        }
        if (apiKey == null) {
            throw new IllegalArgumentException("key cannot be null");
        }
        if (sSharedInstance == null) {
            synchronized (lock) {
                if (sSharedInstance == null) {
                    sSharedInstance = new Builder(context)
                            .build();
                }
            }
        }
        return new Initializer(sSharedInstance, apiKey, optOut);
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
     * @deprecated Use {@link VWO#getVariationForKey(String, Object)} instead.
     */
    @SuppressWarnings({"unused", "DeprecatedIsStillUsed"})
    @Nullable
    @Deprecated
    public static Object getVariationForKey(@NonNull String key) {
        synchronized (lock) {
            if (sSharedInstance != null) {
                if (sSharedInstance.mVWOStartState >= STATE_STARTED) {
                    // Only when the VWO has completely started or loaded from disk
                    Object object;

                    if (sSharedInstance.isEditMode()) {
                        object = sSharedInstance.getVwoSocket().getVariationForKey(key);
                    } else {
                        object = sSharedInstance.getVwoData().getVariationForKey(key);
                    }
                    return object;
                } else if (sSharedInstance.mVWOStartState == STATE_OPTED_OUT) {
                    VWOLog.e(VWOLog.DATA_LOGS, "Key not found. User opted out.",
                            true, false);
                    return null;
                } else if (sSharedInstance.mVWOStartState == STATE_FAILED) {
                    VWOLog.e(VWOLog.DATA_LOGS, "Key not found. SDK failed to Initialize",
                            true, false);
                }

            }
            VWOLog.e(VWOLog.DATA_LOGS, new IllegalStateException("Cannot call getVariationForKey(String key) " +
                    "method before VWO SDK is completely initialized."), false, false);
            return null;
        }
    }

    /**
     * Get variation for a given key. returns control if key does not exist in any
     * Campaigns.
     * <p>
     * <p>
     * This function will return a variation for a given key. This function will search for key in
     * all the currently active campaigns.
     * </p>
     * <p>
     * <p>
     * If key exists in multiple campaigns it will return the value for the key of the latest
     * {@link Campaign}.
     * </p>
     * <p>
     * <p>
     * If user is not already part of a the {@link Campaign} in which the key exists. User automatically
     * becomes part of all the campaign for which that key exists.
     * </p>
     *
     * @param key     is the key for which variation is to be requested
     * @param control is the default value to be returned if key is not found in any campaigns.
     * @return an {@link Object} corresponding to given key.
     */
    @SuppressWarnings({"unused", "deprecation"})
    @NonNull
    public static Object getVariationForKey(@NonNull String key, @NonNull Object control) {
        Object data = getVariationForKey(key);
        if (data == null) {
            VWOLog.w(VWOLog.DATA_LOGS, String.format(Locale.ENGLISH, "No variation found for key: %s " +
                    "returning default value", key), false);
            return control;
        } else {
            return data;
        }
    }

    /**
     * Function for marking a goal when it is achieved.
     * <p>
     * NOTE: This function should be called only after initializing VWO SDK.
     *
     * @param goalIdentifier is name of the goal set in VWO dashboard
     */
    public static void trackConversion(@NonNull String goalIdentifier) {
        synchronized (lock) {
            if (sSharedInstance != null) {
                if (sSharedInstance.mVWOStartState >= STATE_STARTED) {
                    if (sSharedInstance.isEditMode()) {
                        sSharedInstance.getVwoSocket().triggerGoal(goalIdentifier);
                    } else {
                        sSharedInstance.mVWOData.saveGoal(goalIdentifier);
                    }
                } else if (sSharedInstance.mVWOStartState == STATE_OPTED_OUT) {
                    VWOLog.e(VWOLog.DATA_LOGS, "Conversion not tracked. User opted out.",
                            true, false);
                } else if (sSharedInstance.mVWOStartState == STATE_FAILED) {
                    VWOLog.e(VWOLog.DATA_LOGS, "Conversion not tracked. SDK Failed to Initialize",
                            true, false);
                }
            } else {
                VWOLog.e(VWOLog.UPLOAD_LOGS, "SDK not initialized completely",
                        false, false);
            }
        }
    }

    /**
     * Function for marking revenue goal when it is achieved.
     *
     * @param goalIdentifier is name of the goal that is set in VWO dashboard
     * @param value          is the revenue achieved by hitting this goal.
     */
    public static void trackConversion(@NonNull String goalIdentifier, double value) {
        synchronized (lock) {
            if (sSharedInstance != null) {
                if (sSharedInstance.mVWOStartState >= STATE_STARTED) {

                    if (sSharedInstance.isEditMode()) {
                        sSharedInstance.getVwoSocket().triggerGoal(goalIdentifier);
                    } else {
                        // Check if already present in persisting data
                        sSharedInstance.mVWOData.saveGoal(goalIdentifier, value);
                    }
                } else if (sSharedInstance.mVWOStartState == STATE_OPTED_OUT) {
                    VWOLog.e(VWOLog.DATA_LOGS, "Conversion not tracked. User opted out.",
                            true, false);
                } else if (sSharedInstance.mVWOStartState == STATE_FAILED) {
                    VWOLog.e(VWOLog.DATA_LOGS, "Conversion not tracked. SDK Failed to Initialize",
                            true, false);
                }
            } else {
                VWOLog.e(VWOLog.UPLOAD_LOGS, "SDK not initialized completely",
                        false, false);
            }
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
     *
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
        VWOLog.i(VWOLog.INITIALIZATION_LOGS, String.format("**** Starting VWO version: %s Build: %s ****",
                version(), versionCode()), false);
        if (getConfig().isOptOut()) {
            this.mVWOStartState = STATE_OPTED_OUT;
            new VWOPreference(sSharedInstance).clear();
            VWOLog.w(VWOLog.INITIALIZATION_LOGS, "Ignoring initalization, User opted out.", false);
            onLoadSuccess();
            return true;
        }
        if (!VWOUtils.checkForInternetPermissions(mContext)) {
            String errMsg = "Internet permission not added to Manifest. Please add" +
                    "\n\n<uses-permission android:name=\"android.permission.INTERNET\"/> \n\npermission to your app Manifest file.";
            VWOLog.e(VWOLog.INITIALIZATION_LOGS, errMsg,
                    false, false);
            onLoadFailure("Missing internet permission");
            return false;
        } else if (!isAndroidSDKSupported()) {
            String errMsg = "Minimum SDK version required is 14";
            VWOLog.e(VWOLog.INITIALIZATION_LOGS, errMsg, false, false);
            onLoadFailure(errMsg);
            return false;
        } else if (!VWOUtils.isValidVwoAppKey(vwoConfig.getApiKey())) {
            VWOLog.e(VWOLog.INITIALIZATION_LOGS, "Invalid API Key: " + vwoConfig.getAppKey(),
                    false, false);
            onLoadFailure("Invalid API Key.");
            return false;
        } else if (this.mVWOStartState == STATE_STARTING) {
            VWOLog.w(VWOLog.INITIALIZATION_LOGS, "VWO is already initializing.",
                    true);
            return true;
        } else if (this.mVWOStartState >= STATE_STARTED) {
            VWOLog.w(VWOLog.INITIALIZATION_LOGS, "VWO is already initialized.",
                    true);
            onLoadSuccess();
            return true;
        } else {
            // Everything is good so far
            this.mVWOStartState = STATE_STARTING;
            ((Application) (mContext)).registerActivityLifecycleCallbacks(new VWOActivityLifeCycle());
            try {
                this.initializeComponents();
            } catch (IOException exception) {
                String message = "Error initalizing SDK components";
                VWOLog.wtf(VWOLog.INITIALIZATION_LOGS, message, exception, true);
                onLoadFailure(message);
                return false;
            }

            int vwoSession = this.mVWOPreference.getInt(AppConstants.DEVICE_SESSION, 0) + 1;
            this.mVWOPreference.putInt(AppConstants.DEVICE_SESSION, vwoSession);

            VWODownloader.fetchFromServer(sSharedInstance, this);

            return true;
        }
    }

    @Override
    public void onDownloadSuccess(@Nullable String data) {
        if (TextUtils.isEmpty(data)) {
            VWOLog.e(VWOLog.DOWNLOAD_DATA_LOGS, "Empty data downloaded : " + data,
                    true, true);
            onDownloadError(new Exception(), "Empty data downloaded");
        } else {
            try {
                JSONArray jsonArray = new JSONArray(data);

                VWOLog.i(VWOLog.INITIALIZATION_LOGS, jsonArray.toString(4), true);

                mVWOData.parseData(jsonArray);
                mVWOLocalData.saveData(jsonArray);
                mVWOStartState = STATE_STARTED;
                onLoadSuccess();
            } catch (JSONException exception) {
                onDownloadError(exception, "Unable to parse data");
            }
        }
    }

    @Override
    public void onDownloadError(@Nullable Exception exception, @Nullable String message) {
        if (exception != null && exception instanceof JSONException) {
            VWOLog.e(VWOLog.DOWNLOAD_DATA_LOGS, exception, true, false);
        }
        if (exception != null && exception.getCause() != null && exception.getCause() instanceof ErrorResponse) {
            ErrorResponse errorResponse = (ErrorResponse) exception.getCause();
            if (errorResponse.getNetworkResponse() != null) {
                int responseCode = errorResponse.getNetworkResponse().getResponseCode();
                if (responseCode >= 400 && responseCode <= 499) {
                    message = "Invalid api key";
                    VWOLog.e(VWOLog.INITIALIZATION_LOGS, message, false, false);
                    onLoadFailure(message);
                    return;
                }
            }
        }
        if (mVWOLocalData.isLocalDataPresent()) {
            mVWOData.parseData(mVWOLocalData.getData());
            mVWOStartState = STATE_STARTED;
            VWOLog.w(VWOLog.INITIALIZATION_LOGS, "Failed to fetch data serving cached data.",
                    false);
            onLoadSuccess();
        } else {
            onLoadFailure(message);
        }
    }

    /**
     * Initialize socket to use VWO in preview mode.
     * Cases in which socket is not initialized:
     * <ul>
     * <li>socket.io dependency is not added to gradle</li>
     * <li>App is in release mode</li>
     * </ul>
     */
    private void initializeSocket() {
        if (getVwoUtils().isDebugMode()) {
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

    private void initializeServerLogging() {
        Map<String, String> extras = new HashMap<>();
        extras.put(VWOError.VWO_SDK_VERSION, version());
        extras.put(VWOError.VWO_SDK_VERSION_CODE, String.valueOf(versionCode()));
        extras.put(VWOError.PACKAGE_NAME, mContext.getPackageName());
        VWOLoggingClient.getInstance().init(sSharedInstance, extras);
    }

    private void initializeComponents() throws IOException {
        initializeServerLogging();
        this.mVWOLocalData = new VWOLocalData(sSharedInstance);
        this.mVWOUtils = new VWOUtils(sSharedInstance);
        this.mVWOUrlBuilder = new VWOUrlBuilder(sSharedInstance);
        this.mVWOData = new VWOData(sSharedInstance);
        this.mVWOPreference = new VWOPreference(sSharedInstance);

        // Initialize message queues
        this.messageQueue = VWOMessageQueue.getInstance(getCurrentContext(), MESSAGE_QUEUE_NAME);
        this.failureQueue = VWOMessageQueue.getInstance(getCurrentContext(), FAILURE_QUEUE_NAME);

        VWODownloader.initializeMessageQueue(sSharedInstance);
        VWODownloader.initializeFailureQueue(sSharedInstance);

        initializeSocket();
    }

    private void onLoadFailure(final String reason) {
        this.mVWOStartState = STATE_FAILED;
        if (mStatusListener != null) {
            new Handler(getCurrentContext().getMainLooper()).post(() -> mStatusListener.onVWOLoadFailure(reason));
        }
    }

    private void onLoadSuccess() {
        if (mStatusListener != null) {
            new Handler(getCurrentContext().getMainLooper()).post(mStatusListener::onVWOLoaded);
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

    /**
     * To opt-out of the VWO SDK, This function can called by passing a true value.
     * <p>
     * After opting out, User won't become part of any campaign and won't be tracked by the
     * VWO.
     * <p>
     * Note: Opting out can only be done before initialization of VWO SDK i.e. before calling
     * {@link Initializer#launch(VWOStatusListener)} or
     * {@link Initializer#launchSynchronously(long)}
     *
     * @param optOut is the {@link Boolean} value.
     */
    public static void setOptOut(boolean optOut) {
        if (sSharedInstance != null && sSharedInstance.getState() >= STATE_STARTING) {
            VWOLog.e(VWOLog.CONFIG_LOGS, "Cannot change opt-out setting after SDK is initialized",
                    false, false);
        } else {
            VWO.optOut = optOut;
        }
    }

    void setConfig(VWOConfig config) {
        this.vwoConfig = config;
    }

    @VWOState
    public int getState() {
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