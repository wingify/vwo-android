package com.vwo.mobile.events;

import static com.vwo.mobile.BuildConfig.CHINA_DACDN_URL;
import static com.vwo.mobile.BuildConfig.DACDN_URL;
import static com.vwo.mobile.utils.VWOUrlBuilder.DACDN_URL_SCHEME;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.vwo.mobile.VWO;
import com.vwo.mobile.VWOConfig;
import com.vwo.mobile.constants.AppConstants;
import com.vwo.mobile.models.NonData360DimensionPostData;
import com.vwo.mobile.utils.VWOLog;
import com.vwo.mobile.utils.VWOUtils;
import com.vwo.mobile.v3.EUManager;

import org.json.JSONException;

import java.util.HashMap;

/**
 * To be used when custom dimensions with Hashmap needs to be sent and EventArch is disabled.
 */
public class CustomDimensPostEvent extends PostEvent {

    private final String PATH_DACDN_CUSTOM_DIMENSION = "mobile-app/push";
    private final String GOAL_ACCOUNT_ID = "account_id";
    private final String UUID = "u";
    private final String SESSION = "s";
    private final String SID = "sId";
    private final String GOAL_RANDOM = "random";

    public CustomDimensPostEvent(VWO vwo) {
        super(vwo);
    }

    public String getBody(@NonNull HashMap<String, Object> dimensions) {
        try {
            NonData360DimensionPostData data = new NonData360DimensionPostData(dimensions);
            return data.toJson().toString();
        } catch (JSONException exception) {
            //Will never happen
            return "{}";
        }
    }

    @Override
    String getEventName() {
        throw new RuntimeException("Unexpected use of class.");
    }

    @Override
    public String getUrl() {
        VWOConfig config = vwo.getConfig();
        if (config == null) return "";

        String deviceUuid = VWOUtils.getDeviceUUID(vwo.getVwoPreference());
        String accountId = config.getAccountId();
        int session = vwo.getVwoPreference().getInt(AppConstants.DEVICE_SESSION, 0);

        Uri.Builder uriBuilder = new Uri.Builder().scheme(DACDN_URL_SCHEME)
                .authority(this.vwo.getConfig().getIsChinaCDN() ? CHINA_DACDN_URL : DACDN_URL)
                .appendEncodedPath(EUManager.getEuAwarePath(vwo, PATH_DACDN_CUSTOM_DIMENSION))
                .appendQueryParameter(GOAL_ACCOUNT_ID, accountId)
                .appendQueryParameter(UUID, deviceUuid)
                .appendQueryParameter(SESSION, String.valueOf(session))
                .appendQueryParameter(SID, String.valueOf(System.currentTimeMillis() / 1000))
                .appendQueryParameter(GOAL_RANDOM, String.valueOf(VWOUtils.getRandomNumber()));

        String url = uriBuilder.build().toString();
        VWOLog.d("Url", "getCustomDimensionPostEvent Url=" + url, false);
        return uriBuilder.build().toString();
    }
}
