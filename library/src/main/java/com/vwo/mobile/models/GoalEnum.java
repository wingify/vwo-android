package com.vwo.mobile.models;

/**
 * Created by abhishek on 17/09/15 at 11:54 AM.
 */
public enum GoalEnum {
    REVENUE_TRACKING("REVENUE_TRACKING"),
    CUSTOM_GOAL("CUSTOM_GOAL");

    private String mGoalType;

    GoalEnum(String goalType) {
        mGoalType = goalType;
    }

    public static GoalEnum getEnumFromGoal(String goalType) {

        GoalEnum[] goalEnums = GoalEnum.values();
        for (int i = 0; i < goalEnums.length; i++) {
            if (goalEnums[i].getGoalType().equals(goalType)) {
                return goalEnums[i];
            }
        }

        return null;
    }

    public String getGoalType() {
        return mGoalType;
    }

}
