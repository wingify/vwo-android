package com.vwo.mobile.models;

import org.json.JSONException;
import org.json.JSONObject;

public class Goal {

    public final static String TYPE = "type";
    public final static String IDENTIFIER = "identifier";
    public final static String ID = "id";
    public final static String GOAL_TRIGGERED = "triggered";

    private GoalEnum mGoalType;
    private String mIdentifier;
    private int mId;
    private boolean mIsGoalTriggered;

    public Goal(JSONObject goalData) {
        try {
            mId = goalData.getInt(ID);
            mIdentifier = goalData.getString(IDENTIFIER);
            mGoalType = GoalEnum.getEnumFromGoal(goalData.getString(TYPE));
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
        return jsonObject;
    }


}
