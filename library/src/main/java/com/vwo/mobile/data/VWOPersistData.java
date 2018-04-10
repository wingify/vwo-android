package com.vwo.mobile.data;

import com.vwo.mobile.VWO;
import com.vwo.mobile.constants.AppConstants;
import com.vwo.mobile.utils.VWOPreference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class VWOPersistData {

    /**
     * The constant CAMPAIGN_ID.
     */
    private final static String CAMPAIGN_ID = "campaignId";
    /**
     * The constant VARIATION_ID.
     */
    private static final String VARIATION_ID = "variationId";
    /**
     * The constant GOALS.
     */
    private static final String GOALS = "goals";
    /**
     * The constant CAMPAIGN_KEY.
     */
    static final String CAMPAIGN_KEY = "campaign_";
    /**
     * The constant CAMPAIGN_LIST.
     */
    public static final String CAMPAIGN_LIST = "campaignList";

    private long mCampaignId;
    private int mVariationId;
    private ArrayList<Integer> mGoals;

    /**
     * Instantiates a new Vwo persist data.
     *
     * @param campaignId  the campaign id
     * @param variationId the variation id
     */
    VWOPersistData(long campaignId, int variationId) {
        mCampaignId = campaignId;
        mVariationId = variationId;
        mGoals = new ArrayList<>();

    }

    /**
     * Instantiates a new Vwo persist data.
     *
     * @param data the data
     */
    VWOPersistData(JSONObject data) {
        try {
            mCampaignId = data.getLong(CAMPAIGN_ID);
            setGoalsFromJsonArray(data.getJSONArray(GOALS));
            mVariationId = data.getInt(VARIATION_ID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets persist campaign as json object.
     *
     * @return the persist campaign as json object
     * @throws JSONException the json exception
     */
    private JSONObject getPersistCampaignAsJsonObject() throws JSONException {
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

    /**
     * Add goal.
     *
     * @param goalId the goal id
     */
    void addGoal(int goalId) {
        if (mGoals == null) {
            mGoals = new ArrayList<>();
        }
        mGoals.add(goalId);
    }

    /**
     * Is goal exists boolean.
     *
     * @param goalId the goal id
     * @return the boolean
     */
    boolean isGoalExists(int goalId) {
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

    /**
     * Save campaign.
     *
     * @param sharedPreference the shared preference
     */
    void saveCampaign(VWOPreference sharedPreference) {
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


    /**
     * Is existing campaign boolean.
     *
     * @param vwo         the vwo
     * @param campaignKey the campaign key
     * @return the boolean
     */
    static boolean isExistingCampaign(VWO vwo, String campaignKey) {
        String jsonAsString = vwo.getVwoPreference().getString(campaignKey);
        // Already part of campaign. Just add to campaigns list
        return jsonAsString != null && !jsonAsString.equals("");
    }

    /**
     * Mark user as returning user after user has opened app for the first time.
     *
     * @param vwo {@link VWO instance}
     */
    static void updateReturningUser(VWO vwo) {
        vwo.getVwoPreference().putBoolean(AppConstants.IS_RETURNING_USER, true);
    }

    /**
     * Check if the user is starting the app for the first time or not.
     * Returns true is starting app for first time otherwise false
     *
     * @param vwo {@link VWO instance}
     * @return the boolean
     */
    public static boolean isReturningUser(VWO vwo) {
        return vwo.getVwoPreference().getBoolean(AppConstants.IS_RETURNING_USER, false);
    }
}
