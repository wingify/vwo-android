package com.vwo.mobile.models;

/**
 * Created by abhishek on 17/09/15 at 12:09 PM.
 */
public enum CampaignTypeEnum {
    VISUAL_AB("VISUAL_AB")
    ;

    private String mType;

    CampaignTypeEnum(String type) {
        mType = type;
    }

    public static CampaignTypeEnum getEnumFromCampaign(String campaignStatus) {

        CampaignTypeEnum[] enums = CampaignTypeEnum.values();

        for (int i = 0; i < enums.length; i++) {
            if (enums[i].getType().equals(campaignStatus)) {
                return enums[i];
            }
        }

        return null;
    }

    public String getType() {
        return mType;
    }
}
