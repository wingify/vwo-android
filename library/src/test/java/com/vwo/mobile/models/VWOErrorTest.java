package com.vwo.mobile.models;

import com.vwo.mobile.utils.Serializer;

import junit.framework.Assert;

import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.IOException;

/**
 * Created by aman on Fri 19/01/18 18:21.
 */

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 22)
public class VWOErrorTest {

    @Test
    public void test() throws JSONException, IOException, ClassNotFoundException {
        VWOError.Builder builder = new VWOError.Builder("http://www.abc.com", System.currentTimeMillis());

        builder.exception(new Exception("This is a test exception"));
        VWOError inData = builder.build();

        byte[] parceledData = Serializer.marshall(inData);

        VWOError outData = Serializer.unmarshall(parceledData, VWOError.class);

        Assert.assertEquals(outData.getErrorAsJSON().toString(), inData.getErrorAsJSON().toString());
    }
}
