package com.vwo.mobile.segmentation;

import com.vwo.mobile.VWO;

/**
 * Created by abhishek on 23/09/15 at 4:24 PM.
 */
public interface Segment {

    boolean evaluate(VWO vwp);
    boolean isCustomSegment();
}
