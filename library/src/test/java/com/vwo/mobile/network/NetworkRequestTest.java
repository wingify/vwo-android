package com.vwo.mobile.network;

import android.os.Build;
import android.util.Log;

import com.vwo.mobile.BuildConfig;
import com.vwo.mobile.mock.ShadowNetworkRequest;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.IOException;
import java.net.InetAddress;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

/**
 * Created by aman on Sat 09/12/17 10:03.
 */

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.JELLY_BEAN,
        shadows = {ShadowNetworkRequest.class})
@PrepareForTest(NetworkRequest.class)
@PowerMockIgnore({"javax.net.ssl.*", "java.net.*"})
public class NetworkRequestTest {
    private MockWebServer mockWebServer;

    @Before
    public void startServer() throws Exception {
        mockWebServer = new MockWebServer();
        InetAddress inetAddress = InetAddress.getByName("127.0.0.1");
        mockWebServer.start(inetAddress, 6666);
    }

    @Test
    public void requestTest() throws Exception {
        MockResponse mockResponse = new MockResponse();
        mockResponse.setResponseCode(200);
        mockResponse.setBody("Test");
        mockWebServer.enqueue(mockResponse);

//        URL ur = PowerMockito.mock(URL.class);
//        PowerMockito.when(ur.toString()).thenReturn("http://www.abc.com");
//        PowerMockito.whenNew(URL.class).withArguments(ArgumentMatchers.anyString()).thenReturn(ur);
//        HttpURLConnection huc = PowerMockito.mock(HttpURLConnection.class);
//        PowerMockito.when(huc.getResponseCode()).thenReturn(200);

        String url = mockWebServer.url("/").toString();
        FutureNetworkRequest<String> futureNetworkRequest = FutureNetworkRequest.getInstance();
        NetworkStringRequest networkStringRequest = new NetworkStringRequest("http://abc.com", NetworkRequest.GET, futureNetworkRequest, futureNetworkRequest);
//        Uri ur = Uri.parse(networkStringRequest.getUrl());
//        ur.buildUpon().scheme("http").scheme(Uri.parse(url).getScheme());
//        networkStringRequest.setUrl(ur.toString());

        PriorityRequestQueue.getInstance().addToQueue(networkStringRequest);
        String data = futureNetworkRequest.get();
        Assert.assertEquals("Test", data);
        Log.d("Response", data);

    }

    @After
    public void killServer() throws IOException {
        mockWebServer.shutdown();
    }

}
