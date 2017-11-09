package com.vwo.sampleapp.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by aman on 10/08/17.
 */

public class SharedPreferencesHelper {
    private static final String SHARED_PREFS_FILE = "sample_app_prefs";
    private static final String API_KEY = "api_key";
    private static String VWOapiKey;

    private static SharedPreferences mSharedPreference;

    /**
     * Gets shared prefs.
     *
     * @param mContext the m context
     * @return the shared prefs
     */
    public static SharedPreferences getSharedPrefs(Context mContext) {

        if (mSharedPreference == null) {
            mSharedPreference = mContext.getSharedPreferences(mContext.getPackageName(), Activity.MODE_PRIVATE);
        }

        return mSharedPreference;
    }

    /**
     * Gets shared prefs.
     *
     * @param mContext the m context
     * @return the shared prefs
     */
    public static SharedPreferences getVWOSharedPrefs(Context mContext) {
        return mContext.getSharedPreferences("VWO_shared_prefs", Activity.MODE_PRIVATE);
    }

    /**
     * Sets Api key.
     *
     * @param apiKey the application apiKey
     */
    public static void setApiKey(String apiKey, Context context) {
        VWOapiKey = apiKey;
        if(mSharedPreference == null) {
            getSharedPrefs(context);
        }
        mSharedPreference.edit().putString(API_KEY, apiKey).apply();
    }

    /**
     * Gets Api key.
     *
     */
    public static String getApiKey(Context context) {
        if(mSharedPreference == null) {
            getSharedPrefs(context);
        }
        return mSharedPreference.getString(API_KEY, null);
    }

    public static void removeApiKey(Context context) {
        if(mSharedPreference == null) {
            getSharedPrefs(context);
        }
        mSharedPreference.edit().remove(API_KEY).apply();
    }

    public static void clearData(Context context) {
//        removeApiKey(context);
        getVWOSharedPrefs(context).edit().clear().apply();
    }

}
