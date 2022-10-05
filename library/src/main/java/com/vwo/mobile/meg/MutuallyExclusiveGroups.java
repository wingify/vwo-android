package com.vwo.mobile.meg;

import android.text.TextUtils;
import android.util.Log;

import com.vwo.mobile.VWO;
import com.vwo.mobile.hash.MurmurHash;
import com.vwo.mobile.utils.VWOLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class MutuallyExclusiveGroups {

    private static final boolean IS_LOGS_SHOWN = true;

    private static final String TYPE_VISUAL_AB = "VISUAL_AB";

    public static final String ID_GROUP = "groupId";

    public static final String ID_CAMPAIGN = "campaignKey";

    private static final String TAG = MutuallyExclusiveGroups.class.getSimpleName();

    private final HashMap<String, Group> CAMPAIGN_GROUPS = new HashMap<>();

    private final String userId;

    private final HashMap<Integer, String> USER_CAMPAIGN = new HashMap<>();

    public MutuallyExclusiveGroups(String userId) {
        this.userId = userId;
    }

    public void addGroups(HashMap<String, Group> groupHashMap) {
        CAMPAIGN_GROUPS.clear();
        CAMPAIGN_GROUPS.putAll(groupHashMap);
    }

    public String getCampaign(HashMap<String, String> args, JSONArray campaignsData) {
        return calculateTheWinnerCampaign(args, campaignsData);
    }

    private String calculateTheWinnerCampaign(HashMap<String, String> args, JSONArray campaignsData) {

        if (args == null) {
            return null;
        }

        String groupId = args.get(ID_GROUP);
        String testKey = args.get(VWO.Constants.CAMPAIGN_TEST_KEY);
        String campaignId = getCampaignIdFromTestKey(campaignsData, testKey);

        if (groupId == null && campaignId == null) {

            // there must be at least one type of id
            // either GROUP or CAMPAIGN
            VWOLog.w(VWOLog.MEG_LOGS, "The groupId and campaignId ; both are null.", false);
            return null;
        }

        if ((groupId == null || TextUtils.isEmpty(groupId)) && TextUtils.isEmpty(campaignId)) {
            VWOLog.w(VWOLog.MEG_LOGS, "The groupId is null and campaignId is empty!", false);
            return null;
        }

        String campaign;
        String TestKey;
        String groupName;

        boolean groupIdIsNotPresentInArgs = (groupId == null || TextUtils.isEmpty(groupId));
        if (groupIdIsNotPresentInArgs) {

            VWOLog.i(VWOLog.MEG_LOGS, ID_GROUP + " was not found in the mapping so just picking the specific campaign [ " + campaignId + " ]", false);
            // if there is no sign of group we can simply use the campaign matching logic
            campaign = getCampaignFromCampaignId(userId, campaignId);
            VWOLog.i(VWOLog.MEG_LOGS, "Campaign selected from the mutually exclusive group is [ " + campaign + " ]", false);
            TestKey = getTestKeyFromCampaignId(campaignsData, campaign);
            VWOLog.i(VWOLog.MEG_LOGS, "Test-key of the campaign selected from the mutually exclusive group is [ " + TestKey + " ]", false);

            return TestKey;
        }

        VWOLog.d(VWOLog.MEG_LOGS, "Because there was groupId present, we are going to prioritize it and get a campaign from that group", false);
        try {
            groupName = getGroupNameFromGroupId(Integer.parseInt(groupId));
        } catch (NumberFormatException exception) {
            VWOLog.e(VWOLog.DATA_LOGS, exception, true, false);
            return null;
        }
        campaign = getCampaignFromSpecificGroup(groupName);
        VWOLog.i(VWOLog.MEG_LOGS, "Selected campaign from [ " + groupName + " ] is [ " + campaign + " ]", false);

        TestKey = getTestKeyFromCampaignId(campaignsData, campaign);
        VWOLog.i(VWOLog.MEG_LOGS, "Test-key of the campaign selected from the mutually exclusive group is [ " + TestKey + " ]", false);
        return TestKey;
    }

    private String getCampaignIdFromTestKey(JSONArray campaignsData, String testKey) {

        if (campaignsData == null) return null;

        if (campaignsData.length() == 0) return null;

        for (int i = 0; i < campaignsData.length(); i++) {
            try {
                JSONObject groupDataItem = campaignsData.getJSONObject(i);
                if (groupDataItem.optString(VWO.Constants.CAMPAIGN_TYPE, "").equals(TYPE_VISUAL_AB)) {
                    if (groupDataItem.optString(VWO.Constants.CAMPAIGN_TEST_KEY).equals(testKey)) {
                        return groupDataItem.optString(VWO.Constants.CAMPAIGN_ID);
                    }
                }
            } catch (JSONException exception) {
                VWOLog.e(VWOLog.DATA_LOGS, exception, true, false);
            }
        }
        return null;
    }

    private String getTestKeyFromCampaignId(JSONArray campaignsData, String campaignId) {

        if (campaignsData == null) return null;

        if (campaignsData.length() == 0) return null;

        for (int i = 0; i < campaignsData.length(); i++) {
            try {
                JSONObject groupDataItem = campaignsData.getJSONObject(i);
                if (groupDataItem.optString(VWO.Constants.CAMPAIGN_TYPE, "").equals(TYPE_VISUAL_AB)) {
                    if (groupDataItem.optString(VWO.Constants.CAMPAIGN_ID).equals(campaignId)) {
                        return groupDataItem.optString(VWO.Constants.CAMPAIGN_TEST_KEY);
                    }
                }
            } catch (JSONException exception) {
                VWOLog.e(VWOLog.DATA_LOGS, exception, true, false);
            }
        }
        return null;
    }

    private String getCampaignFromSpecificGroup(String groupName) {

        if (groupName == null) {
            // this should never happen unless the id of the group that doesn't exist is passed
            return null;
        }

        int murmurHash = getMurMurHash(userId);

        // If the campaign-user mapping is present in the App storage, get the decision from there. Otherwise, go to the next step
        if (USER_CAMPAIGN.containsKey(murmurHash)) return USER_CAMPAIGN.get(murmurHash);

        int normalizedValue = getNormalizedValue(murmurHash);
        VWOLog.d(VWOLog.MEG_LOGS, "Normalized value for user with userID -> " + userId + " is [ " + normalizedValue + " ] ", false);

        Group interestedGroup = CAMPAIGN_GROUPS.get(groupName);
        if (interestedGroup == null) return null;

        return interestedGroup.getCampaignForRespectiveWeight(normalizedValue);
    }

    private String getGroupNameFromGroupId(int groupId) {
        for (String key :
                CAMPAIGN_GROUPS.keySet()) {

            Group group = CAMPAIGN_GROUPS.get(key);

            if (group == null) return null;

            if (groupId == group.getId()) {
                // we found the group we have been searching for
                return key;
            }
        }
        return null;
    }

    private String getCampaignFromCampaignId(String userId, String campaign) {

        String campaignFoundInGroup = getCampaignIfPresent(campaign);
        if (campaignFoundInGroup == null) {

            VWOLog.i(VWOLog.MEG_LOGS, "The campaign key [ " + campaign + " ] is not present in any of the mutually exclusive groups.", false);
            return campaign;
        } else {

            VWOLog.i(VWOLog.MEG_LOGS, "Found campaign [ " + campaign + " ] in mutually exclusive group [ " + campaignFoundInGroup + " ] ", false);
        }

        // Generate a random number/murmurhash corresponding to the User ID
        int murmurHash = getMurMurHash(userId);

        // If the campaign-user mapping is present in the App storage, get the decision from there. Otherwise, go to the next step
        if (USER_CAMPAIGN.containsKey(murmurHash)) return USER_CAMPAIGN.get(murmurHash);

        int normalizedValue = getNormalizedValue(murmurHash);
        VWOLog.d(VWOLog.MEG_LOGS, "Normalized value for user with userID -> " + userId + " is [ " + normalizedValue + " ] ", false);

        // this group has our campaign
        Group interestedGroup = CAMPAIGN_GROUPS.get(campaignFoundInGroup);

        if (interestedGroup == null)
            return null; // basic null check because HashMap is being used

        String finalCampaign = interestedGroup.getCampaignForRespectiveWeight(normalizedValue);
        if (campaign.equals(finalCampaign)) {
            return finalCampaign;
        } else {
            VWOLog.i(VWOLog.MEG_LOGS, "Passed campaign : " + campaign + " does not match calculated campaign " + finalCampaign, false);
        }

        return null;
    }

    private String getCampaignIfPresent(String campaignKey) {
        for (String key :
                CAMPAIGN_GROUPS.keySet()) {

            Group group = CAMPAIGN_GROUPS.get(key);

            if (group == null) return null;

            String foundCampaign = group.getOnlyIfPresent(campaignKey);
            if (foundCampaign != null) {

                // we should return name of the group
                // the reason being we need to use the weightage of the campaigns later on
                return key;
            }
        }
        return null;
    }

    private int getNormalizedValue(int murmurHash) {
        int max = 100; // our normalized data ranges from { 0 to 100 }
        double ratio = murmurHash / Math.pow(2, 31);
        double multipliedValue = (max * ratio) + 1;
        int value = Math.abs((int) Math.floor(multipliedValue));
        return value;
    }

    private int getMurMurHash(String userId) {
        int hash = Math.abs(MurmurHash.hash32(userId));
        return hash;
    }

    public static void log(String message) {
        if (IS_LOGS_SHOWN) {
            Log.i(TAG, message);
        }
    }

}
