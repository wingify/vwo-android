package com.vwo.mobile.segmentation;


/**
 * Created by abhishek on 18/09/15 at 2:04 PM.
 */
public enum LogicalOperator {

    OR("OR"),
    AND("AND");

    private final String mOperator;

    LogicalOperator(String operator) {
        this.mOperator = operator;
    }

    public static LogicalOperator fromString(String operatorString) {
        LogicalOperator[] operators = LogicalOperator.values();
        for (LogicalOperator operator : operators) {
            if (operator.toString().equals(operatorString)) {
                return operator;
            }
        }
        return null;
    }

    public String toString() {
        return this.mOperator;
    }

    public boolean evaluate(boolean value1, boolean value2) {
        if (this == OR) {
            return value1 || value2;
        } else {
            return value1 && value2;
        }
    }

}
