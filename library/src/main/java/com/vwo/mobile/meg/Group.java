package com.vwo.mobile.meg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class Group {

    public static final String KEY_PRIORITY = "p";

    public static final String KEY_WEIGHT = "wt";

    public static final String KEY_ET = "et";
    public static final int VALUE_ET_INVALID = -1;
    public static final int VALUE_ET_RANDOM = 1;
    public static final int VALUE_ET_ADVANCE = 2;

    public static final String VALUE_INVALID_PRIORITY_CAMPAIGN = null;

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

    /**
     * Type of allocation:
     * 1 - Random
     * 2 - Advance
     * <p>
     * DOC: https://confluence.wingify.com/pages/viewpage.action?spaceKey=VWOENG&title=Mutually+Exclusive+Weights+and+Prioritization+in+Mobile+App+Testing
     */
    private int et = VALUE_ET_INVALID;

    /**
     * The priority to use if the [et] variable is set to Advance.
     */
    private final ArrayList<String> priorityCampaigns = new ArrayList<>();

    public ArrayList<String> getPriorityCampaigns() {
        return priorityCampaigns;
    }

    public void addPriority(String p) {
        priorityCampaigns.add(p);
    }

    public void addEt(int et) {
        this.et = et;
    }

    public int getEt() {
        return et;
    }

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

    public String getPriorityCampaign() {

        MutuallyExclusiveGroups.log("will try to check for priority campaign against campaign list in group -> " + getName());

        // check if et is advance as priority is not
        if (isNotAdvanceMEGAllocation()) {
            MutuallyExclusiveGroups.log("et ( " + et + " ) is not advance type, priority campaigns ( p ) will not be applicable.");
            return VALUE_INVALID_PRIORITY_CAMPAIGN;
        }

        if (priorityCampaigns.isEmpty()) {
            MutuallyExclusiveGroups.log("et is advance but the priority array is empty.");
            return VALUE_INVALID_PRIORITY_CAMPAIGN;
        }

        MutuallyExclusiveGroups.log("there are " + priorityCampaigns.size() + " priorityCampaigns in " + name);

        for (String priorityCampaign : priorityCampaigns) {
            if (campaignList.contains("" + priorityCampaign)) {
                MutuallyExclusiveGroups.log("priority campaign >> " + priorityCampaign + " << found in -> " + name);
                return priorityCampaign;
            } else {
                MutuallyExclusiveGroups.log("priority campaign >> " + priorityCampaign + " << doesn't exist in " + name);
            }
        }

        MutuallyExclusiveGroups.log("priority campaign not defined, caller should continue with normal MEG logic.");

        // we found nothing
        return VALUE_INVALID_PRIORITY_CAMPAIGN;
    }

    private boolean isNotAdvanceMEGAllocation() {
        return et != VALUE_ET_ADVANCE;
    }

    private void calculateWeight() {
        float total = 100f;
        int totalCampaigns = campaignList.size();
        weight = (total / totalCampaigns);
    }

    public void addCampaign(String campaign) {

        if (CampaignUniquenessTracker.groupContainsCampaign(campaign)) {
            MutuallyExclusiveGroups.log("addCampaign: could not add campaign [ " + campaign + " ] to group [ " + getName() + " ] because it already belongs to group [ " + CampaignUniquenessTracker.getNameOfGroupFor(campaign) + " ]");
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

        for (String key : weightMap.keySet()) {
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
        if (isNotAdvanceMEGAllocation()) {
            MutuallyExclusiveGroups.log("not using weight from the server, preparing EQUAL allocation because et = " + et + "[ NOTE: et=1->Random, et=2 -> Advance ]");

            createEquallyDistributedWeightMap();
        } else {
            MutuallyExclusiveGroups.log("weight is received from the server, preparing WEIGHTED allocation.");
            createWeightMapFromProvidedValues();
        }
    }

    private void createWeightMapFromProvidedValues() {
        if (weightMap == null) {
            weightMap = new HashMap<>();
        }

        MutuallyExclusiveGroups.log("morphing weighted allocation data to existing MEG weight format");
        for (int index = 0; index < weightMapFromServer.size(); index++) {
            Weight weight = weightMapFromServer.get(index);
            weightMap.put(weight.getCampaign(), weight.getRange());
        }
    }

    private void createEquallyDistributedWeightMap() {

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

    // using LinkedHashMap to maintain the insertion order
    private final ArrayList<Weight> weightMapFromServer = new ArrayList<>();

    public void addWeight(String campaign, int weight) {

        MutuallyExclusiveGroups.log("adding priority weight -> " + weight + " for campaign -> " + campaign);

        ArrayList<Float> weightRange = new ArrayList<>();
        if (weightMapFromServer.isEmpty()) {
            weightRange.add(0f); // will start at 0
            weightRange.add((float) weight); // end
        } else {
            // last weight's end will be this weight's start
            Weight lw = weightMapFromServer.get(weightMapFromServer.size() - 1);
            // add range
            weightRange.add(lw.getRangeEnd()); // start will be the end of last entry
            weightRange.add((lw.getRangeEnd() + weight)); // end will be start + current weight
        }

        Weight w = new Weight(campaign, weightRange);
        weightMapFromServer.add(w);
        MutuallyExclusiveGroups.log("campaign " + w.getCampaign() + " range " + w.getRangeStart() + " to " + w.getRangeEnd());

    }

    public boolean hasInPriority(String campaign) {
        return priorityCampaigns.contains(campaign);
    }

    public boolean doesNotHaveInPriority(String campaign) {
        return !hasInPriority(campaign);
    }

}
