package com.vwo.mobile;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by aman on Mon 19/03/18 18:36.
 */
public class Connection {
    public static final int FAILED = -1;
    public static final int NOT_STARTED = 0;
    public static final int OPTED_OUT = 1;
    public static final int STARTING = 2;
    public static final int STARTED = 3;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({
            NOT_STARTED,
            STARTING,
            STARTED,
            FAILED,
            OPTED_OUT
    })
    public @interface State {
    }
}
