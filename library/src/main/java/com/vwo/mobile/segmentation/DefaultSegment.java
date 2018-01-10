package com.vwo.mobile.segmentation;

import com.vwo.mobile.VWO;

/**
 * Created by abhishek on 09/10/15 at 5:42 PM.
 */
public class DefaultSegment extends Segment {

    public DefaultSegment(VWO vwo) {
        super(vwo);
    }

    @Override
    public boolean evaluate() {
        return true;
    }

    @Override
    public boolean isCustomSegment() {
        return false;
    }
}
