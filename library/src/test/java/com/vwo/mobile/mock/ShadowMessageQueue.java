package com.vwo.mobile.mock;

import android.os.Build;

import com.vwo.mobile.BuildConfig;

import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

/**
 * Created by aman on Thu 14/12/17 11:08.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.JELLY_BEAN)
public class ShadowMessageQueue {

}
