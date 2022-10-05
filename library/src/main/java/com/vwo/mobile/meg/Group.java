package com.vwo.mobile.meg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class Group {

    public static final String TAG = Group.class.getSimpleName();

    private int id = Integer.MIN_VALUE;

    /**
     * Name of the group
     */
    private String name = null;

    /**
     * The list of campaigns assigned for this group.
     */
    private final ArrayList<String> campaignList = new ArrayList<>();

    /**
     * A simple key value based mechanism to check where our weight belongs to.
     */
    private HashMap<String, ArrayList<Float>> weightMap = null;

    private float weight;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCampaignSize() {
        return campaignList.size();
    }

    public ArrayList<String> getCampaigns() {
        return campaignList;
    }

    private void calculateWeight() {
        float total = 100f; // because 100%
        int totalCampaigns = campaignList.size();
        weight = (total / totalCampaigns);
    }

    public void addCampaign(String campaign) {

        if (CampaignUniquenessTracker.groupContainsCampaign(campaign)) {
            MutuallyExclusiveGroups.log("addCampaign: could not add campaign [ " + campaign + " ] to group [ "
                    + getName() + " ] because it already belongs to group [ "
                    + CampaignUniquenessTracker.getNameOfGroupFor(campaign) + " ]");
            return;
        }

        CampaignUniquenessTracker.addCampaignAsRegistered(campaign, getName());
        this.campaignList.add(campaign);
        calculateWeight();
    }

    public void removeCampaign(String campaign) {
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < campaignList.size(); i++) {
            String value = campaignList.get(i);
            if (Objects.equals(value, campaign)) continue;
            list.add(value);
        }
        campaignList.clear();
        campaignList.addAll(list);
        calculateWeight();
    }

    public float getWeight() {
        return weight;
    }

    public String getOnlyIfPresent(String toSearch) {
        for (String campaignId : campaignList) {
            if (toSearch.equals(campaignId)) return String.valueOf(id);
        }
        return null;
    }

    public String getNameOnlyIfPresent(String toSearch) {
        for (String campaignId : campaignList) {
            if (toSearch.equals(campaignId)) return String.valueOf(name);
        }
        return null;
    }

    public String getCampaignForRespectiveWeight(float weight) {
        createWeightMap();

        for (String key :
                weightMap.keySet()) {
            ArrayList<Float> weightMaxMin = weightMap.get(key);
            if (weightMaxMin == null) continue;

            boolean weightIsGreaterThanMin = (weight > weightMaxMin.get(0));
            boolean weightIsLessThanMax = (weight <= weightMaxMin.get(1));
            if (weightIsGreaterThanMin && weightIsLessThanMax) {
                MutuallyExclusiveGroups.log("campaign [ " + key + " ] found for the given weight [ " + weight + " ] in group [ " + getNameOnlyIfPresent(key) + " ]");
                return key;
            }
        }

        return null;
    }

    private void createWeightMap() {

        if (weightMap == null) {
            weightMap = new HashMap<>();
        }

        float weightBinValue = 0;

        // A 0 - 33.33 , B 33.33 - 66.33 , C 66.333, 100.0
        for (int i = 0; i < campaignList.size(); i++) {
            ArrayList<Float> range = new ArrayList<>();
            range.add(weightBinValue);
            weightBinValue += weight;
            range.add(weightBinValue);
            weightMap.put(campaignList.get(i), range);
        }

    }

}
