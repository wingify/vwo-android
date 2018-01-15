package com.vwo.mobile.segmentation;

import com.vwo.mobile.VWO;

public abstract class Segment {

    protected VWO vwo;
    private boolean leftBracket;
    private boolean rightBracket;

    Segment(VWO vwo) {
        this.leftBracket = false;
        this.rightBracket = false;
        this.vwo = vwo;
    }

    public abstract boolean evaluate();

    public abstract boolean isCustomSegment();

    public boolean hasLeftBracket() {
        return leftBracket;
    }

    public boolean hasRightBracket() {
        return rightBracket;
    }

    void setLeftBracket(boolean leftBracket) {
        this.leftBracket = leftBracket;
    }

    void setRightBracket(boolean rightBracket) {
        this.rightBracket = rightBracket;
    }

}
