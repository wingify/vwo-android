package com.vwo.mobile.meg;

import android.text.TextUtils;

import com.vwo.mobile.VWO;
import com.vwo.mobile.data.VWOLocalData;
import com.vwo.mobile.utils.VWOLog;
import com.vwo.mobile.utils.VWOPreference;
import com.vwo.mobile.utils.VWOUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by { Nabin Niroula } on 21/03/2023 for android-sdk.
 **/
public class MEGManager {

    private static final String EMPTY = "";

    // previously calculated winner campaigns for
    private static final String KEY_SAVED_ARRAY_OF_WINNER_CAMPAIGNS = "winner_mappings";

    private final VWO sSharedInstance;

    private final VWOLocalData mVWOLocalData;

    private final WinnerManager winnerManager;

    public MEGManager(VWO sSharedInstance) {
        this.sSharedInstance = sSharedInstance;
        this.mVWOLocalData = new VWOLocalData(sSharedInstance);
        winnerManager = new WinnerManager(sSharedInstance);
    }

    private void iLog(String message) {
        MutuallyExclusiveGroups.log(message);
    }

    public String getCampaign(String userId, HashMap<String, String> args) {

        iLog("trying to figure out MEG winner campaign.");

        if (userId == null || TextUtils.isEmpty(userId)) {
            iLog("User not passed for MEG, using the SDK's logic for userId generation.");
            // use the (default | random) user id
            VWOPreference vwoPreference = new VWOPreference(sSharedInstance.getCurrentContext());
            userId = VWOUtils.getDeviceUUID(vwoPreference);
        }

        WinnerManager.Response localResponse = winnerManager.getSavedDetailsFor(userId, args);
        if (localResponse.shouldServePreviousWinnerCampaign()) {
            // user doesn't exist, should continue processing
            String savedWinnerCampaign = localResponse.getWinnerCampaign();
            String servingNull;
            if (savedWinnerCampaign == null) {
                servingNull = "null";
            } else {
                servingNull = savedWinnerCampaign;
            }
            MutuallyExclusiveGroups.log("will serve campaign: " + servingNull + " for user: " + userId + " which is based on previous runs.");
            return savedWinnerCampaign;
        }

        MutuallyExclusiveGroups.log("response -> isQualified: " + localResponse.shouldServePreviousWinnerCampaign() + " ; isNewUser: " + localResponse.isNewUser() + " ; getWinnerCampaign: " + localResponse.getWinnerCampaign());

        JSONArray campaignsData = mVWOLocalData.getData();
        if (campaignsData == null || campaignsData.length() == 0) return null; // MEG data not found

        JSONObject megGroupsData = getMEGData(campaignsData);
        if (megGroupsData == null) return null; // MEG data not found

        HashMap<String, Group> mappedData = CampaignGroupMapper.createAndGetGroups(megGroupsData);

        MutuallyExclusiveGroups meg = new MutuallyExclusiveGroups(sSharedInstance, userId);
        meg.addGroups(mappedData);

        String winner = meg.getCampaign(args, campaignsData);
        winnerManager.save(userId, winner, args);
        return winner;
    }

    private JSONObject getMEGData(JSONArray campaignsData) {
        JSONObject megGroupsData = new JSONObject();

        // last item is always the MEG data
        int campaignDataLastIndex = (campaignsData.length() - 1);
        for (int i = campaignDataLastIndex; i >= 0; i--) {
            try {

                JSONObject groupDataItem = campaignsData.getJSONObject(i);

                if (!groupDataItem.has(VWO.Constants.CAMPAIGN_TYPE)) {
                    return null;
                }

                String cType = groupDataItem.optString(VWO.Constants.CAMPAIGN_TYPE, EMPTY);
                if (VWO.Constants.CAMPAIGN_GROUPS.equals(cType)) {
                    megGroupsData = groupDataItem;
                    break;
                }
            } catch (JSONException exception) {
                VWOLog.e(VWOLog.DATA_LOGS, exception, true, false);
            }
        }

        return megGroupsData;
    }

}
