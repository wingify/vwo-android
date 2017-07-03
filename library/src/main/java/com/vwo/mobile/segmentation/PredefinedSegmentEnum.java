package com.vwo.mobile.segmentation;

import com.vwo.mobile.VWO;
import com.vwo.mobile.constants.AppConstants;
import com.vwo.mobile.data.VWOPersistData;
import com.vwo.mobile.utils.VWOUtils;

/**
 * Created by abhishek on 09/10/15 at 4:24 PM.
 */
public enum PredefinedSegmentEnum {


    DEVICE(AppConstants.DEVICE, new EvaluateSegment() {
        @Override
        public boolean evaluate(VWO vwo, Object value) {
            String deviceType = value.toString();
            assert vwo.getCurrentContext() != null;
            if (VWOUtils.isTablet(vwo.getCurrentContext()) && deviceType.equalsIgnoreCase("Tablet")) {
                return true;
            } else if (!VWOUtils.isTablet(vwo.getCurrentContext()) && deviceType.equalsIgnoreCase("phone")) {
                return true;
            }
            return false;
        }
    }),
    RETURNING_USER(AppConstants.RETURNING_USER, new EvaluateSegment() {
        @Override
        public boolean evaluate(VWO vwo, Object value) {
            boolean isReturningUser = (boolean) value;
            if (VWOPersistData.isReturningUser(vwo) && isReturningUser) {
                return true;
            } else if (!VWOPersistData.isReturningUser(vwo) && !isReturningUser) {
                return true;
            }
            return false;
        }
    }),
    DEFAULT("", new EvaluateSegment() {
        @Override
        public boolean evaluate(VWO vwo, Object value) {
            return true;
        }
    });

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
