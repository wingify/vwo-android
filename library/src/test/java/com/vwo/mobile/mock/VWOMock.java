package com.vwo.mobile.mock;

import com.vwo.mobile.VWO;
import com.vwo.mobile.VWOConfig;

import org.mockito.Mockito;
import org.robolectric.RuntimeEnvironment;

/**
 * Created by aman on Tue 10/10/17 12:39.
 */

public class VWOMock {
    private VWO vwo;

    public VWOMock() {
        vwo = Mockito.mock(VWO.class);

        VWOConfig vwoConfig = Mockito.mock(VWOConfig.class);
        Mockito.when(vwoConfig.getValueForCustomSegment("userType")).thenReturn("paid");

        Mockito.when(vwo.getCurrentContext()).thenReturn(RuntimeEnvironment.application);
        Mockito.when(vwo.getState()).thenReturn(1);
        Mockito.when(vwo.getConfig()).thenReturn(vwoConfig);
    }

    public VWO getVWOMockObject() {
        return vwo;
    }
}
