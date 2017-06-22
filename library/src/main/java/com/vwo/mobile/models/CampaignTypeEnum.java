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

        for (CampaignTypeEnum anEnum : enums) {
            if (anEnum.getType().equals(campaignStatus)) {
                return anEnum;
            }
        }

        return null;
    }

    public String getType() {
        return mType;
    }
}
