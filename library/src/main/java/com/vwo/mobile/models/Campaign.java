package com.vwo.mobile.models;


import com.vwo.mobile.VWO;
import com.vwo.mobile.segmentation.CustomSegment;
import com.vwo.mobile.segmentation.DefaultSegment;
import com.vwo.mobile.segmentation.PredefinedSegment;
import com.vwo.mobile.segmentation.Segment;
import com.vwo.mobile.utils.VWOLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Campaign {

    public final static String ID = "id";
    public final static String VERSION = "version";
    public final static String STATUS = "status";
    public final static String TRAFFIC = "pc_traffic";
    public final static String TYPE = "type";
    public static final String CAMPAIGN_NAME = "name";
    public final static String VARIATION = "variations";
    public final static String GOALS = "goals";
    public static final String COUNT_GOAL_ONCE = "count_goal_once";
    public static final String CLICK_MAP = "clickmap";
    public static final String SEGMENT_CUSTOM = "custom";
    public static final String SEGMENT_PREDEFINED = "predefined";
    public static final String SEGMENT_DEFAULT = "default";
    // Track user automatically for a given campaign
    public static final String TRACK_USER_AUTOMATICALLY = "track_user_on_launch";
    private static final String SEGMENT_CODE = "segment_object";
    private static final String SEGMENT_TYPE = "type";
    private static final String PARTIAL_SEGMENTS = "partialSegments";
    private long mId;
    private int mVersion;
    private boolean trackUserAutomatically;
    private CampaignTypeEnum mType;
    private ArrayList<Goal> mGoals;
    private Variation mVariation;
    private String mSegmentType;
    private String name;
    private ArrayList<Segment> mSegments;

    public Campaign(VWO vwo, JSONObject campaignData) {
        try {
            VWOLog.v(VWOLog.CAMPAIGN_LOGS, campaignData.toString());

            this.mId = campaignData.getInt(ID);
            this.mVersion = campaignData.getInt(VERSION);
            mGoals = new ArrayList<>();
            this.mType = CampaignTypeEnum.getEnumFromCampaign(campaignData.getString(TYPE));

            if (campaignData.has(CAMPAIGN_NAME)) {
                name = campaignData.getString(CAMPAIGN_NAME);
            } else {
                name = "campaign";
            }

            JSONArray goals = campaignData.getJSONArray(GOALS);
            for (int i = 0; i < goals.length(); i++) {
                JSONObject goal = goals.getJSONObject(i);
                mGoals.add(new Goal(goal));
                mVariation = new Variation(campaignData.getJSONObject(VARIATION));
            }

            try {
                this.trackUserAutomatically = campaignData.getBoolean(TRACK_USER_AUTOMATICALLY);
            } catch (JSONException exception) {
                this.trackUserAutomatically = false;
                VWOLog.e(VWOLog.DATA_LOGS, "Cannot find or parse key: " + TRACK_USER_AUTOMATICALLY,
                        exception, true, true);
            }

            int clickMap = campaignData.getInt(CLICK_MAP);

            int countGoalOnce = campaignData.getInt(COUNT_GOAL_ONCE);


            if (campaignData.has(SEGMENT_CODE) && campaignData.getJSONObject(SEGMENT_CODE).has(SEGMENT_TYPE)) {

                JSONObject segmentCode = campaignData.getJSONObject(SEGMENT_CODE);
                // Check if segmentation is CUSTOM OR PREDEFINED
                switch (segmentCode.getString(SEGMENT_TYPE)) {
                    case SEGMENT_CUSTOM:
                        mSegmentType = SEGMENT_CUSTOM;
                        mSegments = new ArrayList<>();
                        JSONArray partialSegments = segmentCode.getJSONArray(PARTIAL_SEGMENTS);
                        for (int i = 0; i < partialSegments.length(); i++) {
                            mSegments.add(new CustomSegment(vwo, partialSegments.getJSONObject(i)));
                        }
                        break;
                    case SEGMENT_PREDEFINED:
                        mSegments = new ArrayList<>();
                        mSegments.add(new PredefinedSegment(vwo, segmentCode));
                        mSegmentType = SEGMENT_PREDEFINED;
                        break;
                    default:
                        mSegments = new ArrayList<>();
                        mSegments.add(new DefaultSegment(vwo));
                        mSegmentType = SEGMENT_DEFAULT;
                        break;
                }
            } else {
                mSegments = new ArrayList<>();
                mSegments.add(new DefaultSegment(vwo));
                mSegmentType = SEGMENT_DEFAULT;
            }
        } catch (JSONException e) {
            VWOLog.e(VWOLog.DATA_LOGS, e.getMessage(), true, true);
        }
    }

    public long getId() {
        return mId;
    }

    public String getName() {
        return this.name;
    }

    public int getVersion() {
        return mVersion;
    }

    public CampaignTypeEnum getType() {
        return mType;
    }

    public ArrayList<Goal> getGoals() {
        return mGoals;
    }

    public Variation getVariation() {
        return mVariation;
    }

    public boolean shouldTrackUserAutomatically() {
        return trackUserAutomatically;
    }

    public ArrayList<Segment> getSegments() {
        return mSegments;
    }

    public String getSegmentType() {
        return mSegmentType;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Campaign && ((Campaign) obj).getId() == this.mId || super.equals(obj);
    }

    @Override
    public int hashCode() {
        return (int) mId;
    }
}
