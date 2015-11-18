package com.vwo.mobile.data;

import com.vwo.mobile.Vwo;
import com.vwo.mobile.constants.AppConstants;
import com.vwo.mobile.utils.VwoPreference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by abhishek on 24/09/15 at 1:28 PM.
 */
public class VwoPersistData {

    public final static String CAMPAIGN_ID = "campaignId";
    public static final String VARIATION_ID = "variationId";
    public static final String GOALS = "goals";
    public static final String CAMPAIGN_KEY = "campaign_";
    public static final String CAMPAIGN_LIST = "campaignList";

    private long mCampaignId;
    private int mVariationId;
    private ArrayList<Integer> mGoals;

    public VwoPersistData(long campaignId, int variationId) {
        mCampaignId = campaignId;
        mVariationId = variationId;
        mGoals = new ArrayList<>();

    }

    public VwoPersistData(JSONObject data) {
        try {
            mCampaignId = data.getLong(CAMPAIGN_ID);
            setGoalsFromJsonArray(data.getJSONArray(GOALS));
            mVariationId = data.getInt(VARIATION_ID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONObject getPersistCampaignAsJsonObject() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(CAMPAIGN_ID, mCampaignId);
        jsonObject.put(VARIATION_ID, mVariationId);
        jsonObject.put(GOALS, getGoalsAsJsonArray());

        return jsonObject;
    }

    private JSONArray getGoalsAsJsonArray() {
        JSONArray jsonArray = new JSONArray();
        for (int goalId : mGoals) {
            jsonArray.put(goalId);
        }

        return jsonArray;
    }

    private void setGoalsFromJsonArray(JSONArray goalData) {
        mGoals = new ArrayList<>();
        for (int i = 0; i < goalData.length(); i++) {
            try {
                mGoals.add(goalData.getInt(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void addGoal(int goalId) {
        if (mGoals == null) {
            mGoals = new ArrayList<>();
        }
        mGoals.add(goalId);
    }

    public boolean isGoalExists(int goalId) {
        if (mGoals == null) {
            return false;
        }

        for (int goal : mGoals) {
            if (goal == goalId) {
                return true;
            }
        }

        return false;
    }

    public void saveCampaign(VwoPreference sharedPreference) {

        String campaignKey = CAMPAIGN_KEY + mCampaignId;


        try {
            sharedPreference.putString(campaignKey, getPersistCampaignAsJsonObject().toString());

            String campaignList = sharedPreference.getString(CAMPAIGN_LIST);
            JSONObject jsonObject;
            if (campaignList != null && !campaignList.equals("")) {
                jsonObject = new JSONObject(campaignList);
            } else {
                jsonObject = new JSONObject();
            }

            jsonObject.put(String.valueOf(mCampaignId), mVariationId);

            sharedPreference.putString(CAMPAIGN_LIST, jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    public static void addToQueue(VwoPreference sharedPreference, String url) {
        ArrayList<String> urls = sharedPreference.getListString(VwoData.VWO_QUEUE);
        urls.add(url);
        sharedPreference.putListString(VwoData.VWO_QUEUE, urls);

    }

    public static boolean isExistingCampaign(Vwo vwo, String campaignKey) {
        String jsonAsString = vwo.getVwoPreference().getString(campaignKey);
        // Already part of campaign. Just add to campaigns list
        return jsonAsString != null && !jsonAsString.equals("");
    }

    public static void updateReturningUser(Vwo vwo) {

        vwo.getVwoPreference().putBoolean(AppConstants.IS_RETURNING_USER, true);

    }

    public static boolean isReturningUser(Vwo vwo) {
        return vwo.getVwoPreference().getBoolean(AppConstants.IS_RETURNING_USER, false);
    }


}
