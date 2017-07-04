package com.vwo.mobile.segmentation;

import com.vwo.mobile.VWO;
import com.vwo.mobile.constants.AppConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by abhishek on 18/09/15 at 1:37 PM.
 */
public class CustomSegment implements Segment {

    private static final String PREVIOUS_LOGICAL_OPERATOR = "prevLogicalOperator";
    private static final String TYPE = "type";
    private static final String OPERATOR = "operator";
    private static final String OPERAND_VALUE = "rOperandValue";
    private static final String L_OPERAND_VALUE = "lOperandValue";
    private static final String LEFT_BRACKET = "lBracket";
    private static final String RIGHT_BRACKET = "rBracket";

    private boolean mLeftBracket;
    private boolean mRightBracket;
    private JSONArray mOperandValue;
    private String lOperandValue;
    private LogicalOperator mPreviousLogicalOperator;
    private int mSegmentOperator;
    private String mType;

    public CustomSegment(JSONObject segment) {

        try {
            mLeftBracket = segment.has(LEFT_BRACKET) && segment.getBoolean(LEFT_BRACKET);

            mRightBracket = segment.has(RIGHT_BRACKET) && segment.getBoolean(RIGHT_BRACKET);

            if (segment.has(PREVIOUS_LOGICAL_OPERATOR)) {
                mPreviousLogicalOperator = LogicalOperator.fromString(segment.getString(PREVIOUS_LOGICAL_OPERATOR));

            }

            JSONArray operandValue = segment.optJSONArray(OPERAND_VALUE);
            if (operandValue == null) {
                String operandAsString = segment.getString(OPERAND_VALUE);
                mOperandValue = new JSONArray();
                mOperandValue.put(operandAsString);
            } else {
                mOperandValue = operandValue;
            }

            lOperandValue = segment.getString(L_OPERAND_VALUE);
            mSegmentOperator = segment.getInt(OPERATOR);
            mType = segment.getString(TYPE);


            // TODO: Add segment operator

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public boolean isLeftBracket() {
        return mLeftBracket;
    }

    public boolean isRightBracket() {
        return mRightBracket;
    }

    public LogicalOperator getPreviousLogicalOperator() {
        return mPreviousLogicalOperator;
    }

    public String getType() {
        return mType;
    }

    @Override
    public boolean evaluate(VWO vwo) {
        if (mType.equals(AppConstants.CUSTOM_SEGMENT)) {
            return CustomSegmentEvaluateEnum.getEvaluator(mType, mSegmentOperator).evaluate(vwo, mOperandValue, lOperandValue);
        }
        return CustomSegmentEvaluateEnum.getEvaluator(mType, mSegmentOperator).evaluate(vwo, mOperandValue);
    }

    @Override
    public boolean isCustomSegment() {
        return true;
    }
}
