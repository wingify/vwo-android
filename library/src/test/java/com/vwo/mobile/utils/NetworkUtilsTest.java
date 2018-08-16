package com.vwo.mobile.utils;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by aman on Thu 04/01/18 15:53.
 */

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 22)
public class NetworkUtilsTest {
    @Test
    public void headersTest() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Date", "Thu, 04 Jan 2018 10:21:27 GMT");
        headers.put("Content-Type", "application/json; charset=UTF-8");
        headers.put("Connection", "keep-alive");
        headers.put("server", "sjc1");

        Assert.assertEquals("UTF-8", NetworkUtils.Headers.parseCharset(headers));

        headers.remove("Content-Type");
        Assert.assertEquals("UTF-8", NetworkUtils.Headers.parseCharset(headers));
    }
}
