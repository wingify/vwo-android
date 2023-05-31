package com.vwo.mobile.data;


import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.vwo.mobile.VWO;
import com.vwo.mobile.VWOConfig;
import com.vwo.mobile.events.TrackGoalPostEvent;
import com.vwo.mobile.events.TrackUserPostEvent;
import com.vwo.mobile.events.VisitorSyncEvent;
import com.vwo.mobile.models.Campaign;
import com.vwo.mobile.models.CampaignEntry;
import com.vwo.mobile.models.CustomDimensionEntry;
import com.vwo.mobile.models.Goal;
import com.vwo.mobile.models.GoalEntry;
import com.vwo.mobile.models.Variation;
import com.vwo.mobile.segmentation.SegmentUtils;
import com.vwo.mobile.utils.VWOLog;
import com.vwo.mobile.utils.VWOUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Aman on Fri 16:06.
 */
public class VWOData {
    public static final String CAMPAIGN_RUNNING = "RUNNING";
    public static final String CAMPAIGN_EXCLUDED = "EXCLUDED";
    public static final String TYPE_GROUPS = "groups";

    private ArrayList<Campaign> mCampaigns;
    private Map<String, Campaign> mVariations;
    private Map<String, Variation> mCampaignKeyVariationMap;
    private ArrayList<Campaign> mUntrackedCampaigns;
    private VWO mVWO;

    public VWOData(VWO vwo) {
        this.mVWO = vwo;
        mCampaigns = new ArrayList<>();
        mUntrackedCampaigns = new ArrayList<>();
    }

    public void parseData(JSONArray data) {
        for (int i = 0; i < data.length(); i++) {
            try {
                if (data.getJSONObject(i).optString(Campaign.TYPE).equals(TYPE_GROUPS)) {
                    continue;
                }
                switch (data.getJSONObject(i).optString(Campaign.STATUS)) {
                    case CAMPAIGN_RUNNING:
                        // Only saving campaign if it has a variation object
                        if (data.getJSONObject(i).has(Campaign.VARIATION)) {

                            Campaign tempCampaign = new Campaign(mVWO, data.getJSONObject(i));

                            if (VWOPersistData.isExistingCampaign(mVWO, VWOPersistData.CAMPAIGN_KEY + tempCampaign.getId())) {
                                // Already part of campaign. Just add to campaigns list
                                mCampaigns.add(tempCampaign);
                                VWOLog.w(VWOLog.CAMPAIGN_LOGS, "User already part of campaign \""
                                                + tempCampaign.getName() + "\"\nAnd variation \"" + tempCampaign.getVariation().getName() + "\"",
                                        true);
                            } else {
                                if (tempCampaign.shouldTrackUserAutomatically()) {
                                    evaluateAndMakeUserPartOfCampaign(tempCampaign);
                                } else {
                                    mUntrackedCampaigns.add(tempCampaign);
                                }
                            }
                        } else {
                            VWOLog.wtf(VWOLog.CAMPAIGN_LOGS, "Variation object missing: \n" + data.toString(), true);
                        }
                        break;
                    case CAMPAIGN_EXCLUDED:
                        int campaignId = data.getJSONObject(i).getInt("id");
                        VWOPersistData vwoPersistData = new VWOPersistData(campaignId, 0);
                        vwoPersistData.saveCampaign(mVWO.getVwoPreference());
                        break;
                    default:
                        String campaignName;
                        if (data.getJSONObject(i).has(Campaign.CAMPAIGN_NAME)) {
                            campaignName = data.getJSONObject(i).getString(Campaign.CAMPAIGN_NAME);
                        } else {
                            campaignName = data.getJSONObject(i).getString(Campaign.ID);
                        }
                        VWOLog.i(VWOLog.CAMPAIGN_LOGS, "Discarding Campaign \"" + campaignName + "\", because it is not running", true);
                        break;
                }

            } catch (JSONException exception) {
                VWOLog.e(VWOLog.CAMPAIGN_LOGS, "Unable to parse campaign data: \n" + data.toString(), exception, true, true);
            }
        }

        if (mCampaigns.size() > 0) {
            VWOPersistData.updateReturningUser(mVWO);
        }

        generateVariationHash();
    }

    /**
     * Returns the value corresponding to he key for a any variation.
     *
     * @param key is the identifier corresponding to which a value needs to be fetched.
     * @return the variation value for the given key
     */
    @Nullable
    public Object getVariationForKey(String key) {

        if (mVariations == null) {
            return null;
        }

        Object variation = null;

        // Check if user is accessing key for the campaign that user is already part of.
        if (mVariations.containsKey(key)) {
            Campaign campaign = mVariations.get(key);

            variation = campaign.getVariation().getKey(key);
        }

        // Check if key exists in campaigns that user is not part of.
        boolean foundAnyCampaign = false;
        List<Campaign> campaignsToBeRemoved = new ArrayList<>();
        for (Campaign campaign : mUntrackedCampaigns) {
            if (campaign.getVariation().hasKey(key)) {
                evaluateAndMakeUserPartOfCampaign(campaign);
                campaignsToBeRemoved.add(campaign);
                foundAnyCampaign = true;
            }
        }

        if (foundAnyCampaign) {
            mUntrackedCampaigns.removeAll(campaignsToBeRemoved);
            generateVariationHash();
            variation = getVariationForKey(key);
        }

        return variation;
    }

    /**
     * Returns the value corresponding to the testKey of the campaign and the key for a any variation.
     *
     * @param testKey is the identifier containing the value of testKey of campaign corresponding to which a value needs to be fetched.
     * @param key     is the identifier corresponding to which a value needs to be fetched.
     * @return the variation value for the given key
     */
    @Nullable
    public Object getVariationForKey(String testKey, String key) {

        if (mVariations == null) {
            return null;
        }

        Object variation = null;

        // Check if user is accessing key for the campaign that user is already part of.
        if (mVariations.containsKey(key)) {
            Campaign campaign = mVariations.get(key);
            variation = campaign.getVariation().getKey(key);
        }

        // Check if key exists in campaigns that user is not part of.
        boolean foundAnyCampaign = false;
        List<Campaign> campaignsToBeRemoved = new ArrayList<>();
        for (Campaign campaign : mUntrackedCampaigns) {
            if (campaign.getVariation().hasKey(key) && campaign.getTestKey().equals(testKey)) {
                evaluateAndMakeUserPartOfCampaign(campaign);
                campaignsToBeRemoved.add(campaign);
                foundAnyCampaign = true;
            }
        }

        if (foundAnyCampaign) {
            mUntrackedCampaigns.removeAll(campaignsToBeRemoved);
            generateVariationHash();
            variation = getVariationForKey(testKey, key);
        }

        return variation;
    }

    @Nullable
    public Variation getVariationForCampaign(String testKey) {
        if (mVariations == null) {
            return null;
        }

        Variation variation = null;

        // Check is user is accessing key for the campaign that user is already part of.
        if (mCampaignKeyVariationMap.containsKey(testKey)) {
            variation = mCampaignKeyVariationMap.get(testKey);
        }

        // Check if key exists in campaigns that user is not part of.
        boolean foundAnyCampaign = false;
        List<Campaign> campaignsToBeRemoved = new ArrayList<>();
        for (Campaign campaign : mUntrackedCampaigns) {
            if (campaign.getTestKey().equals(testKey)) {
                evaluateAndMakeUserPartOfCampaign(campaign);
                campaignsToBeRemoved.add(campaign);
                foundAnyCampaign = true;
            }
        }

        if (foundAnyCampaign) {
            mUntrackedCampaigns.removeAll(campaignsToBeRemoved);
            generateVariationHash();
            variation = getVariationForCampaign(testKey);
        }

        return variation;
    }

    private boolean evaluateAndMakeUserPartOfCampaign(Campaign campaign) {
        if (SegmentUtils.evaluateSegmentation(campaign)) {
            mCampaigns.add(campaign);

            VWOLog.v(VWOLog.CAMPAIGN_LOGS, "Campaign \"" + campaign.getName() + "\" is a new and valid campaign");
            VWOLog.v(VWOLog.CAMPAIGN_LOGS, "Making user part of campaign \"" + campaign.getId() + "\"\nand variation with id: "
                    + campaign.getVariation().getId());

            VWOPersistData vwoPersistData = new VWOPersistData(campaign.getId(), campaign.getVariation().getId());
            vwoPersistData.saveCampaign(mVWO.getVwoPreference());

            // Make user part of campaign and avoid duplication
            if (!mVWO.getVwoPreference().isPartOfCampaign(String.valueOf(campaign.getId()))) {
                mVWO.getVwoPreference().setPartOfCampaign(String.valueOf(campaign.getId()));
                CampaignEntry campaignEntry = getCampaignRequestEntry(campaign);
                mVWO.getMessageQueue().add(campaignEntry);
                Intent intent = new Intent();
                intent.putExtra(VWO.Constants.ARG_CAMPAIGN_ID, String.valueOf(campaign.getId()));
                intent.putExtra(VWO.Constants.ARG_CAMPAIGN_NAME, campaign.getName());
                intent.putExtra(VWO.Constants.ARG_VARIATION_ID, String.valueOf(campaign.getVariation().getId()));
                intent.putExtra(VWO.Constants.ARG_VARIATION_NAME, campaign.getVariation().getName());
                intent.setAction(VWO.Constants.NOTIFY_USER_TRACKING_STARTED);
                if (VWOUtils.checkIfClassExists("android.support.v4.content.LocalBroadcastManager") ||
                        VWOUtils.checkIfClassExists("androidx.localbroadcastmanager.content.LocalBroadcastManager")) {
                    LocalBroadcastManager.getInstance(mVWO.getCurrentContext()).sendBroadcast(intent);
                } else {
                    VWOLog.e(VWOLog.CAMPAIGN_LOGS, "Add following dependency to your build.gradle" +
                                    "\nimplementation 'com.android.support:support-core-utils:26.0.1'\n" +
                                    "\nimplementation 'androidx.legacy:legacy-support-core-utils:1.0.0'\n to receive broadcasts.",
                            false, false);
                }
                return true;
            }
        } else {
            VWOLog.i(VWOLog.CAMPAIGN_LOGS, "Segmentation Condition for Campaign \"" +
                    campaign.getId() + "\" not met", true);

        }

        return false;
    }

    private CampaignEntry getCampaignRequestEntry(Campaign campaign) {
        VWOConfig config = mVWO.getConfig();
        boolean isEventArchEnabled = false;
        if (config != null)
            isEventArchEnabled = config.isEventArchEnabled();

        int variationId = campaign.getVariation().getId();
        long campaignId = campaign.getId();

        String body = null;
        String campaignRecordUrl;

        if (isEventArchEnabled) {
            TrackUserPostEvent event = new TrackUserPostEvent(mVWO);
            body = event.getBody(campaignId, variationId);
            campaignRecordUrl = event.getUrl();
        } else {
            campaignRecordUrl = mVWO.getVwoUrlBuilder().getCampaignUrl(campaignId, variationId);
        }
        return new CampaignEntry(campaignRecordUrl, campaignId, variationId, body, isEventArchEnabled);
    }

    /**
     * Mark goal as achieved with revenue
     *
     * @param goalIdentifier is the goal id set on dashboard
     * @param value          is the revenue value
     */
    public void saveGoal(@NonNull String goalIdentifier, Double value) {
        for (Campaign campaign : mCampaigns) {
            for (Goal goal : campaign.getGoals()) {
                if (goal.getIdentifier().equals(goalIdentifier)) {
                    String campaignData = mVWO.getVwoPreference().getString(VWOPersistData.CAMPAIGN_KEY + campaign.getId());
                    if (campaignData != null && !campaignData.equals("")) {
                        try {
                            JSONObject jsonObject = new JSONObject(campaignData);
                            VWOPersistData vwoPersistData = new VWOPersistData(jsonObject);
                            if (goal.isRecurringGoal() || !vwoPersistData.isGoalExists(goal.getId())) {
                                vwoPersistData.addGoal(goal.getId());
                                vwoPersistData.saveCampaign(mVWO.getVwoPreference());

                                GoalEntry goalEntry = getGoalRequestEntry(campaign, goal, value);
                                mVWO.getMessageQueue().add(goalEntry);
                            } else {
                                VWOLog.w(VWOLog.CAMPAIGN_LOGS, "Duplicate goal identifier: " + goalIdentifier, true);
                            }
                        } catch (Exception exception) {
                            VWOLog.w(VWOLog.CAMPAIGN_LOGS, "Unable to generate goal data object", exception, true);
                        }
                    }
                }
            }
        }
    }

    private GoalEntry getGoalRequestEntry(Campaign campaign, Goal goal, Double value) {
        VWOConfig config = mVWO.getConfig();
        boolean isEventArchEnabled = false;
        if (config != null)
            isEventArchEnabled = config.isEventArchEnabled();

        int variationId = campaign.getVariation().getId();
        long campaignId = campaign.getId();
        int goalId = goal.getId();

        String body = null;
        String goalUrl;

        if (isEventArchEnabled) {
            TrackGoalPostEvent event = new TrackGoalPostEvent(mVWO, goal);
            body = event.getBody(campaignId, value);
            goalUrl = event.getUrl();
        } else {
            if (value != null) {
                goalUrl = mVWO.getVwoUrlBuilder().getGoalUrl(campaignId, variationId, goalId, value);
            } else {
                goalUrl = mVWO.getVwoUrlBuilder().getGoalUrl(campaignId, variationId, goalId);
            }
        }
        return new GoalEntry(goalUrl, campaignId, variationId, goalId, body, isEventArchEnabled);
    }

    public void sendCustomDimension(String customDimensionKey, String customDimensionValue) {
        try {
            VWOConfig config = mVWO.getConfig();
            boolean isEventArchEnabled = false;
            if (config != null)
                isEventArchEnabled = config.isEventArchEnabled();

            String body = null;
            String customDimensionUrl;
            if (isEventArchEnabled) {
                VisitorSyncEvent event = new VisitorSyncEvent(mVWO);
                body = event.getBody(customDimensionKey, customDimensionValue);
                customDimensionUrl = event.getUrl();
            } else {
                customDimensionUrl = mVWO.getVwoUrlBuilder().getCustomDimensionUrl(customDimensionKey, customDimensionValue);
            }
            CustomDimensionEntry entry = new CustomDimensionEntry(customDimensionUrl,
                    customDimensionKey, customDimensionValue, body, isEventArchEnabled);

            mVWO.getMessageQueue().add(entry);
        } catch (Exception exception) {
            VWOLog.w(VWOLog.CAMPAIGN_LOGS, "Unable to send custom dimension to VWO server", exception, true);
        }
    }

    private void generateVariationHash() {
        if (mVariations == null) {
            mVariations = new HashMap<>();
            mCampaignKeyVariationMap = new HashMap<>();
        } else {
            mVariations.clear();
            mCampaignKeyVariationMap.clear();
        }

        for (Campaign campaign : mCampaigns) {
            mCampaignKeyVariationMap.put(campaign.getTestKey(), campaign.getVariation());
            Iterator<?> keys = campaign.getVariation().getServeObject().keys();
            while (keys.hasNext()) {
                String key = (String) keys.next();
                mVariations.put(key, campaign);
            }
        }
    }
}
