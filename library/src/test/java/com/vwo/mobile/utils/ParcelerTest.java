package com.vwo.mobile.utils;

import android.os.Parcel;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by aman on Fri 19/01/18 18:05.
 */

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 22)
public class ParcelerTest {

    @Test
    public void readWriteMapTest() {
        HashMap<String, String> map = new HashMap<>();
        map.put("abc", "def");
        map.put("ghi", "jkl");
        map.put("uvw", "xyz");
        map.put("null", null);

        Parcel parcel = Parcel.obtain();
        parcel.recycle();
        Parceler.writeStringMapToParcel(map, parcel);
        parcel.setDataPosition(0);

        Map<String, String> unparceledData = Parceler.readStringMapFromParcel(parcel);

        Assert.assertEquals(map.size(), unparceledData.size());
        Assert.assertEquals(unparceledData.get("uvw"), "xyz");
        Assert.assertNull(unparceledData.get("null"));
    }
}
