package com.vwo.mobile.models;

import android.os.Build;

import com.vwo.mobile.BuildConfig;

import junit.framework.Assert;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

/**
 * Created by aman on Thu 04/01/18 12:32.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.JELLY_BEAN)
public class GoalTest {

    @Test
    public void testGoal() throws JSONException {
        String data = "{\n" +
                "        \"id\": 349,\n" +
                "        \"identifier\": \"goal\",\n" +
                "        \"type\": \"REVENUE_TRACKING\"\n" +
                "      }";
        Goal goal = new Goal(new JSONObject(data));
        Assert.assertEquals(goal.getId(), 349);
    }
}
