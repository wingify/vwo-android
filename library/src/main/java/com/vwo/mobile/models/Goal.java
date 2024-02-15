package com.vwo.mobile.models;

import org.json.JSONException;
import org.json.JSONObject;

public class Goal {

    public final static String TYPE = "type";
    public final static String IDENTIFIER = "identifier";
    public final static String ID = "id";
    public final static String REVENUE_PROP = "revenueProp";
    public final static String GOAL_TRIGGERED = "triggered";
    public final static String GOAL_MCA = "mca";
    public final static int GOAL_TYPE_RECURRING = -1;
    public final static int GOAL_TYPE_DEFAULT = 0;

    private GoalEnum mGoalType;
    private String mIdentifier;
    private int mId;
    private boolean mIsGoalTriggered;
    private String mRevenueProp;
    private int mMca;

    public Goal(JSONObject goalData) {
        try {
            mId = goalData.getInt(ID);
            mIdentifier = goalData.getString(IDENTIFIER);
            mGoalType = GoalEnum.getEnumFromGoal(goalData.getString(TYPE));
            mRevenueProp = goalData.optString(REVENUE_PROP, null);
            mMca = goalData.optInt(GOAL_MCA, GOAL_TYPE_DEFAULT);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            mIsGoalTriggered = goalData.getBoolean(GOAL_TRIGGERED);
        } catch (JSONException e) {
            mIsGoalTriggered = false;

        }
    }

    public boolean isGoalTriggered() {
        return mIsGoalTriggered;
    }

    public void setIsGoalTriggered(boolean isGoalTriggered) {
        mIsGoalTriggered = isGoalTriggered;
    }

    public GoalEnum getGoalType() {
        return mGoalType;
    }

    public String getIdentifier() {
        return mIdentifier;
    }

    public int getId() {
        return mId;
    }

    public JSONObject getGoalAsJsonObject() throws JSONException {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put(ID, mId);
        jsonObject.put(IDENTIFIER, mIdentifier);
        jsonObject.put(TYPE, mGoalType.getGoalType());
        jsonObject.put(GOAL_TRIGGERED, mIsGoalTriggered);
        jsonObject.put(REVENUE_PROP, mRevenueProp);
        return jsonObject;
    }

    public String getRevenueProp() {
        return mRevenueProp;
    }

    public boolean isRecurringGoal() {
        return mMca == GOAL_TYPE_RECURRING;
    }
}
