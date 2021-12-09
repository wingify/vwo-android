package com.vwo.mobile.network;

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
@Config(sdk = 22,
        shadows = {ShadowNetworkRequest.class})
@PrepareForTest(NetworkRequest.class)
@PowerMockIgnore({"javax.net.ssl.*", "java.net.*"})
public class NetworkRequestTest {
    private MockWebServer mockWebServer;

    @Before
    public void startServer() throws Exception {
        System.setProperty("javax.net.ssl.trustStore", "NONE");
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

        FutureNetworkRequest<String> futureNetworkRequest = FutureNetworkRequest.getInstance();
        NetworkStringRequest networkStringRequest = new NetworkStringRequest("http://abc.com", NetworkRequest.GET, futureNetworkRequest, futureNetworkRequest);

        PriorityRequestQueue.getInstance().addToQueue(networkStringRequest);
        String data = futureNetworkRequest.get();
        Assert.assertEquals("Test", data);
        System.out.println(data);

    }

    @After
    public void killServer() throws IOException {
        mockWebServer.shutdown();
    }

}
