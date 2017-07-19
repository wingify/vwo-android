package com.vwo.mobile.utils;

import com.vwo.mobile.BuildConfig;
import com.vwo.mobile.VWO;
import com.vwo.mobile.constants.AppConstants;
import com.vwo.mobile.data.VWOPersistData;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Locale;

/**
 * Created by abhishek on 16/09/15 at 10:18 PM.
 */
public class VWOUrlBuilder {
    private static final String DACDN_URL = BuildConfig.DACDN_URL;
    private static final String DACDN_FETCH_URL_WITH_K = DACDN_URL + "mobile?a=%s&v=%s&i=%s&dt=%s&os=%s&r=%f&k=%s";
    private static final String DACDN_FETCH_URL_WITHOUT_K = DACDN_URL + "mobile?a=%s&v=%s&i=%s&dt=%s&os=%s&r=%f";
    public static final String DACDN_GOAL = DACDN_URL + "c.gif";
    public static final String DACDN_CAMPAIGN = DACDN_URL + "l.gif";


    private final VWO vwo;

    public VWOUrlBuilder(VWO vwo) {
        this.vwo = vwo;
    }

    public String getDownloadUrl() {
        String sdkVersion = add(BuildConfig.VERSION_NAME);
        String accountId = vwo.getConfig().getAccountId();
        String appKey = vwo.getConfig().getAppKey();
        String deviceType = "android";
        String currentDeviceSystemVersion = VWOUtils.androidVersion();
        String k = add(vwo.getVwoPreference().getString(VWOPersistData.CAMPAIGN_LIST));
        double randomNo = VWOUtils.getRandomNumber();

        String url;
        if (k.equals("")) {
            url = String.format(Locale.ENGLISH, DACDN_FETCH_URL_WITHOUT_K, accountId, sdkVersion, appKey, deviceType, currentDeviceSystemVersion, randomNo);
        } else {
            url = String.format(Locale.ENGLISH, DACDN_FETCH_URL_WITH_K, accountId, sdkVersion, appKey, deviceType, currentDeviceSystemVersion, randomNo, k);
        }

        VWOLog.v(VWOLog.URL_LOGS, "Campaign download url : " + url);

        return url;
    }

    private String add(String data) {
        if (data == null) {
            return "";
        }
        try {
            return URLEncoder.encode(data, "UTF-8");
        } catch (UnsupportedEncodingException exception) {
            VWOLog.e(VWOLog.URL_LOGS, "Exception generation url", exception, true, true);
            return "";
        }
    }

    public String getCampaignUrl(long experimentId, int variationId) {

        String deviceUuid = VWOUtils.getDeviceUUID(vwo);

        String accountId = vwo.getConfig().getAccountId();
        String uuid = add(deviceUuid);
        String url = DACDN_CAMPAIGN + "?experiment_id=%d" +
                "&account_id=%s" +
                "&combination=%d" +
                "&u=%s" +
                "&s=%d" +
                "&random=%f";


        int session = vwo.getVwoPreference().getInt(AppConstants.DEVICE_SESSION, 0);

        url = String.format(Locale.ENGLISH, url, experimentId, accountId, variationId, uuid, session, VWOUtils.getRandomNumber());
        String extraData = add(getExtraData());
        url += "&ed=" + extraData;
        VWOLog.v(VWOLog.URL_LOGS, "Campaign url: " + url);
        return url;
    }

    public String getGoalUrl(long experimentId, int variationId, int goalId) {
        String accountId = vwo.getConfig().getAccountId();
        String deviceUuid = VWOUtils.getDeviceUUID(vwo);

        String uuid = add(deviceUuid);
        String url = DACDN_GOAL + "?experiment_id=%d" +
                "&account_id=%s" +
                "&combination=%d" +
                "&u=%s" +
                "&s=%d" +
                "&random=%f" +
                "&goal_id=%d";

        int session = vwo.getVwoPreference().getInt(AppConstants.DEVICE_SESSION, 0);

        url = String.format(Locale.ENGLISH, url, experimentId, accountId, variationId, uuid, session, VWOUtils.getRandomNumber(), goalId);

        String extraData = add(getExtraData());
        url += "&ed=" + extraData;
        VWOLog.v(VWOLog.URL_LOGS, "Goal URL: " + url);
        return url;
    }

    public String getGoalUrl(long experimentId, int variationId, int goalId, float revenue) {
        String url = getGoalUrl(experimentId, variationId, goalId);
        url += "&r=" + revenue;

        return url;
    }

    private String getExtraData() {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("lt", System.currentTimeMillis() / 1000);
            jsonObject.put("v", VWOUtils.getVwoSdkVersion());
            jsonObject.put("ai", vwo.getConfig().getAppKey());
            jsonObject.put("av", VWOUtils.applicationVersion(vwo));
            jsonObject.put("dt", "android");
            jsonObject.put("os", VWOUtils.androidVersion());

            return jsonObject.toString();
        } catch (JSONException exception) {
            VWOLog.e(VWOLog.URL_LOGS, "Exception parsing json object: \n" + jsonObject.toString(),
                    exception, true, true);
        }
        return "";
    }


}
