package com.vwo.mobile.v3;

import android.text.TextUtils;

import com.vwo.mobile.VWO;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by { Nabin Niroula } on 21/03/2023 for android-sdk.
 **/
public abstract class EUManager {

    private static final String COLLECTION_PREFIX = "collectionPrefix";

    private static String getCollectionPrefix(VWO vwo) {
        // will be empty if there is no collection prefix;
        // data can be uploaded to normal servers
        return vwo.getVwoPreference().getString(COLLECTION_PREFIX);
    }

    public static void putCollectionPrefix(VWO vwo, String json) {

        try {

            JSONObject jsonObject = new JSONObject(json);

            if (jsonObject.has(COLLECTION_PREFIX) && !TextUtils.isEmpty(jsonObject.getString(COLLECTION_PREFIX))) {

                // if there is collectionPrefix in the JSON received from the server save it
                String collectionPrefix = jsonObject.getString(COLLECTION_PREFIX);
                vwo.getVwoPreference().putString(COLLECTION_PREFIX, collectionPrefix);
            } else {

                // else just remove any stale collection prefix
                // because the new settings does not expect any collectionPrefix anyway
                // avoid any edge case bugs
                vwo.getVwoPreference().remove(COLLECTION_PREFIX);
            }

        } catch (JSONException ex) {

            // VWOLog.e(VWOLog.CONFIG_LOGS, "Could not ", ex, true);
        }
    }

    public static String getEuAwarePath(VWO vwo, String constant) {
        StringBuilder path = new StringBuilder();
        if (isEUClient(vwo)) {
            path.append(EUManager.getCollectionPrefix(vwo)).append('/').append(constant);
        } else {
            path.append(constant);
        }
        return path.toString();
    }

    private static boolean isEUClient(VWO vwo) {
        return !TextUtils.isEmpty(getCollectionPrefix(vwo));
    }

}
