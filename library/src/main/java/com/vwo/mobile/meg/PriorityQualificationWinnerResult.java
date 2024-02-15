package com.vwo.mobile.meg;

/**
 * Created by { Nabin Niroula } on 01/05/2023 for android-sdk.
 **/
public class PriorityQualificationWinnerResult {

    /**
     * To identify whether this check was done for {groupId} or just the {test_key}.
     */
    private boolean isGroupInPriority;

    /**
     * Will be set true when all the conditions are met. When this flag is set to true, all the conditions are
     * satisfied and the related campaign can be a winner.
     */
    private boolean isQualified;

    /**
     * Will be true if the priority campaign was found, this will be helpful for optimization
     * when a campaign was found but was not qualified as a winner.
     */
    private boolean isPriorityCampaignFound;

    public boolean isGroupInPriority() {
        return isGroupInPriority;
    }

    public boolean isPriorityCampaignFound() {
        return isPriorityCampaignFound;
    }

    public boolean isQualified() {
        return isQualified;
    }

    public boolean isNotQualified() {
        return !isQualified();
    }

    public void setGroupInPriority(boolean groupInPriority) {
        isGroupInPriority = groupInPriority;
    }

    public void setPriorityCampaignFound(boolean priorityCampaignFound) {
        isPriorityCampaignFound = priorityCampaignFound;
    }

    public void setQualified(boolean qualified) {
        isQualified = qualified;
    }

    public boolean shouldContinueWithFurtherChecks() {
        // if true will continue with the unequal weight distribution
        // if false will return null from that point itself.
        return isGroupInPriority && !isQualified;
    }

}
