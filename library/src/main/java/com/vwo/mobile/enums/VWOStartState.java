package com.vwo.mobile.enums;

/**
 * Created by abhishek on 07/10/15 at 5:37 PM.
 */
public enum VWOStartState {

    NOT_STARTED(0),
    STARTING(1),
    STARTED(2),
    NO_INTERNET(3);

    private final int mValue;


    VWOStartState(final int newValue) {
        mValue = newValue;
    }

    public int getValue() { return mValue; }
}
