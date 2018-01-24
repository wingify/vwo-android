package com.vwo.mobile.models;

import com.vwo.mobile.utils.Parceler;

import junit.framework.Assert;

import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

/**
 * Created by aman on Fri 19/01/18 18:21.
 */

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 22)
public class VWOErrorTest {

    @Test
    public void test() throws JSONException {
        VWOError.Builder builder = new VWOError.Builder(RuntimeEnvironment.application.getApplicationContext(),
                "http://www.abc.com", System.currentTimeMillis());

        builder.exception(new Exception("This is a test exception"));
        VWOError inData = builder.build();

        byte[] parceledData = Parceler.marshall(inData);

        VWOError outData = Parceler.unmarshall(parceledData, VWOError.CREATOR);

        Assert.assertEquals(outData.getErrorAsJSON().toString(), inData.getErrorAsJSON().toString());
    }
}
