package com.vwo.mobile.mock;

import com.vwo.mobile.VWO;
import com.vwo.mobile.data.VWOPersistData;
import com.vwo.mobile.utils.VWOPreference;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;

/**
 * Created by aman on Tue 10/10/17 16:42.
 */

@Implements(value = VWOPersistData.class)
public class VWOPersistDataShadow {

    @Implementation
    public static boolean isReturningUser(VWO vwo) {
        return true;
    }

    @Implementation
    void saveCampaign(VWOPreference sharedPreference) {

    }
}
