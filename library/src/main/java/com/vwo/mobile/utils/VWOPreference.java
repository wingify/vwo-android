package com.vwo.mobile.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.text.TextUtils;

import com.vwo.mobile.constants.AppConstants;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;


public class VWOPreference {
    private final SharedPreferences preferences;
    private static final String PART_OF_CAMPAIGNS = "partOfCampaigns";
    private static final String TRACKED_GOALS = "trackedGoals";

    public VWOPreference(Context context) {
        preferences = context.getSharedPreferences(AppConstants.VWO_PREFERENCE_FILE, Context.MODE_PRIVATE);
    }

    // Getters

    /**
     * Get int value from SharedPreferences at 'key'. If key not found, return 'defaultValue'
     *
     * @param key          SharedPreferences key
     * @param defaultValue int value returned if key was not found
     * @return int value at 'key' or 'defaultValue' if key not found
     */
    public int getInt(String key, int defaultValue) {
        return preferences.getInt(key, defaultValue);
    }

    /**
     * Get String value from SharedPreferences at 'key'. If key not found, return ""
     *
     * @param key SharedPreferences key
     * @return String value at 'key' or "" (empty String) if key not found
     */
    public String getString(String key) {
        return preferences.getString(key, "");
    }

    /**
     * Get parsed ArrayList of String from SharedPreferences at 'key'
     *
     * @param key SharedPreferences key
     * @return ArrayList of String
     */
    private ArrayList<String> getListString(String key) {
        return new ArrayList<>(Arrays.asList(TextUtils.split(preferences.getString(key, ""), "‚‗‚")));
    }

    /**
     * Get boolean value from SharedPreferences at 'key'. If key not found, return 'defaultValue'
     *
     * @param key          SharedPreferences key
     * @param defaultValue boolean value returned if key was not found
     * @return boolean value at 'key' or 'defaultValue' if key not found
     */
    public boolean getBoolean(String key, boolean defaultValue) {
        return preferences.getBoolean(key, defaultValue);
    }

    // Put methods

    /**
     * Put int value into SharedPreferences with 'key' and save
     *
     * @param key   SharedPreferences key
     * @param value int value to be added
     */
    public void putInt(String key, int value) {
        preferences.edit().putInt(key, value).apply();
    }

    /**
     * Put String value into SharedPreferences with 'key' and save
     *
     * @param key   SharedPreferences key
     * @param value String value to be added
     */
    public void putString(String key, String value) {
        preferences.edit().putString(key, value).apply();
    }

    /**
     * Put ArrayList of String into SharedPreferences with 'key' and save
     *
     * @param key        SharedPreferences key
     * @param stringList ArrayList of String to be added
     */
    private void putListString(String key, ArrayList<String> stringList) {
        synchronized (preferences) {
            String[] myStringList = stringList.toArray(new String[stringList.size()]);
            preferences.edit().putString(key, TextUtils.join("‚‗‚", myStringList)).apply();
        }
    }

    /**
     * Put boolean value into SharedPreferences with 'key' and save
     *
     * @param key   SharedPreferences key
     * @param value boolean value to be added
     */
    public void putBoolean(String key, boolean value) {
        preferences.edit().putBoolean(key, value).apply();
    }


    /**
     * Remove SharedPreferences item with 'key'
     *
     * @param key SharedPreferences key
     */
    public void remove(String key) {
        preferences.edit().remove(key).apply();
    }


    /**
     * Clear SharedPreferences (remove everything)
     */
    public void clear() {
        preferences.edit().clear().apply();
    }


    public void setPartOfCampaign(String campaignId) {
        ArrayList<String> campaigns = getListString(PART_OF_CAMPAIGNS);
        if(!campaigns.contains(campaignId)) {
            campaigns.add(campaignId);
            putListString(PART_OF_CAMPAIGNS, campaigns);
        }

    }

    /**
     * Check if user is already a part of campaign or not.
     *
     * @param campaignId is the campaign identifier
     * @return true if user is already a part of campaign
     */
    public boolean isPartOfCampaign(String campaignId) {
        ArrayList<String> campaigns = getListString(PART_OF_CAMPAIGNS);
        return campaigns.size() > 0 && campaigns.contains(campaignId);
    }

    /**
     * check if goal is already sent to server or not
     *
     * @param goal is the goal identifier
     * @return {@link Boolean}
     */
    public boolean isGoalTracked(String goal) {
        ArrayList<String> goals = getListString(TRACKED_GOALS);
        return goals.size() > 0 && goals.contains(goal);
    }
}
