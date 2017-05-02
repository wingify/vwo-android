package com.vwo.mobile.utils;

import com.vwo.mobile.Vwo;
import com.vwo.mobile.constants.AppConstants;
import com.vwo.mobile.constants.GlobalConstants;
import com.vwo.mobile.data.VwoPersistData;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Locale;
import java.util.logging.Logger;

/**
 * Created by abhishek on 16/09/15 at 10:18 PM.
 */
public class VwoUrlBuilder {
    private static final Logger LOGGER = VWOLogger.getLogger(VwoUrlBuilder.class.getCanonicalName());

    private static final String DACDN_URL = "https://dacdn.vwo.com/";
    private static final String DACDN_FETCH_URL_WITH_K = DACDN_URL + "mobile?a=%s&v=%s&i=%s&dt=%s&os=%s&r=%f&k=%s";
    private static final String DACDN_FETCH_URL_WITHOUT_K = DACDN_URL + "mobile?a=%s&v=%s&i=%s&dt=%s&os=%s&r=%f";
    public static final String DACDN_GOAL = DACDN_URL + "c.gif";
    public static final String DACDN_CAMPAIGN = DACDN_URL + "l.gif";


    private final Vwo vwo;

    public VwoUrlBuilder(Vwo vwo) {
        this.vwo = vwo;
    }

    public String getDownloadUrl() {
        String sdkVersion = add(GlobalConstants.SDK_VERSION);
        String accountId = vwo.getAccountId();
        String appKey = vwo.getAppKey();
        String deviceType = "android";
        String currentDeviceSystemVersion = VwoUtils.androidVersion();
        String k = add(vwo.getVwoPreference().getString(VwoPersistData.CAMPAIGN_LIST));
        double randomNo = VwoUtils.getRandomNumber();

        String url;
        if (k.equals("")) {
            url = String.format(Locale.ENGLISH, DACDN_FETCH_URL_WITHOUT_K, accountId, sdkVersion, appKey, deviceType, currentDeviceSystemVersion, randomNo);
        } else {
            url = String.format(Locale.ENGLISH, DACDN_FETCH_URL_WITH_K, accountId, sdkVersion, appKey, deviceType, currentDeviceSystemVersion, randomNo, k);
        }

        LOGGER.info("Url : " + url);

        return url;
    }

    private String add(String data) {
        if (data == null) {
            return "";
        }
        try {
            return URLEncoder.encode(data, "UTF-8");
        } catch (UnsupportedEncodingException exception) {
            LOGGER.throwing(VwoUrlBuilder.class.getSimpleName(), "add(String)", exception);
            return "";
        }
    }

    public String getCampaignUrl(long experimentId, int variationId) {

        String deviceUuid = VwoUtils.getDeviceUUID(vwo);

        String accountId = vwo.getAccountId();
        String uuid = add(deviceUuid);
        String url = DACDN_CAMPAIGN + "?experiment_id=%d" +
                "&account_id=%s" +
                "&combination=%d" +
                "&u=%s" +
                "&s=%d" +
                "&random=%f";


        int session = vwo.getVwoPreference().getInt(AppConstants.DEVICE_SESSION, 0);

        url = String.format(Locale.ENGLISH, url, experimentId, accountId, variationId, uuid, session, VwoUtils.getRandomNumber());
        String extraData = add(getExtraData());
        url += "&ed=" + extraData;
        LOGGER.info("URL: " + url);
        return url;
    }

    public String getGoalUrl(long experimentId, int variationId, int goalId) {
        String accountId = vwo.getAccountId();
        String deviceUuid = VwoUtils.getDeviceUUID(vwo);

        String uuid = add(deviceUuid);
        String url = DACDN_GOAL + "?experiment_id=%d" +
                "&account_id=%s" +
                "&combination=%d" +
                "&u=%s" +
                "&s=%d" +
                "&random=%f" +
                "&goal_id=%d";

        int session = vwo.getVwoPreference().getInt(AppConstants.DEVICE_SESSION, 0);

        url = String.format(Locale.ENGLISH, url, experimentId, accountId, variationId, uuid, session, VwoUtils.getRandomNumber(), goalId);

        String extraData = add(getExtraData());
        url += "&ed=" + extraData;
        LOGGER.info("URL: " + url);
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
            jsonObject.put("v", VwoUtils.getVwoSdkVersion());
            jsonObject.put("ai", vwo.getAppKey());
            jsonObject.put("av", VwoUtils.applicationVersion(vwo));
            jsonObject.put("dt", "android");
            jsonObject.put("os", VwoUtils.androidVersion());

            return jsonObject.toString();
        } catch (JSONException exception) {
            LOGGER.throwing(VwoUrlBuilder.class.getSimpleName(), "getExtraData()", exception);
        }
        return "";
    }


}
