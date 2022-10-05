package com.vwo.mobile.meg;

import java.util.HashMap;

/**
 * We have an assumption that : one campaign will not belong to more than one group. This class
 * keeps tracks of the campaigns and where it belongs. And it is called before we can add the
 * campaign to any group.
 */
public final class CampaignUniquenessTracker {

    private CampaignUniquenessTracker() {
    }

    private static final HashMap<String, String> CAMPAIGNS = new HashMap<>();

    public static boolean groupContainsCampaign(String campaign) {
        return CAMPAIGNS.get(campaign) != null;
    }

    public static String getNameOfGroupFor(String campaign) {
        return CAMPAIGNS.get(campaign);
    }

    public static void addCampaignAsRegistered(String campaign, String group) {
        CAMPAIGNS.put(campaign, group);
    }

}
