package com.vwo.mobile;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import com.vwo.mobile.constants.ApiConstant;
import com.vwo.mobile.constants.AppConstants;
import com.vwo.mobile.data.VwoData;
import com.vwo.mobile.data.VwoLocalData;
import com.vwo.mobile.enums.VwoStartState;
import com.vwo.mobile.events.VwoStatusListener;
import com.vwo.mobile.listeners.VwoActivityLifeCycle;
import com.vwo.mobile.network.VwoDownloader;
import com.vwo.mobile.utils.VWOLogger;
import com.vwo.mobile.utils.VwoPreference;
import com.vwo.mobile.utils.VwoUrlBuilder;
import com.vwo.mobile.utils.VwoUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import io.sentry.Sentry;
import io.sentry.android.AndroidSentryClientFactory;
import io.sentry.dsn.Dsn;

/**
 * Created by abhishek on 17/09/15 at 10:02 PM.
 */
public class Vwo {
    private static final Logger LOGGER = VWOLogger.getLogger(Vwo.class.getCanonicalName());

//    private static final String TAG = "VWO Mobile";

    private static Vwo sSharedInstance;

    private boolean mIsEditMode;
    private boolean mIsSyncMode;
    private Context mContext;
    private VwoDownloader mVwoDownloader;
    private VwoData mVwoData;
    private VwoUrlBuilder mVwoUrlBuilder;
    private VwoUtils mVwoUtils;
    private VwoPreference mVwoPreference;
    private VwoSocket mVwoSocket;
    private VwoLocalData mVwoLocalData;
    private String mAppKey;
    private String mAccountId;
    private Application mApplication;
    private VwoStatusListener mStatusListener;
    private VwoStartState mVwoStartState;

    protected Vwo(Context context) {
        this.mContext = context;
        this.mIsEditMode = false;

    }

    private Vwo() {
        this.mVwoStartState = VwoStartState.NOT_STARTED;
        this.mIsEditMode = false;
        this.mVwoDownloader = new VwoDownloader(this);
        this.mVwoUrlBuilder = new VwoUrlBuilder(this);
        this.mVwoLocalData = new VwoLocalData(this);
    }

    @SuppressWarnings("unused")
    public static void startAsync(String appKey, Application application) {
        sharedInstance().startVwoInstance(appKey, application);
        sharedInstance().mIsSyncMode = false;
    }

    @SuppressWarnings("unused")
    public static void startAsync(String appKey, Application application, VwoStatusListener statusListener) {
        sharedInstance().startVwoInstance(appKey, application);
        sharedInstance().mStatusListener = statusListener;
        sharedInstance().mIsSyncMode = false;
    }

    @SuppressWarnings("unused")
    public static void start(String appKey, Application application) {
        sharedInstance().mIsSyncMode = true;
        sharedInstance().startVwoInstance(appKey, application);
    }

    private static synchronized Vwo sharedInstance() {
        if (sSharedInstance != null) {
            return sSharedInstance;
        } else {
            sSharedInstance = new Vwo();
            return sSharedInstance;
        }
    }

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
        return null;
    }

    @SuppressWarnings("unused")
    public static Object getObjectForKey(String key, Object control) {

        Object data = getObjectForKey(key);
        if (data == null) {
            LOGGER.warning("No Key Value found. Serving Control");
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
        return new JSONObject();
    }

    public static void markConversionForGoal(String goalIdentifier) {

        if (sSharedInstance != null && sSharedInstance.mVwoStartState.getValue() >= VwoStartState.STARTED.getValue()) {

            Vwo vwo = sharedInstance();

            if (vwo.isEditMode()) {
                vwo.getVwoSocket().triggerGoal(goalIdentifier);
            } else {
                sSharedInstance.mVwoData.saveGoal(goalIdentifier);
            }
        }

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
    }

    @SuppressWarnings("SpellCheckingInspection")
    protected boolean startVwoInstance(String appKey, final Application application) {
        LOGGER.entering(Vwo.class.getSimpleName(), "startVwoInstance(String, Application)", "**** Starting VWO ver " + VwoUtils.getVwoSdkVersion() + " ****");

        final AndroidSentryClientFactory factory = new AndroidSentryClientFactory(application.getApplicationContext());
//        factory.createSentryClient(new Dsn(ApiConstant.SENTRY));

        if (!VwoUtils.checkForInternetPermissions(application.getApplicationContext())) {
            return false;
        } else if (!VwoUtils.checkIfClassExists("io.socket.client.Socket") && !VwoUtils.checkIfClassExists("com.squareup.okhttp.OkHttpClient")) {
            String errMsg = "VWO is dependent on Socket.IO library.\n" +
                    "In application level build.gradle file add\t" +
                    "compile 'io.socket:socket.io-client:0.6.2'";
            LOGGER.finer(errMsg);
            return false;
        } else if (!isAndroidSDKSupported()) {
            Sentry.init(factory);
            LOGGER.finer("Minimum SDK version should be 14");
            return false;
        } else if (!validateVwoAppKey(appKey)) {
            Sentry.init(factory);
            LOGGER.finer("Invalid App Key: " + appKey);
            return false;
        } else if (this.mVwoStartState != VwoStartState.NOT_STARTED) {
            LOGGER.warning("VWO already started");
            return true;
        } else {
            // Everything is good so far
            this.mApplication = application;
            this.mContext = application;
            this.mVwoStartState = VwoStartState.STARTING;
            this.mAccountId = appKey.substring(appKey.indexOf("-") + 1);
            this.mAppKey = appKey.substring(0, appKey.indexOf("-"));
            this.mApplication.registerActivityLifecycleCallbacks(new VwoActivityLifeCycle());
            this.initializeComponents();

            int vwoSession = this.mVwoPreference.getInt(AppConstants.DEVICE_SESSION, 0) + 1;
            this.mVwoPreference.putInt(AppConstants.DEVICE_SESSION, vwoSession);

            this.mVwoDownloader.fetchFromServer(new VwoDownloader.DownloadResult() {
                @Override
                public void onDownloadSuccess(JSONArray data) {
                    Sentry.init(ApiConstant.SENTRY, factory);
                    if (data.length() == 0) {
                        LOGGER.warning("Empty data downloaded");
                        // FIXME: Handle this. Can crash here.
                    } else {
                        try {
                            LOGGER.info(data.toString(4));
                        } catch (JSONException exception) {
                            LOGGER.finer("Data not Downloaded: " + exception.getLocalizedMessage());
                            LOGGER.throwing(Vwo.class.getSimpleName(), "DownloadSuccess", exception);
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
        // TODO: write a function to pass custom segment keys.
        this.mVwoData = new VwoData(this, null);
        this.mVwoSocket = new VwoSocket(this);
        this.mVwoPreference = new VwoPreference(this);

    }

    private boolean isAndroidSDKSupported() {
        try {
            int e = Build.VERSION.SDK_INT;
            if (e >= 14) {
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


    public Context getCurrentContext() {
        return this.mContext;
    }

    public Application getApplication() {
        return mApplication;
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

    public String getAppKey() {
        return mAppKey;
    }

    public String getAccountId() {
        return mAccountId;
    }

    public VwoUtils getVwoUtils() {
        return mVwoUtils;
    }

    public VwoPreference getVwoPreference() {
        return mVwoPreference;
    }

    public boolean isSyncMode() {
        return mIsSyncMode;
    }
}
