package com.vwo.mobile.segmentation;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.vwo.mobile.VWO;
import com.vwo.mobile.constants.AppConstants;
import com.vwo.mobile.utils.VWOLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by abhishek on 18/09/15 at 1:37 PM.
 */
public class CustomSegment extends Segment {

    private static final String PREVIOUS_LOGICAL_OPERATOR = "prevLogicalOperator";
    private static final String TYPE = "type";
    private static final String OPERATOR = "operator";
    private static final String OPERAND_VALUE = "rOperandValue";
    private static final String L_OPERAND_VALUE = "lOperandValue";
    private static final String LEFT_BRACKET = "lBracket";
    private static final String RIGHT_BRACKET = "rBracket";

    private JSONArray mOperandValue;
    @Nullable
    private String lOperandValue;
    private Operator mPreviousLogicalOperator;
    private int mSegmentOperator;
    @Nullable
    private String mType;

    public CustomSegment(VWO vwo, JSONObject segment) {
        super(vwo);

        try {
            setLeftBracket(segment.has(LEFT_BRACKET) && segment.getBoolean(LEFT_BRACKET));

            setRightBracket(segment.has(RIGHT_BRACKET) && segment.getBoolean(RIGHT_BRACKET));

            if (segment.has(PREVIOUS_LOGICAL_OPERATOR)) {
                mPreviousLogicalOperator = Operator.fromString(segment.getString(PREVIOUS_LOGICAL_OPERATOR));
            }

            JSONArray operandValue = segment.optJSONArray(OPERAND_VALUE);
            if (operandValue == null) {
                String operandAsString = segment.getString(OPERAND_VALUE);
                mOperandValue = new JSONArray();
                mOperandValue.put(operandAsString);
            } else {
                mOperandValue = operandValue;
            }

            if(segment.has(L_OPERAND_VALUE)) {
                lOperandValue = segment.getString(L_OPERAND_VALUE);
            }
            mSegmentOperator = segment.getInt(OPERATOR);
            mType = segment.getString(TYPE);


            // TODO: Add segment operator

        } catch (JSONException exception) {
            VWOLog.e(VWOLog.DATA_LOGS, "Data: " + segment.toString(), exception, false, true);
        }
    }

    @Nullable
    public Operator getPreviousLogicalOperator() {
        return mPreviousLogicalOperator;
    }

    @Nullable
    public String getType() {
        return mType;
    }

    @Override
    public boolean evaluate() {
        if (!TextUtils.isEmpty(mType) && mType.equals(AppConstants.CUSTOM_SEGMENT)) {
            return CustomSegmentEvaluateEnum.getEvaluator(mType, mSegmentOperator).evaluate(vwo, mOperandValue, lOperandValue);
        }
        return CustomSegmentEvaluateEnum.getEvaluator(mType, mSegmentOperator).evaluate(vwo, mOperandValue);
    }

    @Override
    public boolean isCustomSegment() {
        return true;
    }

    public List<Object> toInfix() {
        List<Object> list = new ArrayList<>();
        if (getPreviousLogicalOperator() != null) {
            list.add(getPreviousLogicalOperator());
        }
        if (hasLeftBracket()) {
            list.add(Operator.OPEN_PARENTHESES);
        }
        list.add(evaluate());
        if (hasRightBracket()) {
            list.add(Operator.CLOSE_PARENTHESES);
        }

        return list;
    }
}
