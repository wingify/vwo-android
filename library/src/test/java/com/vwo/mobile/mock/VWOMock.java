package com.vwo.mobile.mock;

import com.vwo.mobile.VWO;
import com.vwo.mobile.VWOConfig;
import com.vwo.mobile.utils.VWOUrlBuilder;

import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.robolectric.RuntimeEnvironment;

/**
 * Created by aman on Tue 10/10/17 12:39.
 */

public class VWOMock {
    private VWO vwo;

    public VWOMock() {
        vwo = Mockito.mock(VWO.class);

        VWOUrlBuilder vwoUrlBuilder = Mockito.mock(VWOUrlBuilder.class);
        Mockito.when(vwoUrlBuilder.getCampaignUrl(ArgumentMatchers.anyLong(), ArgumentMatchers.anyInt())).thenReturn("http://dacdn.visualwebsiteoptimizer.com/mobile?a=295087&v=2.0.0-beta7&i=90d22643c5c730732cf5c48ba2cdcf85&dt=android&os=23&r=0.470000");

        VWOConfig vwoConfig = Mockito.mock(VWOConfig.class);
        Mockito.when(vwoConfig.getValueForCustomSegment("userType")).thenReturn("paid");

        Mockito.when(vwo.getCurrentContext()).thenReturn(RuntimeEnvironment.application);
        Mockito.when(vwo.getState()).thenReturn(1);
        Mockito.when(vwo.getConfig()).thenReturn(vwoConfig);
        Mockito.when(vwo.getVwoUrlBuilder()).thenReturn(vwoUrlBuilder);
    }

    public VWO getVWOMockObject() {
        return vwo;
    }
}
