package com.vwo.mobile.models;


import com.vwo.mobile.segmentation.CustomSegment;
import com.vwo.mobile.segmentation.CustomSegmentEvaluateEnum;
import com.vwo.mobile.segmentation.DefaultSegment;
import com.vwo.mobile.segmentation.LogicalOperator;
import com.vwo.mobile.segmentation.PredefinedSegment;
import com.vwo.mobile.segmentation.Segment;
import com.vwo.mobile.utils.VwoLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Stack;

/**
 * Created by abhishek on 17/09/15 at 12:08 PM.
 */
public class Campaign {

    public final static String ID = "id";
    public final static String VERSION = "version";
    public final static String STATUS = "status";
    public final static String TRAFFIC = "pc_traffic";
    public final static String TYPE = "type";
    public final static String VARIATION = "variations";
    public final static String GOALS = "goals";
    public static final String COUNT_GOAL_ONCE = "count_goal_once";
    public static final String CLICK_MAP = "clickmap";
    private static final String SEGMENT_CODE = "segment_object";
    private static final String SEGMENT_TYPE = "type";
    private static final String PARTIAL_SEGMENTS = "partialSegments";

    public static final String SEGMENT_CUSTOM = "custom";
    public static final String SEGMENT_PREDEFINED = "predefined";
    public static final String SEGMENT_DEFAULT = "default";


    private long mId;
    private int mVersion;
    private int mTraffic;
    private CampaignTypeEnum mType;
    private boolean mCountGoalOnce;
    private boolean mIsClickMap;
    private ArrayList<Goal> mGoals;
    private Variation mVariation;
    private boolean isExperimentServed;
    private String mSegmentType;
    private ArrayList<Segment> mSegments;

    public Campaign(JSONObject campaignData) {
        try {
            this.mId = campaignData.getInt(ID);
            this.mVersion = campaignData.getInt(VERSION);
            mGoals = new ArrayList<>();
            this.mTraffic = campaignData.getInt(TRAFFIC);
            this.mType = CampaignTypeEnum.getEnumFromCampaign(campaignData.getString(TYPE));

            JSONArray goals = campaignData.getJSONArray(GOALS);
            for (int i = 0; i < goals.length(); i++) {
                JSONObject goal = goals.getJSONObject(i);
                mGoals.add(new Goal(goal));
                mVariation = new Variation(campaignData.getJSONObject(VARIATION));
            }

            int clickMap = campaignData.getInt(CLICK_MAP);
            this.mIsClickMap = (clickMap != 0);

            int countGoalOnce = campaignData.getInt(COUNT_GOAL_ONCE);
            this.mCountGoalOnce = (countGoalOnce != 0);


            if (campaignData.has(SEGMENT_CODE)) {

                JSONObject segmentCode = campaignData.getJSONObject(SEGMENT_CODE);
                // Check if segmentation is CUSTOM OR PREDEFINED
                if (segmentCode.getString(SEGMENT_TYPE).equals(SEGMENT_CUSTOM)) {
                    mSegmentType = SEGMENT_CUSTOM;
                    mSegments = new ArrayList<>();
                    JSONArray partialSegments = segmentCode.getJSONArray(PARTIAL_SEGMENTS);
                    for (int i = 0; i < partialSegments.length(); i++) {
                        mSegments.add(new CustomSegment(partialSegments.getJSONObject(i)));
                    }

                } else if (segmentCode.getString(SEGMENT_TYPE).equals(SEGMENT_PREDEFINED)) {
                    mSegments = new ArrayList<>();
                    mSegments.add(new PredefinedSegment(segmentCode));
                    mSegmentType = SEGMENT_PREDEFINED;
                } else {
                    mSegments = new ArrayList<>();
                    mSegments.add(new DefaultSegment());
                    mSegmentType = SEGMENT_DEFAULT;
                }


            } else {
                mSegments = new ArrayList<>();
                mSegments.add(new DefaultSegment());
                mSegmentType = SEGMENT_DEFAULT;
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public long getId() {
        return mId;
    }

    public int getVersion() {
        return mVersion;
    }

    public int getTraffic() {
        return mTraffic;
    }

    public CampaignTypeEnum getType() {
        return mType;
    }

    public boolean isCountGoalOnce() {
        return mCountGoalOnce;
    }

    public boolean isClickMap() {
        return mIsClickMap;
    }

    public ArrayList<Goal> getGoals() {
        return mGoals;
    }

    public Variation getVariation() {
        return mVariation;
    }

    public boolean isExperimentServed() {
        return isExperimentServed;
    }

    public void setIsExperimentServed(boolean isExperimentServed) {
        this.isExperimentServed = isExperimentServed;
    }

    public ArrayList<Segment> getSegments() {
        return mSegments;
    }

    public JSONObject getCampaignAsJsonObject() throws JSONException {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put(ID, mId);
        jsonObject.put(VERSION, mVersion);
        jsonObject.put(TRAFFIC, mTraffic);
        jsonObject.put(TYPE, mType.getType());
        jsonObject.put(VARIATION, mVariation.getVariationAsJsonObject());
        jsonObject.put(COUNT_GOAL_ONCE, mCountGoalOnce);
        jsonObject.put(CLICK_MAP, mIsClickMap);

        JSONArray goalArray = new JSONArray();

        for (Goal goal : mGoals) {
            goalArray.put(goal.getGoalAsJsonObject());
        }
        jsonObject.put(GOALS, goalArray);
        return jsonObject;
    }

    public String getSegmentType() {
        return mSegmentType;
    }
}
