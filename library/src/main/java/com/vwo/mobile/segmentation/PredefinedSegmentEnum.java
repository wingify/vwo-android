package com.vwo.mobile.segmentation;

import com.vwo.mobile.VWO;
import com.vwo.mobile.constants.AppConstants;
import com.vwo.mobile.data.VWOPersistData;
import com.vwo.mobile.utils.VWOUtils;

/**
 * Created by abhishek on 09/10/15 at 4:24 PM.
 */
public enum PredefinedSegmentEnum {


    DEVICE(AppConstants.DEVICE, (vwo, value) -> {
        String deviceType = value.toString();

        return VWOUtils.isTablet(vwo.getCurrentContext()) && deviceType.equalsIgnoreCase("Tablet") ||
                !VWOUtils.isTablet(vwo.getCurrentContext()) && deviceType.equalsIgnoreCase("phone");
    }),
    RETURNING_USER(AppConstants.RETURNING_USER, (vwo, value) -> {
        boolean isReturningUser = (boolean) value;
        return VWOPersistData.isReturningUser(vwo) && isReturningUser || !VWOPersistData.isReturningUser(vwo) && !isReturningUser;
    }),
    DEFAULT("", (vwo, value) -> true);

    private String mType;

    private EvaluateSegment mEvaluateSegment;

    PredefinedSegmentEnum(String type, EvaluateSegment evaluateSegment) {
        mType = type;
        mEvaluateSegment = evaluateSegment;
    }

    public static EvaluateSegment getEvaluator(String type) {

        if (type.equalsIgnoreCase("device")) {
            return DEVICE.mEvaluateSegment;
        } else if (type.equalsIgnoreCase("returning_visitor")) {
            return RETURNING_USER.getEvaluateSegment();
        } else {
            return DEFAULT.getEvaluateSegment();
        }
    }

    public interface EvaluateSegment {
        boolean evaluate(VWO vwo, Object value);
    }

    public String getType() {
        return mType;
    }

    public EvaluateSegment getEvaluateSegment() {
        return mEvaluateSegment;
    }
}
