package com.vwo.mobile.meg;

import android.text.TextUtils;

import com.vwo.mobile.VWO;
import com.vwo.mobile.data.VWOLocalData;
import com.vwo.mobile.hash.MurmurHash;
import com.vwo.mobile.models.Campaign;
import com.vwo.mobile.segmentation.SegmentUtils;
import com.vwo.mobile.utils.VWOLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class MutuallyExclusiveGroups {

    private static final String TYPE_VISUAL_AB = "VISUAL_AB";

    public static final String ID_GROUP = "groupId";

    public static final String ID_CAMPAIGN = "campaignKey";

    private static final String TAG = MutuallyExclusiveGroups.class.getSimpleName();

    private final HashMap<String, Group> CAMPAIGN_GROUPS = new HashMap<>();

    private final String userId;

    private final HashMap<Integer, String> USER_CAMPAIGN = new HashMap<>();

    private final VWO sSharedInstance;

    public MutuallyExclusiveGroups(VWO sSharedInstance, String userId) {
        this.sSharedInstance = sSharedInstance;
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

            log(ID_GROUP + " was not found in the mapping so just picking the specific campaign [ " + campaignId + " ]");
            // if there is no sign of group we can simply use the campaign matching logic
            campaign = getCampaignFromCampaignId(userId, campaignId);
            log("Campaign selected from the mutually exclusive group is [ " + campaign + " ]");
            TestKey = getTestKeyFromCampaignId(campaignsData, campaign);
            log("Test-key of the campaign selected from the mutually exclusive group is [ " + TestKey + " ]");

            return TestKey;
        }

        log("Because there was groupId present, we are going to prioritize it and get a campaign from that group");
        try {
            groupName = getGroupNameFromGroupId(Integer.parseInt(groupId));
        } catch (NumberFormatException exception) {
            VWOLog.e(VWOLog.DATA_LOGS, exception, true, false);
            return null;
        }
        campaign = getCampaignFromSpecificGroup(groupName);
        log("Selected campaign from [ " + groupName + " ] is [ " + campaign + " ]");

        TestKey = getTestKeyFromCampaignId(campaignsData, campaign);
        log("Test-key of the campaign selected from the mutually exclusive group is [ " + TestKey + " ]");
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
        log("Normalized value for user with userID -> " + userId + " is [ " + normalizedValue + " ] ");

        Group interestedGroup = CAMPAIGN_GROUPS.get(groupName);
        if (interestedGroup == null) return null;

        // evaluate all the priority campaigns
        log("----------- { BEGIN } Priority Campaign Evaluation -----------");
        ArrayList<String> priorityCampaignsInGroup = interestedGroup.getPriorityCampaigns();
        if (priorityCampaignsInGroup.isEmpty()) log("> there are 0 priority campaigns");
        for (int i = 0; i < priorityCampaignsInGroup.size(); i++) {
            String priorityCampaign = priorityCampaignsInGroup.get(i);
            log("now evaluating priority campaign ( p ) @ index " + i + " -> " + priorityCampaign);
            PriorityQualificationWinnerResult result = isQualifiedAsWinner(priorityCampaign, true);
            if (result.isQualified()) {
                log("found a winner campaign from the priority campaign list -> " + priorityCampaign);
                return priorityCampaign;
            }
        }
        log("----------- { END } Priority Campaign Evaluation -----------");

        log("none of the priority campaigns are qualified as winners, next will try to check for weighted campaign.");
        return interestedGroup.getCampaignForRespectiveWeight(normalizedValue);
    }

    private PriorityQualificationWinnerResult isQualifiedAsWinner(String priorityCampaignId, boolean isGroupPassedByUser) {

        boolean priorityIsNull = Objects.equals(Group.VALUE_INVALID_PRIORITY_CAMPAIGN, priorityCampaignId);
        if (priorityIsNull) {
            log("the passed priority campaign id is null, will not qualify.");

            PriorityQualificationWinnerResult result = new PriorityQualificationWinnerResult();
            result.setQualified(false);
            result.setGroupInPriority(isGroupPassedByUser);
            result.setPriorityCampaignFound(false);
            return result;
        }

        try {

            VWOLocalData vwoData = new VWOLocalData(sSharedInstance);
            if (!vwoData.isLocalDataPresent()) {
                log("INCONSISTENT STATE detected, local data for VWO is not present.");

                PriorityQualificationWinnerResult result = new PriorityQualificationWinnerResult();
                result.setQualified(false);
                result.setGroupInPriority(isGroupPassedByUser);
                result.setPriorityCampaignFound(false);
                return result;
            }

            log("> evaluating each campaign from campaign list to check if they are qualified");
            JSONArray data = vwoData.getData();
            boolean isPriorityCampaignFoundLocally = false;
            for (int i = 0; i < data.length(); ++i) {
                JSONObject jsonCampaign = data.getJSONObject(i);
                Campaign campaign = new Campaign(sSharedInstance, jsonCampaign);

                boolean isPriorityCampaignValid = isPriorityValid(campaign, priorityCampaignId);

                if (!isGroupPassedByUser && !isPriorityCampaignValid) {
                    // skip
                    log("will not evaluate -> " + campaign.getId() + " as it is redundant to do so");
                    continue;
                }

                if (isPriorityCampaignValid) {
                    // avoid assigning false once it is true because we want to know that
                    // if campaignId and priorityCampaignId matched at some point
                    isPriorityCampaignFoundLocally = true;
                }

                if (isSegmentationValid(campaign) && isVariationValid(campaign) && isPriorityCampaignValid) {
                    log("------------ QUALIFIED campaign found -> " + campaign.getName());
                    log("campaign ( name: " + campaign.getName() + ", id: " + campaign.getId() + " ) -> " + jsonCampaign);
                    log("-----------------------------------------------------------");

                    PriorityQualificationWinnerResult result = new PriorityQualificationWinnerResult();
                    result.setQualified(true);
                    result.setGroupInPriority(isGroupPassedByUser);
                    result.setPriorityCampaignFound(true);
                    return result;
                }

                log("------------ DID NOT QUALIFY -> " + campaign.getName());
                log("campaign ( name: " + campaign.getName() + ", id: " + campaign.getId() + " ) -> " + jsonCampaign);
                log("-----------------------------------------------------------");

                // at this point the campaign did not qualify so if no group was passed then we can stop this loop
                // this optimizes our runtime cost as { null } will be returned after loop cases are exhausted
                if (!isGroupPassedByUser) {
                    log("breaking loop @{i = " + i + "} as it will help us optimize by skipping " + (data.length() - i) + " more qualification checks.");
                    break;
                }

            }

            PriorityQualificationWinnerResult result = new PriorityQualificationWinnerResult();
            result.setQualified(false);
            result.setGroupInPriority(isGroupPassedByUser);
            result.setPriorityCampaignFound(isPriorityCampaignFoundLocally);
            return result;
        } catch (Exception ex) {

            log("an error occurred while selecting qualified winner -> " + ex.getMessage());

            PriorityQualificationWinnerResult result = new PriorityQualificationWinnerResult();
            result.setQualified(false);
            result.setGroupInPriority(isGroupPassedByUser);
            result.setPriorityCampaignFound(false);
            return result;
        }
    }

    private boolean isVariationValid(Campaign campaign) {
        boolean isVariationNotNull = campaign.getVariation() != null;
        boolean isVariationValid = (isVariationNotNull && campaign.getVariation().getId() > 0);
        if (isVariationValid) {
            log("VALID | variation id -> " + campaign.getVariation().getId());
        } else {
            if (isVariationNotNull) {
                log("INVALID | variation id -> " + campaign.getVariation().getId());
            }
        }
        return isVariationValid;
    }

    private boolean isSegmentationValid(Campaign campaign) {
        boolean isSegmentationValid = SegmentUtils.evaluateSegmentation(campaign);
        if (isSegmentationValid) log("VALID | segmentation checks");
        else log("INVALID | segmentation checks");
        return isSegmentationValid;
    }

    private boolean isPriorityValid(Campaign campaign, String priorityCampaignId) {
        boolean isSameAsPriority = (campaign.getId() == Long.parseLong(priorityCampaignId));
        if (isSameAsPriority) {
            log("VALID | campaignId -> " + campaign.getId() + " priorityCampaignId -> " + priorityCampaignId);
        } else {
            log("INVALID | campaignId -> " + campaign.getId() + " priorityCampaignId -> " + priorityCampaignId);
        }
        return isSameAsPriority;
    }

    private String getGroupNameFromGroupId(int groupId) {
        for (String key : CAMPAIGN_GROUPS.keySet()) {

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

            log("The campaign key [ " + campaign + " ] is not present in any of the mutually exclusive groups.");
            return campaign;
        } else {

            log("Found campaign [ " + campaign + " ] in mutually exclusive group [ " + campaignFoundInGroup + " ] ");
        }

        // Generate a random number/murmurhash corresponding to the User ID
        int murmurHash = getMurMurHash(userId);

        // If the campaign-user mapping is present in the App storage, get the decision from there. Otherwise, go to the next step
        if (USER_CAMPAIGN.containsKey(murmurHash)) return USER_CAMPAIGN.get(murmurHash);

        int normalizedValue = getNormalizedValue(murmurHash);
        log("Normalized value for user with userID -> " + userId + " is [ " + normalizedValue + " ] ");

        // this group has our campaign
        Group interestedGroup = CAMPAIGN_GROUPS.get(campaignFoundInGroup);

        if (interestedGroup == null) return null; // basic null check because HashMap is being used

        // check if this campaign is in priority list
        // if not found there's no point in evaluating the list
        if (interestedGroup.hasInPriority(campaign)) {

            log(campaign + " found in priority campaign list.");

            // evaluate priority campaigns
            // here the campaign is the priorityCampaign because we are targeting the specific campaign
            PriorityQualificationWinnerResult result = isQualifiedAsWinner(campaign, false);
            if (result.isQualified()) {
                log("winner campaign found from the priority campaign list -> " + campaign);
                return campaign;
            }

            // check if we found the related campaign and still unqualified
            if (result.isPriorityCampaignFound() && result.isNotQualified()) {
                log("priority campaign was found but was not qualified for winning, will simply return { null } from this point.");
                return null;
            }
        } else {

            log("priority campaigns does not have campaign -> " + campaign + ", skipping redundant checks for optimization.");
        }

        String finalCampaign = interestedGroup.getCampaignForRespectiveWeight(normalizedValue);
        if (campaign.equals(finalCampaign)) {
            return finalCampaign;
        } else {
            log("Passed campaign : " + campaign + " does not match calculated campaign " + finalCampaign);
        }

        return null;
    }

    private String getCampaignIfPresent(String campaignKey) {
        for (String key : CAMPAIGN_GROUPS.keySet()) {

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
        // disable logging of all MEG messages, we do not need it
        // VWOLog.i(VWOLog.MEG_LOGS, message, true);
    }

}
