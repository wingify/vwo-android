package com.vwo.mobile;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.text.TextUtils;

import com.vwo.mobile.constants.AppConstants;
import com.vwo.mobile.data.VWOData;
import com.vwo.mobile.data.VWOLocalData;
import com.vwo.mobile.data.VWOMessageQueue;
import com.vwo.mobile.events.PreviewListener;
import com.vwo.mobile.gestures.ShakeDetector;
import com.vwo.mobile.listeners.VWOActivityLifeCycle;
import com.vwo.mobile.logging.VWOLoggingClient;
import com.vwo.mobile.meg.CampaignGroupMapper;
import com.vwo.mobile.models.Campaign;
import com.vwo.mobile.models.VWOError;
import com.vwo.mobile.models.Variation;
import com.vwo.mobile.network.ErrorResponse;
import com.vwo.mobile.network.VWODownloader;
import com.vwo.mobile.timetracker.TimeTracker;
import com.vwo.mobile.utils.VWOLog;
import com.vwo.mobile.utils.VWOPreference;
import com.vwo.mobile.utils.VWOUrlBuilder;
import com.vwo.mobile.utils.VWOUtils;
import com.vwo.mobile.v3.EUManager;
import com.vwo.mobile.v3.MEGManager;
import com.vwo.mobile.v3.MockServerResponse;
import com.vwo.mobile.v3.ServerResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import static com.vwo.mobile.Connection.FAILED;
import static com.vwo.mobile.Connection.NOT_STARTED;
import static com.vwo.mobile.Connection.OPTED_OUT;
import static com.vwo.mobile.Connection.STARTED;
import static com.vwo.mobile.Connection.STARTING;

/**
 * Created by Aman on Wed 27/09/17 at 14:55.
 */
public class VWO implements VWODownloader.DownloadResult, PreviewListener {
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
        public static final String CAMPAIGN_TYPE = "type";
        public static final String CAMPAIGN_ID = "id";
        public static final String CAMPAIGN_GROUPS = "groups";
        public static final String CAMPAIGN_TEST_KEY = "test_key";
    }

    private static final String MESSAGE_QUEUE_NAME = "queue_v2.vwo";
    private static final String FAILURE_QUEUE_NAME = "failure_queue_v2.vwo";

    @SuppressLint("StaticFieldLeak")
    private static VWO sSharedInstance;
    @NonNull
    private final Context mContext;
    private boolean mIsEditMode;
    private VWOUrlBuilder mVWOUrlBuilder;
    private VWOPreference mVWOPreference;
    private VWOSocket mVWOSocket;

    private VWOData mVWOData;
    private static VWOLocalData mVWOLocalData;
    private VWOConfig vwoConfig;

    private static Boolean optOut = null;

    private static final Object lock = new Object();

    @Connection.State
    private int mVWOStartState;
    private VWOMessageQueue messageQueue;
    private VWOMessageQueue failureQueue;

    private VWO(@NonNull Context context, @NonNull VWOConfig vwoConfig) {
        this.mContext = context;
        this.mIsEditMode = false;
        this.vwoConfig = vwoConfig;
        this.mVWOStartState = NOT_STARTED;
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
                    sSharedInstance = new Builder(context).build();
                }
            }
        }
        return new Initializer(sSharedInstance, apiKey, optOut);
    }

    public static String getCampaign(String userId, HashMap<String, String> args) {
        MEGManager megManager = new MEGManager(sSharedInstance);
        return megManager.getCampaign(userId, args);
    }

    @Nullable
    public static String getVariationNameForTestKey(@NonNull String testKey) {
        if (TextUtils.isEmpty(testKey)) {
            VWOLog.w(VWOLog.INITIALIZATION_LOGS, "testKey cannot be null or empty", false);
            return null;
        }
        Variation variation = null;
        String message = "";
        synchronized (lock) {
            if (sSharedInstance != null) {
                if (sSharedInstance.mVWOStartState >= STARTED) {
                    variation = sSharedInstance.getVwoData().getVariationForCampaign(testKey);
                } else if (sSharedInstance.mVWOStartState == OPTED_OUT) {
                    message = "User opted out.";
                } else if (sSharedInstance.mVWOStartState == FAILED) {
                    message = "SDK failed to Initialize.";
                } else {
                    message = "SDK is initializing";
                }
            } else {
                message = "SDK is not initialized";
            }
        }

        if (variation == null) {
            VWOLog.w(VWOLog.DATA_LOGS, String.format(Locale.ENGLISH, "No campaign found for given test key : %s, reason: %s", testKey, !TextUtils.isEmpty(message) ? message : "Campaign does not exist."), false);
            return null;
        } else {
            return variation.getName();
        }
    }

    /**
     * Get an {@link Integer} value for a given key. returns control if key does not exist in any
     * Campaigns.
     * <p>
     * <p>
     * This function will return an {@link Integer} value for a given key. This function will search for key in
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
     * @param key          is the key for which variation is to be requested
     * @param defaultValue is the {@link Integer} value to be returned in case of missing key or invalid value.
     * @return an {@link Integer} value corresponding to given key.
     */
    public static int getIntegerForKey(@NonNull String key, int defaultValue) {
        if (TextUtils.isEmpty(key)) {
            throw new NullPointerException("key cannot be null or empty");
        }
        Object data = getObjectForKey(key, defaultValue);
        if (data == null) {
            return defaultValue;
        } else {
            try {
                if (!(data instanceof Integer)) {
                    VWOLog.w(VWOLog.DATA_LOGS, String.format(Locale.ENGLISH, "Casting %s to %s.", data.getClass().getName(), int.class.getName()), false);
                }
                return Integer.parseInt(String.valueOf(data));
            } catch (ClassCastException | NumberFormatException exception) {
                VWOLog.e(VWOLog.DATA_LOGS, String.format(Locale.ENGLISH, "Cannot cast %s to %s. Returning default value", data.getClass().getName(), int.class.getName()), exception, false, false);
            } catch (Exception exception) {
                VWOLog.e(VWOLog.DATA_LOGS, "Parse Exception. Returning Default value", exception, false, false);
            }
        }
        return defaultValue;
    }

    /**
     * Get an {@link Integer} value for a given key and testKey. returns control if key does not exist in any
     * Campaigns.
     * <p>
     * <p>
     * This function will return an {@link Integer} value for a given key and testKey. This function will search for key in
     * all the currently active campaigns and match the selected campaign testKey with input testKey.
     * </p>
     *
     * @param testKey      is the testKey of the campaign which is used along with key to fetch the variation.
     * @param key          is the key for which variation is to be requested
     * @param defaultValue is the {@link Integer} value to be returned in case of missing key or invalid value.
     * @return an {@link Integer} value corresponding to given key.
     */
    public static int getIntegerForKey(@NonNull String testKey, @NonNull String key, int defaultValue) {
        if (TextUtils.isEmpty(key)) {
            throw new NullPointerException("key cannot be null or empty");
        }
        Object data = getObjectForKey(testKey, key, defaultValue);
        if (data == null) {
            return defaultValue;
        } else {
            try {
                if (!(data instanceof Integer)) {
                    VWOLog.w(VWOLog.DATA_LOGS, String.format(Locale.ENGLISH, "Casting %s to %s.", data.getClass().getName(), int.class.getName()), false);
                }
                return Integer.parseInt(String.valueOf(data));
            } catch (ClassCastException | NumberFormatException exception) {
                VWOLog.e(VWOLog.DATA_LOGS, String.format(Locale.ENGLISH, "Cannot cast %s to %s. Returning default value", data.getClass().getName(), int.class.getName()), exception, false, false);
            } catch (Exception exception) {
                VWOLog.e(VWOLog.DATA_LOGS, "Parse Exception. Returning Default value", exception, false, false);
            }
        }
        return defaultValue;
    }

    /**
     * Get a {@link String} value for a given key. returns control if key does not exist in any
     * Campaigns.
     * <p>
     * <p>
     * This function will return a {@link String} value for a given key. This function will search for key in
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
     * @param key          is the key for which variation is to be requested
     * @param defaultValue is the {@link String} value to be returned in case of missing key or invalid value.
     * @return a {@link String} value corresponding to given key.
     */
    public static String getStringForKey(@NonNull String key, @Nullable String defaultValue) {
        if (TextUtils.isEmpty(key)) {
            throw new NullPointerException("key cannot be null or empty");
        }
        Object data = getObjectForKey(key, defaultValue);
        if (data == null) {
            return defaultValue;
        } else {
            try {
                if (!(data instanceof String)) {
                    VWOLog.w(VWOLog.DATA_LOGS, String.format(Locale.ENGLISH, "Casting %s to %s.", data.getClass().getName(), String.class.getName()), false);
                }
                return String.valueOf(data);
            } catch (ClassCastException exception) {
                VWOLog.e(VWOLog.DATA_LOGS, String.format(Locale.ENGLISH, "Cannot cast %s to %s. Returning Default value.", data.getClass().getName(), String.class.getName()), exception, false, false);
            } catch (Exception exception) {
                VWOLog.e(VWOLog.DATA_LOGS, "Parse Exception. Returning Default value", exception, false, false);
                return defaultValue;
            }
        }
        return defaultValue;
    }

    /**
     * Get an {@link String} value for a given key and testKey. returns control if key does not exist in any
     * Campaigns.
     * <p>
     * <p>
     * This function will return an {@link String} value for a given key and testKey. This function will search for key in
     * all the currently active campaigns and match the selected campaign testKey with input testKey.
     * </p>
     *
     * @param testKey      is the testKey of the campaign which is used along with key to fetch the variation.
     * @param key          is the key for which variation is to be requested
     * @param defaultValue is the {@link String} value to be returned in case of missing key or invalid value.
     * @return an {@link String} value corresponding to given key.
     */
    public static String getStringForKey(@NonNull String testKey, @NonNull String key, @Nullable String defaultValue) {
        if (TextUtils.isEmpty(key)) {
            throw new NullPointerException("key cannot be null or empty");
        }
        Object data = getObjectForKey(testKey, key, defaultValue);
        if (data == null) {
            return defaultValue;
        } else {
            try {
                if (!(data instanceof String)) {
                    VWOLog.w(VWOLog.DATA_LOGS, String.format(Locale.ENGLISH, "Casting %s to %s.", data.getClass().getName(), String.class.getName()), false);
                }
                return String.valueOf(data);
            } catch (ClassCastException exception) {
                VWOLog.e(VWOLog.DATA_LOGS, String.format(Locale.ENGLISH, "Cannot cast %s to %s. Returning Default value.", data.getClass().getName(), String.class.getName()), exception, false, false);
            } catch (Exception exception) {
                VWOLog.e(VWOLog.DATA_LOGS, "Parse Exception. Returning Default value", exception, false, false);
                return defaultValue;
            }
        }
        return defaultValue;
    }

    /**
     * Get a {@link Double} value for a given key. returns control if key does not exist in any
     * Campaigns.
     * <p>
     * <p>
     * This function will return a {@link Double} value for a given key. This function will search for key in
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
     * @param key          is the key for which variation is to be requested
     * @param defaultValue is the {@link Double} value to be returned in case of missing key or invalid value.
     * @return a {@link Double} value corresponding to given key.
     */
    public static double getDoubleForKey(@NonNull String key, double defaultValue) {
        if (TextUtils.isEmpty(key)) {
            throw new NullPointerException("key cannot be empty");
        }
        Object data = getObjectForKey(key, defaultValue);
        if (data == null) {
            return defaultValue;
        } else {
            try {
                if (!(data instanceof Double)) {
                    VWOLog.w(VWOLog.DATA_LOGS, String.format(Locale.ENGLISH, "Casting %s to %s.", data.getClass().getName(), double.class.getName()), false);
                }
                return Double.parseDouble(String.valueOf(data));
            } catch (ClassCastException | NumberFormatException exception) {
                VWOLog.e(VWOLog.DATA_LOGS, String.format(Locale.ENGLISH, "Cannot cast %s to %s. Returning Default value.", data.getClass().getName(), double.class.getName()), exception, false, false);
            } catch (Exception exception) {
                VWOLog.e(VWOLog.DATA_LOGS, "Parse Exception. Returning Default value", exception, false, false);
            }
        }

        return defaultValue;
    }

    /**
     * Get an {@link Double} value for a given key and testKey. returns control if key does not exist in any
     * Campaigns.
     * <p>
     * <p>
     * This function will return an {@link Double} value for a given key and testKey. This function will search for key in
     * all the currently active campaigns and match the selected campaign testKey with input testKey.
     * </p>
     *
     * @param testKey      is the testKey of the campaign which is used along with key to fetch the variation.
     * @param key          is the key for which variation is to be requested
     * @param defaultValue is the {@link Double} value to be returned in case of missing key or invalid value.
     * @return an {@link Double} value corresponding to given key.
     */
    public static double getDoubleForKey(@NonNull String testKey, @NonNull String key, double defaultValue) {
        if (TextUtils.isEmpty(key)) {
            throw new NullPointerException("key cannot be empty");
        }
        Object data = getObjectForKey(testKey, key, defaultValue);
        if (data == null) {
            return defaultValue;
        } else {
            try {
                if (!(data instanceof Double)) {
                    VWOLog.w(VWOLog.DATA_LOGS, String.format(Locale.ENGLISH, "Casting %s to %s.", data.getClass().getName(), double.class.getName()), false);
                }
                return Double.parseDouble(String.valueOf(data));
            } catch (ClassCastException | NumberFormatException exception) {
                VWOLog.e(VWOLog.DATA_LOGS, String.format(Locale.ENGLISH, "Cannot cast %s to %s. Returning Default value.", data.getClass().getName(), double.class.getName()), exception, false, false);
            } catch (Exception exception) {
                VWOLog.e(VWOLog.DATA_LOGS, "Parse Exception. Returning Default value", exception, false, false);
            }
        }

        return defaultValue;
    }

    /**
     * Get a {@link Boolean} value for a given key. returns control if key does not exist in any
     * Campaigns.
     * <p>
     * <p>
     * This function will return a {@link Boolean} value for a given key. This function will search for key in
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
     * @param key          is the key for which variation is to be requested
     * @param defaultValue is the {@link Boolean} value to be returned in case of missing key or invalid value.
     * @return a {@link Boolean} value corresponding to given key.
     */
    public static boolean getBooleanForKey(@NonNull String key, boolean defaultValue) {
        if (TextUtils.isEmpty(key)) {
            throw new NullPointerException("key cannot be null or empty");
        }
        Object data = getObjectForKey(key, defaultValue);
        if (data == null) {
            return defaultValue;
        } else {
            try {
                if (!(data instanceof Boolean)) {
                    VWOLog.w(VWOLog.DATA_LOGS, String.format(Locale.ENGLISH, "Casting %s to %s.", data.getClass().getName(), double.class.getName()), false);
                }
                return Boolean.parseBoolean(String.valueOf(data));
            } catch (ClassCastException exception) {
                VWOLog.e(VWOLog.DATA_LOGS, String.format(Locale.ENGLISH, "Cannot cast %s to %s. Returning Default value.", data.getClass().getName(), boolean.class.getName()), exception, false, false);
            } catch (Exception exception) {
                VWOLog.e(VWOLog.DATA_LOGS, "Parse Exception. Returning Default value", exception, false, false);
            }
        }
        return defaultValue;
    }

    /**
     * Get an {@link Boolean} value for a given key and testKey. returns control if key does not exist in any
     * Campaigns.
     * <p>
     * <p>
     * This function will return an {@link Boolean} value for a given key and testKey. This function will search for key in
     * all the currently active campaigns and match the selected campaign testKey with input testKey.
     * </p>
     *
     * @param testKey      is the testKey of the campaign which is used along with key to fetch the variation.
     * @param key          is the key for which variation is to be requested
     * @param defaultValue is the {@link Boolean} value to be returned in case of missing key or invalid value.
     * @return an {@link Boolean} value corresponding to given key.
     */
    public static boolean getBooleanForKey(@NonNull String testKey, @NonNull String key, boolean defaultValue) {
        if (TextUtils.isEmpty(key)) {
            throw new NullPointerException("key cannot be null or empty");
        }
        Object data = getObjectForKey(testKey, key, defaultValue);
        if (data == null) {
            return defaultValue;
        } else {
            try {
                if (!(data instanceof Boolean)) {
                    VWOLog.w(VWOLog.DATA_LOGS, String.format(Locale.ENGLISH, "Casting %s to %s.", data.getClass().getName(), double.class.getName()), false);
                }
                return Boolean.parseBoolean(String.valueOf(data));
            } catch (ClassCastException exception) {
                VWOLog.e(VWOLog.DATA_LOGS, String.format(Locale.ENGLISH, "Cannot cast %s to %s. Returning Default value.", data.getClass().getName(), boolean.class.getName()), exception, false, false);
            } catch (Exception exception) {
                VWOLog.e(VWOLog.DATA_LOGS, "Parse Exception. Returning Default value", exception, false, false);
            }
        }
        return defaultValue;
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
     * @param key          is the key for which variation is to be requested
     * @param defaultValue is the default value to be returned if key is not found in any of the campaigns.
     * @return an {@link Object} corresponding to given key.
     * @deprecated use {@link VWO#getObjectForKey(String, Object)} instead
     */
    @Deprecated
    @Nullable
    public static Object getVariationForKey(@NonNull String key, @Nullable Object defaultValue) {
        return getObjectForKey(key, defaultValue);
    }

    /**
     * Get variation for a given key and testKey. returns control if key does not exist in any
     * Campaigns.
     * <p>
     * <p>
     * This function will return a variation for a given key and testKey. This function will search for key in
     * all the currently active campaigns and match the selected campaign testKey with input testKey.
     * </p>
     *
     * @param testKey      is the testKey of the campaign which is used along with key to fetch the variation.
     * @param key          is the key for which variation is to be requested
     * @param defaultValue is the {@link Boolean} value to be returned in case of missing key or invalid value.
     * @return an {@link Object} corresponding to given key.
     * @deprecated use {@link VWO#getObjectForKey(String, Object)} instead
     */
    @Deprecated
    @Nullable
    public static Object getVariationForKey(@NonNull String testKey, @NonNull String key, @Nullable Object defaultValue) {
        return getObjectForKey(testKey, key, defaultValue);
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
     * @param key          is the key for which variation is to be requested
     * @param defaultValue is the default value to be returned if key is not found in any of the campaigns.
     * @return an {@link Object} corresponding to given key.
     */
    public static Object getObjectForKey(@NonNull String key, @Nullable Object defaultValue) {
        if (TextUtils.isEmpty(key)) {
            throw new NullPointerException("key cannot be null or empty");
        }
        Object data = null;
        String message = "";
        synchronized (lock) {
            if (sSharedInstance != null) {
                if (sSharedInstance.mVWOStartState >= STARTED) {
                    if (sSharedInstance.isEditMode()) {
                        data = sSharedInstance.getVwoSocket().getVariationForKey(key);
                    } else {
                        data = sSharedInstance.getVwoData().getVariationForKey(key);
                    }
                } else if (sSharedInstance.mVWOStartState == OPTED_OUT) {
                    message = "User opted out.";
                } else if (sSharedInstance.mVWOStartState == FAILED) {
                    message = "SDK failed to Initialize.";
                } else {
                    message = "SDK is initializing";
                }
            } else {
                message = "SDK is not initialized";
            }
        }
        if (data == null) {
            VWOLog.w(VWOLog.DATA_LOGS, String.format(Locale.ENGLISH, "No variation found for key: \"%s\"\nReason: %s, returning default value", key, !TextUtils.isEmpty(message) ? message : "Key does not exist"), false);
            return defaultValue;
        } else {
            return data;
        }
    }

    /**
     * Get variation for a given key and testKey. returns control if key does not exist in any
     * Campaigns.
     * <p>
     * <p>
     * This function will return a variation for a given key and testKey. This function will search for key in
     * all the currently active campaigns and match the selected campaign testKey with input testKey.
     * </p>
     *
     * @param testKey      is the testKey of the campaign which is used along with key to fetch the variation.
     * @param key          is the key for which variation is to be requested
     * @param defaultValue is the default value to be returned if key is not found in any of the campaigns.
     * @return an {@link Object} corresponding to given key.
     */
    public static Object getObjectForKey(@NonNull String testKey, @NonNull String key, @Nullable Object defaultValue) {
        if (TextUtils.isEmpty(key)) {
            throw new NullPointerException("key cannot be null or empty");
        }
        Object data = null;
        String message = "";
        synchronized (lock) {
            if (sSharedInstance != null) {
                if (sSharedInstance.mVWOStartState >= STARTED) {
                    if (sSharedInstance.isEditMode()) {
                        data = sSharedInstance.getVwoSocket().getVariationForKey(key);
                    } else {
                        data = sSharedInstance.getVwoData().getVariationForKey(testKey, key);
                    }
                } else if (sSharedInstance.mVWOStartState == OPTED_OUT) {
                    message = "User opted out.";
                } else if (sSharedInstance.mVWOStartState == FAILED) {
                    message = "SDK failed to Initialize.";
                } else {
                    message = "SDK is initializing";
                }
            } else {
                message = "SDK is not initialized";
            }
        }
        if (data == null) {
            VWOLog.w(VWOLog.DATA_LOGS, String.format(Locale.ENGLISH, "No variation found for key: \"%s\"\nReason: %s, returning default value", key, !TextUtils.isEmpty(message) ? message : "Key does not exist"), false);
            return defaultValue;
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
                if (sSharedInstance.mVWOStartState >= STARTED) {
                    if (sSharedInstance.isEditMode()) {
                        sSharedInstance.getVwoSocket().triggerGoal(goalIdentifier);
                    } else {
                        sSharedInstance.mVWOData.saveGoal(goalIdentifier, null);
                    }
                } else if (sSharedInstance.mVWOStartState == OPTED_OUT) {
                    VWOLog.e(VWOLog.DATA_LOGS, "Conversion not tracked. User opted out.", true, false);
                } else if (sSharedInstance.mVWOStartState == FAILED) {
                    VWOLog.e(VWOLog.DATA_LOGS, "Conversion not tracked. SDK Failed to Initialize", true, false);
                }
            } else {
                VWOLog.e(VWOLog.NETWORK_LOGS, "SDK not initialized completely", false, false);
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
                if (sSharedInstance.mVWOStartState >= STARTED) {

                    if (sSharedInstance.isEditMode()) {
                        sSharedInstance.getVwoSocket().triggerGoal(goalIdentifier);
                    } else {
                        // Check if already present in persisting data
                        sSharedInstance.mVWOData.saveGoal(goalIdentifier, value);
                    }
                } else if (sSharedInstance.mVWOStartState == OPTED_OUT) {
                    VWOLog.e(VWOLog.DATA_LOGS, "Conversion not tracked. User opted out.", true, false);
                } else if (sSharedInstance.mVWOStartState == FAILED) {
                    VWOLog.e(VWOLog.DATA_LOGS, "Conversion not tracked. SDK Failed to Initialize", true, false);
                }
            } else {
                VWOLog.e(VWOLog.NETWORK_LOGS, "SDK not initialized completely", false, false);
            }
        }
    }

    /**
     * This function can be used to push the custom dimensions to the VWO servers.
     * This will help to filter out the reports on VWO web-app.
     *
     * @param customDimensionKey   is the key for the custom dimension
     * @param customDimensionValue is the value corresponding to the given customDimensionKey
     */
    public static void pushCustomDimension(@NonNull String customDimensionKey, @NonNull String customDimensionValue) {
        if (TextUtils.isEmpty(customDimensionKey)) {
            throw new IllegalArgumentException("customDimensionKey cannot be null or empty");
        }
        if (TextUtils.isEmpty(customDimensionValue)) {
            throw new IllegalArgumentException("customDimensionValue cannot be null or empty");
        }
        synchronized (lock) {
            if (sSharedInstance != null) {
                if (sSharedInstance.mVWOStartState >= STARTED) {
                    sSharedInstance.mVWOData.sendCustomDimension(customDimensionKey, customDimensionValue);
                } else if (sSharedInstance.mVWOStartState == OPTED_OUT) {
                    VWOLog.e(VWOLog.DATA_LOGS, "Custom Dimension not sent. User opted out.", true, false);
                } else if (sSharedInstance.mVWOStartState == FAILED) {
                    VWOLog.e(VWOLog.DATA_LOGS, "Custom Dimension not sent. SDK Failed to Initialize", true, false);
                } else {
                    VWOLog.e(VWOLog.DATA_LOGS, "Custom Dimension not sent. SDK is initializing", true, false);
                }
            } else {
                VWOLog.e(VWOLog.NETWORK_LOGS, "SDK not initialized completely", false, false);
            }
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
        if (sSharedInstance == null || sSharedInstance.getConfig() == null) {
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
        VWOLog.i(VWOLog.INITIALIZATION_LOGS, String.format("**** Starting VWO version: %s Build: %s ****", version(), versionCode()), false);
        assert getConfig() != null;
        if (getConfig().isOptOut()) {
            this.mVWOStartState = OPTED_OUT;
            new VWOPreference(getCurrentContext()).clear();
            VWOLog.w(VWOLog.INITIALIZATION_LOGS, "Ignoring initalization, User opted out.", false);
            onLoadSuccess();
            return true;
        }
        if (!VWOUtils.checkForInternetPermissions(mContext)) {
            String errMsg = "Internet permission not added to Manifest. Please add" + "\n\n<uses-permission android:name=\"android.permission.INTERNET\"/> \n\npermission to your app Manifest file.";
            VWOLog.e(VWOLog.INITIALIZATION_LOGS, errMsg, false, false);
            onLoadFailure("Missing internet permission");
            return false;
        } else if (!isAndroidSDKSupported()) {
            String errMsg = "Minimum SDK version required is 14";
            VWOLog.e(VWOLog.INITIALIZATION_LOGS, errMsg, false, false);
            onLoadFailure(errMsg);
            return false;
        } else if (!VWOUtils.isValidVwoAppKey(vwoConfig.getApiKey())) {
            VWOLog.e(VWOLog.INITIALIZATION_LOGS, "Invalid API Key: " + vwoConfig.getAppKey(), false, false);
            onLoadFailure("Invalid API Key.");
            return false;
        } else if (this.mVWOStartState == STARTING) {
            VWOLog.w(VWOLog.INITIALIZATION_LOGS, "VWO is already initializing.", true);
            return true;
        } else if (this.mVWOStartState >= STARTED) {
            VWOLog.w(VWOLog.INITIALIZATION_LOGS, "VWO is already initialized.", true);
            onLoadSuccess();
            return true;
        } else {
            // Everything is good so far
            this.mVWOStartState = STARTING;
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

            VWOLog.e(VWOLog.DATA_LOGS, "Empty data downloaded : " + data, true, true);
            onDownloadError(new Exception(), "Empty data downloaded");
        } else {

            // If server isn't ready mock your own data
            // Will cause no side effects even in release build.
            StringBuilder sb = new StringBuilder(Objects.requireNonNull(data));
            MockServerResponse.replaceWithApiV3EuModificationResponse(sb);
            data = sb.toString();

            ServerResponse.log("response sent by the server ...");
            ServerResponse.log(data);

            ServerResponse serverResponse = new ServerResponse(data);

            if (serverResponse.isNewStandardApi()) {

                EUManager.putCollectionPrefix(sSharedInstance, data);
                parseAndContinueWithNewStandardData(serverResponse);
            } else if (serverResponse.isLegacyApi()) {

                parseAndContinueWithLegacyData(data);
            } else {

                // ------
                // SHOULD NEVER HAPPEN, UNLESS SOMETHING WRONG WITH SERVER
                // ------
                onDownloadError(new Exception("Invalid response from SERVER."), "Unable to parse data");
            }
        }
    }

    private void continueWithInitialization(JSONArray data) throws JSONException {
        VWOLog.i(VWOLog.INITIALIZATION_LOGS, data.toString(4), true);
        mVWOData.parseData(data);
        mVWOLocalData.saveData(data);
        mVWOStartState = STARTED;
        onLoadSuccess();
    }

    private void parseAndContinueWithNewStandardData(ServerResponse response) {
        ServerResponse.log("Parsing the new V3 data, EU Version.");
        try {
            JSONObject jsonObject = response.getV3Json();
            JSONArray campaignsJsonArray = convertV3ToLegacyMEGStructure(jsonObject);
            continueWithInitialization(campaignsJsonArray);
        } catch (JSONException exception) {
            onDownloadError(exception, "Unable to parse data");
        }
    }

    private JSONArray convertV3ToLegacyMEGStructure(JSONObject jsonObject) throws JSONException {

        // extract campaigns to make it as same as previous structure
        JSONArray campaignsJsonArray = jsonObject.getJSONArray(CampaignGroupMapper.KEY_CAMPAIGNS);

        // build legacy structure for parsing the campaigns and MEG too
        // because the code structure isn't as straight forward
        // the previous structure is last item in the campaigns array is the data for MEG
        // using this format ensures backward compatibility, will not have any adverse effects
        // because the campaign parsing logic is not as straight forward as we may think.
        JSONObject meg = new JSONObject();
        meg.put("type", CampaignGroupMapper.KEY_GROUPS);

        boolean hasGroups = jsonObject.has(CampaignGroupMapper.KEY_GROUPS);
        if (hasGroups) {
            meg.put(CampaignGroupMapper.KEY_GROUPS, jsonObject.getJSONObject(CampaignGroupMapper.KEY_GROUPS));
        }

        boolean hasCampaignGroups = jsonObject.has(CampaignGroupMapper.KEY_CAMPAIGN_GROUPS);
        if (hasCampaignGroups) {
            meg.put(CampaignGroupMapper.KEY_CAMPAIGN_GROUPS, jsonObject.getJSONObject(CampaignGroupMapper.KEY_CAMPAIGN_GROUPS));
        }

        if (hasGroups || hasCampaignGroups) {
            campaignsJsonArray.put(meg);
        }

        return campaignsJsonArray;
    }

    private void parseAndContinueWithLegacyData(String data) {
        ServerResponse.log("Parsing the old V2 data, NON-EU Version.");
        try {
            JSONArray jsonArray = new JSONArray(data);
            continueWithInitialization(jsonArray);
        } catch (JSONException exception) {
            onDownloadError(exception, "Unable to parse data");
        }
    }

    @Override
    public void onDownloadError(@Nullable Exception exception, @Nullable String message) {
        if (exception != null && exception instanceof JSONException) {
            VWOLog.e(VWOLog.DATA_LOGS, exception, true, false);
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
            mVWOStartState = STARTED;
            VWOLog.w(VWOLog.INITIALIZATION_LOGS, "Failed to fetch data serving cached data.", false);
            onLoadSuccess();
        } else {
            onLoadFailure(message);
        }
    }

    @Override
    public void onPreviewEnabled() {
        mIsEditMode = true;
    }

    @Override
    public void onPreviewDisabled() {
        mIsEditMode = false;
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
        this.mVWOUrlBuilder = new VWOUrlBuilder(sSharedInstance);
        this.mVWOData = new VWOData(sSharedInstance);
        this.mVWOPreference = new VWOPreference(getCurrentContext());

        // Initialize message queues
        this.messageQueue = VWOMessageQueue.getInstance(getCurrentContext(), MESSAGE_QUEUE_NAME);
        this.failureQueue = VWOMessageQueue.getInstance(getCurrentContext(), FAILURE_QUEUE_NAME);

        VWODownloader.initializeMessageQueue(sSharedInstance);
        VWODownloader.initializeFailureQueue(sSharedInstance);

        initializePreviewMode();
    }

    /**
     * Initialize socket to use VWO in preview mode.
     * Cases in which socket is not initialized:
     * <ul>
     * <li>socket.io dependency is not added to gradle</li>
     * <li>{@link }</li>
     * </ul>
     */
    private void initializePreviewMode() {
        if (VWOUtils.checkIfClassExists("io.socket.client.Socket") && vwoConfig.isPreviewEnabled()) {
            this.mVWOSocket = new VWOSocket(this, vwoConfig.getAppKey());
            setPreviewGesture();
            if (VWOUtils.isApplicationDebuggable(getCurrentContext())) {
                mVWOSocket.init();
            }
        } else {
            VWOLog.e(VWOLog.INITIALIZATION_LOGS, "You need to add following dependency\n" + "\t\tcompile 'io.socket:socket.io-client:1.0.0\n" + "to your build.gradle file in order to use VWO's preview mode.", false, false);
        }
    }

    private void setPreviewGesture() {
        SensorManager sensorManager = (SensorManager) getCurrentContext().getSystemService(Context.SENSOR_SERVICE);

        ShakeDetector shakeDetector = new ShakeDetector(new ShakeDetector.Listener() {
            @Override
            public void hearShake() {
                mVWOSocket.init();
            }
        });

        shakeDetector.setSensitivity(ShakeDetector.SENSITIVITY_HARD);
        shakeDetector.start(sensorManager);
    }

    private void onLoadSuccess() {
        if (vwoConfig.getStatusListener() != null) {
            new Handler(getCurrentContext().getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    if (vwoConfig.isEnableBenchmarking()) {
                        TimeTracker.updateTracking(TimeTracker.KEY_AFTER_API_INIT_DURATION);
                        TimeTracker.updateTracking(TimeTracker.KEY_TOTAL_INIT_DURATION);
                    }
                    vwoConfig.getStatusListener().onVWOLoaded();
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

    @NonNull
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

    @Nullable
    public VWOConfig getConfig() {
        return this.vwoConfig;
    }

    private void onLoadFailure(final String reason) {
        this.mVWOStartState = FAILED;
        if (vwoConfig.getStatusListener() != null) {
            new Handler(getCurrentContext().getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    vwoConfig.getStatusListener().onVWOLoadFailure(reason);
                }
            });
        }
    }

    void setConfig(VWOConfig config) {
        this.vwoConfig = config;
    }

    @Connection.State
    public int getState() {
        return this.mVWOStartState;
    }

    public VWOMessageQueue getMessageQueue() {
        return this.messageQueue;
    }

    public VWOMessageQueue getFailureQueue() {
        return this.failureQueue;
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