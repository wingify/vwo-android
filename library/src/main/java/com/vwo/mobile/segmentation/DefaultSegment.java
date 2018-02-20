package com.vwo.mobile.segmentation;

import com.vwo.mobile.VWO;

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
