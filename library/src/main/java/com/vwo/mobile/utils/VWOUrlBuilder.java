package com.vwo.mobile.utils;

import android.net.Uri;
import android.text.TextUtils;

import com.vwo.mobile.BuildConfig;
import com.vwo.mobile.VWO;
import com.vwo.mobile.constants.AppConstants;
import com.vwo.mobile.data.VWOPersistData;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Locale;

/**
 * Created by abhishek on 16/09/15 at 10:18 PM.
 */
public class VWOUrlBuilder {
    private static final String DACDN_URL = BuildConfig.DACDN_URL;
    private static final String DACDN_URL_SCHEME = BuildConfig.SCHEME;
    private static final String PATH_MOBILE = "mobile";
    private static final String PATH_DACDN_GOAL = "c.gif";
    private static final String PATH_DACDN_CAMPAIGN = "l.gif";

    private static final String VALUE_DEVICE_TYPE = "android";

    private static final String ACCOUNT_ID = "a";
    private static final String SDK_VERSION = "v";
    private static final String APP_KEY = "i";
    private static final String DEVICE_TYPE = "dt";
    private static final String DEVICE_SYSTEM_VERSION = "os";
    // To prevent cached results
    private static final String RANDOM_NUMBER = "rk";
    private static final String EXISTING_CAMPAIGN_LIST = "k";


    private static final String EXPERIMENT_ID = "experiment_id";
    private static final String GOAL_ACCOUNT_ID = "account_id";
    private static final String COMBINATION = "combination";
    private static final String UUID = "u";
    private static final String SESSION = "s";
    private static final String GOAL_RANDOM = "random";
    private static final String EXTRA_DATA = "ed";
    private static final String REVENUE = "r";
    private static final String GOAL_ID = "goal_id";


    private static final String EXTRA_TIME_IN_SECONDS = "lt";
    private static final String EXTRA_VERSION_CODE = "v";
    private static final String EXTRA_API_KEY = "ai";
    private static final String EXTRA_APPLICATION_VERSION = "av";
    private static final String EXTRA_DEVICE_TYPE = "dt";
    private static final String EXTRA_OPERATING_SYSTEM = "os";


    private final VWO vwo;

    public VWOUrlBuilder(VWO vwo) {
        this.vwo = vwo;
    }

    public String getDownloadUrl() {
        String sdkVersion = String.valueOf(VWO.versionCode());
        String accountId = vwo.getConfig().getAccountId();
        String appKey = vwo.getConfig().getAppKey();
        String currentDeviceSystemVersion = VWOUtils.androidVersion();
        String existingCampaignList = vwo.getVwoPreference().getString(VWOPersistData.CAMPAIGN_LIST);
        double randomNo = VWOUtils.getRandomNumber();

        Uri.Builder uriBuilder = new Uri.Builder().scheme(DACDN_URL_SCHEME)
                .authority(DACDN_URL)
                .appendEncodedPath(PATH_MOBILE)
                .appendQueryParameter(ACCOUNT_ID, accountId)
                .appendQueryParameter(SDK_VERSION, sdkVersion)
                .appendQueryParameter(APP_KEY, appKey)
                .appendQueryParameter(DEVICE_TYPE, VALUE_DEVICE_TYPE)
                .appendQueryParameter(DEVICE_SYSTEM_VERSION, currentDeviceSystemVersion)
                .appendQueryParameter(RANDOM_NUMBER, String.valueOf(randomNo));

        if (!TextUtils.isEmpty(existingCampaignList)) {
            uriBuilder.appendQueryParameter(EXISTING_CAMPAIGN_LIST, existingCampaignList);
        }
        String url = uriBuilder.build().toString();

        VWOLog.v(VWOLog.URL_LOGS, "Campaign download url : " + url);

        return url;
    }

    public String getCampaignUrl(long experimentId, int variationId) {

        String deviceUuid = VWOUtils.getDeviceUUID(vwo);

        String accountId = vwo.getConfig().getAccountId();

        int session = vwo.getVwoPreference().getInt(AppConstants.DEVICE_SESSION, 0);

        Uri.Builder uriBuilder = new Uri.Builder().scheme(DACDN_URL_SCHEME)
                .authority(DACDN_URL)
                .appendEncodedPath(PATH_DACDN_CAMPAIGN)
                .appendQueryParameter(EXPERIMENT_ID, String.valueOf(experimentId))
                .appendQueryParameter(GOAL_ACCOUNT_ID, accountId)
                .appendQueryParameter(COMBINATION, String.valueOf(variationId))
                .appendQueryParameter(UUID, deviceUuid)
                .appendQueryParameter(SESSION, String.valueOf(session))
                .appendQueryParameter(GOAL_RANDOM, String.valueOf(VWOUtils.getRandomNumber()))
                .appendQueryParameter(EXTRA_DATA, getExtraData());



        String url = uriBuilder.build().toString();
        VWOLog.v(VWOLog.URL_LOGS, "Campaign url: " + url);
        return url;
    }

    public String getGoalUrl(long experimentId, int variationId, int goalId) {
        String accountId = vwo.getConfig().getAccountId();
        String deviceUuid = VWOUtils.getDeviceUUID(vwo);

        int session = vwo.getVwoPreference().getInt(AppConstants.DEVICE_SESSION, 0);

        Uri.Builder uriBuilder = new Uri.Builder().scheme(DACDN_URL_SCHEME)
                .authority(DACDN_URL)
                .appendEncodedPath(PATH_DACDN_GOAL)
                .appendQueryParameter(EXPERIMENT_ID, String.valueOf(experimentId))
                .appendQueryParameter(GOAL_ACCOUNT_ID, accountId)
                .appendQueryParameter(COMBINATION, String.valueOf(variationId))
                .appendQueryParameter(UUID, deviceUuid)
                .appendQueryParameter(SESSION, String.valueOf(session))
                .appendQueryParameter(GOAL_RANDOM, String.valueOf(VWOUtils.getRandomNumber()))
                .appendQueryParameter(GOAL_ID, String.valueOf(goalId))
                .appendQueryParameter(EXTRA_DATA, getExtraData());



        String url = uriBuilder.build().toString();
        VWOLog.v(VWOLog.URL_LOGS, "Goal URL: " + url);
        return url;
    }

    public String getGoalUrl(long experimentId, int variationId, int goalId, float revenue) {

        return Uri.parse(getGoalUrl(experimentId, variationId, goalId))
                .buildUpon()
                .appendQueryParameter(REVENUE, String.valueOf(revenue))
                .build()
                .toString();
    }

    private String getExtraData() {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(EXTRA_TIME_IN_SECONDS, System.currentTimeMillis() / 1000);
            jsonObject.put(EXTRA_VERSION_CODE, VWO.versionCode());
            jsonObject.put(EXTRA_API_KEY, vwo.getConfig().getAppKey());
            jsonObject.put(EXTRA_APPLICATION_VERSION, VWOUtils.applicationVersion(vwo.getCurrentContext()));
            jsonObject.put(EXTRA_DEVICE_TYPE, VALUE_DEVICE_TYPE);
            jsonObject.put(EXTRA_OPERATING_SYSTEM, VWOUtils.androidVersion());

            return jsonObject.toString();
        } catch (JSONException exception) {
            VWOLog.e(VWOLog.URL_LOGS, "Exception parsing json object: \n" + jsonObject.toString(),
                    exception, true, true);
        }
        return "";
    }


}
