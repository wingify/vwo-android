package com.vwo.mobile.segmentation;

import com.vwo.mobile.Vwo;

/**
 * Created by abhishek on 23/09/15 at 4:24 PM.
 */
public interface Segment {

    boolean evaluate(Vwo vwp);
    boolean isCustomSegment();
}
