package com.vwo.mobile.models;

import com.vwo.mobile.TestUtils;

import junit.framework.Assert;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.IOException;

/**
 * Created by aman on Thu 04/01/18 12:32.
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 22)
public class GoalTest {

    @Test
    public void testGoal() throws JSONException, IOException {
        String data = TestUtils.readJsonFile(getClass(), "com/vwo/mobile/models/goal.json");
        Goal goal = new Goal(new JSONObject(data));
        Assert.assertEquals(goal.getId(), 349);
    }
}
