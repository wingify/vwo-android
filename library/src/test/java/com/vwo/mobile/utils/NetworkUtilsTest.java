package com.vwo.mobile.utils;

import com.vwo.mobile.BuildConfig;

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
@Config(constants = BuildConfig.class, sdk = Config.ALL_SDKS)
public class NetworkUtilsTest {
    @Test
    public void headersTest() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Date", "Thu, 04 Jan 2018 10:21:27 GMT");
        headers.put("Content-Type", "application/json; charset=UTF-8");
        headers.put("Connection", "keep-alive");
        headers.put("server", "sjc1");

        Assert.assertEquals(NetworkUtils.Headers.parseCharset(headers), "utf-8");

        headers.remove("Content-Type");
        Assert.assertEquals(NetworkUtils.Headers.parseCharset(headers), "utf-8");
    }
}
