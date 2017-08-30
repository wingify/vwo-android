package com.vwo.mobile.data;


import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.vwo.mobile.VWO;
import com.vwo.mobile.models.Campaign;
import com.vwo.mobile.models.Goal;
import com.vwo.mobile.segmentation.CustomSegment;
import com.vwo.mobile.segmentation.LogicalOperator;
import com.vwo.mobile.segmentation.Segment;
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
import java.util.Stack;

/**
 * Created by abhishek on 17/09/15 at 10:10 PM.
 */
public class VWOData {
    public static final String CAMPAIGN_RUNNING = "RUNNING";
    public static final String CAMPAIGN_EXCLUDED = "EXCLUED";

    public static final String VWO_QUEUE = "VWO_QUEUE";
    private ArrayList<Campaign> mCampaigns;
    private Map<String, Campaign> mVariations;
    private ArrayList<Campaign> mUntrackedCampaigns;
    private VWO mVWO;

    public VWOData(VWO vwo) {
        this.mVWO = vwo;
        mCampaigns = new ArrayList<>();
        mUntrackedCampaigns = new ArrayList<>();
    }

    private static boolean evaluateSegmentation(VWO vwo, Campaign campaign) {

        if (campaign.getSegmentType().equals(Campaign.SEGMENT_CUSTOM)) {

            Stack<Object> stack = new Stack<>();
            for (Segment segment : campaign.getSegments()) {

                CustomSegment customSegment = (CustomSegment) segment;

                boolean currentValue = segment.evaluate(vwo);

                if (customSegment.getPreviousLogicalOperator() != null && customSegment.isLeftBracket()) {
                    stack.push(customSegment.getPreviousLogicalOperator());
                } else if (customSegment.getPreviousLogicalOperator() != null) {
                    boolean leftVariable = (boolean) stack.pop();
                    currentValue = customSegment.getPreviousLogicalOperator().evaluate(leftVariable, currentValue);
                }

                if (customSegment.isLeftBracket()) {
                    stack.push("(");
                }

                if (customSegment.isRightBracket()) {
                    stack.pop();
                    while ((stack.size() > 0) && !(stack.peek()).equals("(")) {
                        String random = stack.peek().toString();
                        LogicalOperator logicalOperator = LogicalOperator.fromString(random);
                        stack.pop();
                        boolean leftVariable = (Boolean) stack.pop();
                        currentValue = (logicalOperator != null) && logicalOperator.evaluate(leftVariable, currentValue);
                    }
                }

                stack.push(currentValue);
            }
            return (boolean) stack.pop();
        } else {
            return campaign.getSegments().get(0).evaluate(vwo);
        }
    }

    public void parseData(JSONArray data) {

        for (int i = 0; i < data.length(); i++) {
            try {
                if (data.getJSONObject(i).getString(Campaign.STATUS).equals(CAMPAIGN_RUNNING)) {

                    // Only saving campaign if it has a variation object
                    if (data.getJSONObject(i).has(Campaign.VARIATION)) {

                        Campaign tempCampaign = new Campaign(data.getJSONObject(i));

                        if (VWOPersistData.isExistingCampaign(mVWO, VWOPersistData.CAMPAIGN_KEY + tempCampaign.getId())) {
                            // Already part of campaign. Just add to campaigns list
                            mCampaigns.add(tempCampaign);
                            VWOLog.w(VWOLog.CAMPAIGN_LOGS, "User already part of campaign \""
                                    + tempCampaign.getName() + "\"\nAnd variation \"" + tempCampaign.getVariation().getName() +"\"",
                                    true);
                        } else {
                            if (tempCampaign.shouldTrackUserAutomatically()) {
                                evaluateAndMakeUserPartOfCampaign(tempCampaign);
                            } else {
                                mUntrackedCampaigns.add(tempCampaign);
                            }
                        }
                    }
                } else if (data.getJSONObject(i).getString(Campaign.STATUS).equals(CAMPAIGN_EXCLUDED)) {
                    int campaignId = data.getJSONObject(i).getInt("id");
                    VWOPersistData vwoPersistData = new VWOPersistData(campaignId, 0);
                    vwoPersistData.saveCampaign(mVWO.getVwoPreference());
                } else {
                    String campaignName;
                    if(data.getJSONObject(i).has(Campaign.CAMPAIGN_NAME)) {
                        campaignName = data.getJSONObject(i).getString(Campaign.CAMPAIGN_NAME);
                    } else {
                        campaignName = data.getJSONObject(i).getString(Campaign.ID);
                    }
                    VWOLog.i(VWOLog.CAMPAIGN_LOGS, "Discarding Campaign \"" + campaignName + "\", because it is not running", true);
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
     * @param key is the identifier corresponding to which a value needs to be fetched.
     *
     * @return the variation value for the given key
     */
    @Nullable
    public Object getVariationForKey(String key) {

        if (mVariations == null) {
            return null;
        }

        Object variation = null;

        // Check is user is accessing key for the campaign that user is already part of.
        if (mVariations.containsKey(key)) {
            Campaign campaign = mVariations.get(key);

            variation = campaign.getVariation().getKey(key);
        }

        // Check if key exists in campaigns that user is not part of.

        boolean foundAnyCampaign = false;
        List<Campaign> campaignsToBeRemoved = new ArrayList<>();
        for(Campaign campaign : mUntrackedCampaigns) {
            evaluateAndMakeUserPartOfCampaign(campaign);
            campaignsToBeRemoved.add(campaign);
            foundAnyCampaign = true;
        }

        if(foundAnyCampaign) {
            mUntrackedCampaigns.removeAll(campaignsToBeRemoved);
            generateVariationHash();
            variation = getVariationForKey(key);
        }

        return variation;
    }

    public Campaign getCampaignForKey(String key) {
        if (mVariations == null) {
            return null;
        }
        if (mVariations.containsKey(key)) {
            return mVariations.get(key);
        }
        return null;
    }

    private boolean evaluateAndMakeUserPartOfCampaign(Campaign campaign) {
        if (evaluateSegmentation(mVWO, campaign)) {
            mCampaigns.add(campaign);

            String campaignRecordUrl = mVWO.getVwoUrlBuilder().getCampaignUrl(campaign.getId(), campaign.getVariation().getId());
            VWOLog.v(VWOLog.CAMPAIGN_LOGS, "Campaign \"" + campaign.getName() + "\" is a new and valid campaign");
            VWOLog.v(VWOLog.CAMPAIGN_LOGS, "Making user part of campaign \"" + campaign.getId() + "\"\nand variation with id: "
                            + campaign.getVariation().getId());

            VWOPersistData vwoPersistData = new VWOPersistData(campaign.getId(), campaign.getVariation().getId());
            vwoPersistData.saveCampaign(mVWO.getVwoPreference());

            // Make user part of campaign and avoid duplication
            if (!mVWO.getVwoPreference().isPartOfCampaign(String.valueOf(campaign.getId()))) {
                mVWO.getVwoPreference().setPartOfCampaign(String.valueOf(campaign.getId()));
                VWOPersistData.addToQueue(mVWO.getVwoPreference(), campaignRecordUrl);
                Intent intent = new Intent();
                intent.putExtra(VWO.Constants.ARG_CAMPAIGN_ID, String.valueOf(campaign.getId()));
                intent.putExtra(VWO.Constants.ARG_CAMPAIGN_NAME, campaign.getName());
                intent.putExtra(VWO.Constants.ARG_VARIATION_ID, String.valueOf(campaign.getVariation().getId()));
                intent.putExtra(VWO.Constants.ARG_VARIATION_NAME, campaign.getVariation().getName());
                intent.setAction(VWO.Constants.NOTIFY_USER_TRACKING_STARTED);
                if(VWOUtils.checkIfClassExists("android.support.v4.content.LocalBroadcastManager")) {
                    LocalBroadcastManager.getInstance(mVWO.getCurrentContext()).sendBroadcast(intent);
                } else {
                    VWOLog.e(VWOLog.CAMPAIGN_LOGS, "Add following dependency to your build.gradle" +
                            "\ncompile 'com.android.support:support-core-utils:26.0.1'\n to receive broadcasts.",
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

    public void saveGoal(String goalIdentifier) {
        for (Campaign campaign : mCampaigns) {
            for (Goal goal : campaign.getGoals()) {
                if (goal.getIdentifier().equals(goalIdentifier)) {
                    String campaignData = mVWO.getVwoPreference().getString(VWOPersistData.CAMPAIGN_KEY + campaign.getId());
                    if (campaignData != null && !campaignData.equals("")) {
                        try {
                            JSONObject jsonObject = new JSONObject(campaignData);
                            VWOPersistData vwoPersistData = new VWOPersistData(jsonObject);
                            if (!vwoPersistData.isGoalExists(goal.getId())) {
                                vwoPersistData.addGoal(goal.getId());
                                vwoPersistData.saveCampaign(mVWO.getVwoPreference());

                                String goalUrl = mVWO.getVwoUrlBuilder().getGoalUrl(campaign.getId(), campaign.getVariation().getId(), goal.getId());
                                VWOPersistData.addToQueue(mVWO.getVwoPreference(), goalUrl);
                            } else {
                                VWOLog.w(VWOLog.CAMPAIGN_LOGS, "Duplicate goal identifier: " + goalIdentifier, true);
                            }
                        } catch (JSONException exception) {
                            VWOLog.w(VWOLog.CAMPAIGN_LOGS, "Unable to generate goal object", exception, true);
                        }

                    }
                } else {
                    Log.v(VWOLog.CAMPAIGN_LOGS, "Goal not matched");
                }
            }
        }
    }

    /**
     * Mark goal as achieved with revenue
     *
     * @param goalIdentifier is the goal id set on dashboard
     * @param value is the revenue value
     */
    public void saveGoal(@NonNull String goalIdentifier, double value) {
        for (Campaign campaign : mCampaigns) {
            for (Goal goal : campaign.getGoals()) {
                if (goal.getIdentifier().equals(goalIdentifier)) {
                    String campaignData = mVWO.getVwoPreference().getString(VWOPersistData.CAMPAIGN_KEY + campaign.getId());
                    if (campaignData != null && !campaignData.equals("")) {
                        try {
                            JSONObject jsonObject = new JSONObject(campaignData);
                            VWOPersistData vwoPersistData = new VWOPersistData(jsonObject);
                            if (!vwoPersistData.isGoalExists(goal.getId())) {
                                vwoPersistData.addGoal(goal.getId());
                                vwoPersistData.saveCampaign(mVWO.getVwoPreference());

                                String goalUrl = mVWO.getVwoUrlBuilder().getGoalUrl(campaign.getId(),
                                        campaign.getVariation().getId(), goal.getId(), (float) value);
                                VWOPersistData.addToQueue(mVWO.getVwoPreference(), goalUrl);
                            } else {
                                VWOLog.w(VWOLog.CAMPAIGN_LOGS, "Duplicate goal identifier: " + goalIdentifier, true);
                            }
                        } catch (JSONException exception) {
                            VWOLog.w(VWOLog.CAMPAIGN_LOGS, "Unable to generate goal data object", exception, true);
                        }

                    }
                }
            }
        }
    }

    private void generateVariationHash() {
        if (mVariations == null) {
            mVariations = new HashMap<>();
        } else {
            mVariations.clear();
        }

        for (Campaign campaign : mCampaigns) {
            Iterator<?> keys = campaign.getVariation().getServeObject().keys();
            while (keys.hasNext()) {
                String key = (String) keys.next();
                mVariations.put(key, campaign);
            }
        }
    }
}
