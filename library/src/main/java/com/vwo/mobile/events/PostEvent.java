package com.vwo.mobile.events;

import static com.vwo.mobile.BuildConfig.CHINA_DACDN_URL;
import static com.vwo.mobile.BuildConfig.DACDN_URL;
import static com.vwo.mobile.utils.VWOUrlBuilder.DACDN_URL_SCHEME;
import static com.vwo.mobile.utils.VWOUrlBuilder.PATH_EVENTS;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.vwo.mobile.VWO;
import com.vwo.mobile.utils.VWOLog;
import com.vwo.mobile.utils.VWOUtils;
import com.vwo.mobile.v3.EUManager;

abstract class PostEvent {

    protected String EVENT_NAME_KEY = "en";
    protected String ACC_ID_KEY = "a";
    protected String APPLICATION_VERSION = "av";
    protected String API_KEY = "env";
    protected String EVENT_TIME_KEY = "eTime";
    protected String RANDOM_KEY = "random";

    protected String SDK_NAME = "vwo-android-sdk";

    protected long eventTime = System.currentTimeMillis();
    protected String deviceUuid;

    protected VWO vwo;

    public PostEvent(VWO vwo) {
        this.vwo = vwo;
        deviceUuid = VWOUtils.getDeviceUUID(vwo.getVwoPreference());
    }

    public String getUrl() {

        String accountId = vwo.getConfig().getAccountId();
        String applicationVersion = String.valueOf(VWOUtils.applicationVersionName(vwo.getCurrentContext()));
        Uri.Builder uriBuilder = new Uri.Builder()
                .scheme(DACDN_URL_SCHEME)
                .authority(getAuthority())
                .appendEncodedPath(getPathForEventArchitecture())
                .appendQueryParameter(EVENT_NAME_KEY, getEventName())
                .appendQueryParameter(ACC_ID_KEY, accountId)
                .appendQueryParameter(APPLICATION_VERSION, applicationVersion)
                .appendQueryParameter(API_KEY, getApiKey())
                .appendQueryParameter(EVENT_TIME_KEY, String.valueOf(eventTime))
                .appendQueryParameter(RANDOM_KEY, String.valueOf(VWOUtils.getRandomNumber()));

        String url = uriBuilder.build().toString();
        VWOLog.d("Url", "PostEvent Url=" + url, false);
        return url;
    }

    protected String getApiKey() {
        return vwo.getConfig().getApiKey();
    }

    @NonNull
    protected String getAuthority() {
        return vwo.getConfig().getIsChinaCDN() ? CHINA_DACDN_URL : DACDN_URL;
    }

    @NonNull
    protected String getPathForEventArchitecture() {
        return EUManager.getEuAwarePath(vwo, PATH_EVENTS);
    }

    abstract String getEventName();

    String getMessageId() {
        return deviceUuid + "-" + eventTime;
    }

    protected long getSessionId() {
        return eventTime / 1000;
    }

    String getVisitorId() {
        return deviceUuid;
    }
}