package com.vwo.mobile.mock;

import com.vwo.mobile.VWO;

import org.mockito.Mockito;
import org.robolectric.RuntimeEnvironment;

/**
 * Created by aman on Tue 10/10/17 12:39.
 */

public class VWOMock {
    private VWO vwo;

    public VWOMock() {
        vwo = Mockito.mock(VWO.class);

//        Mockito.when(vwo.getConfig().getValueForCustomSegment("key")).thenReturn("value");
        Mockito.when(vwo.getCurrentContext()).thenReturn(RuntimeEnvironment.application);
        Mockito.when(vwo.getState()).thenReturn(1);
    }

    public VWO getVWOMockObject() {
        return vwo;
    }
}
