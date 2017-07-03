package com.vwo.mobile.data;


import com.vwo.mobile.VWO;
import com.vwo.mobile.models.Campaign;
import com.vwo.mobile.models.Goal;
import com.vwo.mobile.segmentation.CustomSegment;
import com.vwo.mobile.segmentation.LogicalOperator;
import com.vwo.mobile.segmentation.Segment;
import com.vwo.mobile.utils.VWOLogger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;
import java.util.logging.Logger;

/**
 * Created by abhishek on 17/09/15 at 10:10 PM.
 */
public class VWOData {
    private static final Logger LOGGER = VWOLogger.getLogger(VWOData.class.getCanonicalName());

    public static final String CAMPAIGN_RUNNING = "RUNNING";
    public static final String CAMPAIGN_EXCLUDED = "EXCLUED";

    public static final String VWO_QUEUE = "VWO_QUEUE";
    private ArrayList<Campaign> mCampaigns;
    private Map<String, Campaign> mVariations;
    private VWO mVWO;

    public VWOData(VWO vwo) {
        this.mVWO = vwo;
        mCampaigns = new ArrayList<>();
    }

    public void parseData(JSONArray data) {

        for (int i = 0; i < data.length(); i++) {
            try {
                if (data.getJSONObject(i).getString(Campaign.STATUS).equals(CAMPAIGN_RUNNING)) {

                    // Only saving campaign if it fas a variation object
                    if (data.getJSONObject(i).has(Campaign.VARIATION)) {

                        Campaign tempCampaign = new Campaign(data.getJSONObject(i));

                        if (VWOPersistData.isExistingCampaign(mVWO, VWOPersistData.CAMPAIGN_KEY + tempCampaign.getId())) {
                            // Already part of campaign. Just add to campaigns list
                            mCampaigns.add(tempCampaign);
                            LOGGER.warning("Campaign " + tempCampaign.getId() + " is already a part");

                        } else {
                            if (evaluateSegmentation(mVWO, tempCampaign)) {
                                mCampaigns.add(tempCampaign);

                                String campaignRecordUrl = mVWO.getVwoUrlBuilder().getCampaignUrl(tempCampaign.getId(), tempCampaign.getVariation().getId());
                                LOGGER.info("Campaign " + tempCampaign.getId() + " is a new and valid campaign");
                                VWOPersistData VWOPersistData = new VWOPersistData(tempCampaign.getId(), tempCampaign.getVariation().getId());
                                VWOPersistData.saveCampaign(mVWO.getVwoPreference());
                                VWOPersistData.addToQueue(mVWO.getVwoPreference(), campaignRecordUrl);
                            } else {
                                LOGGER.finest("Campaign " + tempCampaign.getId() + ", Segmentation Condition not met, discarding");
                            }
                        }
                    }
                } else if (data.getJSONObject(i).getString(Campaign.STATUS).equals(CAMPAIGN_EXCLUDED)) {
                    int campaignId = data.getJSONObject(i).getInt("id");
                    VWOPersistData VWOPersistData = new VWOPersistData(campaignId, 0);
                    VWOPersistData.saveCampaign(mVWO.getVwoPreference());
                } else {
                    LOGGER.finer("Campaign " + data.getJSONObject(i).getInt("id") + ", Discarding because it is not running");
                }

            } catch (JSONException exception) {
                LOGGER.throwing(VWOData.class.getSimpleName(), "parseData(JSONArray)", exception);
            }
        }

        if (mCampaigns.size() > 0) {
            VWOPersistData.updateReturningUser(mVWO);
        }

        generateVariationHash();
    }

    public Object getVariationForKey(String key) {

        if (mVariations == null) {
            return null;
        }

        if (mVariations.containsKey(key)) {
            Campaign campaign = mVariations.get(key);
            return campaign.getVariation().getKey(key);
        }
        return null;
    }

    public Object getAllVariations() {
        if (mVariations == null) {
            return new JSONObject();
        } else {

            JSONObject data = new JSONObject();
            for (Map.Entry<String, Campaign> variation : mVariations.entrySet()) {
                try {
                    data.put(variation.getKey(), variation.getValue().getVariation().getKey(variation.getKey()));
                } catch (JSONException exception) {
                    LOGGER.throwing(VWOData.class.getSimpleName(), "getAllVariations()", exception);
                }
            }
            return data;
        }

    }

    public void saveGoal(String goalIdentifier) {

        for (Campaign campaign : mCampaigns) {
            for (Goal goal : campaign.getGoals()) {
                if (goal.getIdentifier().equals(goalIdentifier)) {
                    String campaignData = mVWO.getVwoPreference().getString(VWOPersistData.CAMPAIGN_KEY + campaign.getId());
                    if (campaignData != null && !campaignData.equals("")) {
                        try {
                            JSONObject jsonObject = new JSONObject(campaignData);
                            VWOPersistData VWOPersistData = new VWOPersistData(jsonObject);
                            if (!VWOPersistData.isGoalExists(goal.getId())) {
                                VWOPersistData.addGoal(goal.getId());
                                VWOPersistData.saveCampaign(mVWO.getVwoPreference());

                                String goalUrl = mVWO.getVwoUrlBuilder().getGoalUrl(campaign.getId(), campaign.getVariation().getId(), goal.getId());
                                VWOPersistData.addToQueue(mVWO.getVwoPreference(), goalUrl);
                            } else {
                                LOGGER.info(goalIdentifier + " found in existing data");
                            }
                        } catch (JSONException exception) {
                            LOGGER.throwing(VWOData.class.getSimpleName(), "saveGoal(String)", exception);
                        }

                    }
                }
            }
        }
    }

    public void saveGoal(String goalIdentifier, double value) {

        for (Campaign campaign : mCampaigns) {
            for (Goal goal : campaign.getGoals()) {
                if (goal.getIdentifier().equals(goalIdentifier)) {
                    String campaignData = mVWO.getVwoPreference().getString(VWOPersistData.CAMPAIGN_KEY + campaign.getId());
                    if (campaignData != null && !campaignData.equals("")) {
                        try {
                            JSONObject jsonObject = new JSONObject(campaignData);
                            VWOPersistData VWOPersistData = new VWOPersistData(jsonObject);
                            if (!VWOPersistData.isGoalExists(goal.getId())) {
                                VWOPersistData.addGoal(goal.getId());
                                VWOPersistData.saveCampaign(mVWO.getVwoPreference());

                                String goalUrl = mVWO.getVwoUrlBuilder().getGoalUrl(campaign.getId(), campaign.getVariation().getId(), goal.getId(), (float) value);
                                VWOPersistData.addToQueue(mVWO.getVwoPreference(), goalUrl);
                            } else {
                                LOGGER.info(goalIdentifier + " found in existing data");
                            }
                        } catch (JSONException exception) {
                            LOGGER.throwing(VWOData.class.getSimpleName(), "saveGoal(String, double)", exception);
                        }

                    }
                }
            }
        }
    }

    private void generateVariationHash() {
        if (mVariations == null) {
            mVariations = new HashMap<>();
        }

        for (Campaign campaign : mCampaigns) {
            Iterator<?> keys = campaign.getVariation().getServeObject().keys();
            while (keys.hasNext()) {
                String key = (String) keys.next();
                mVariations.put(key, campaign);
            }
        }
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
}
