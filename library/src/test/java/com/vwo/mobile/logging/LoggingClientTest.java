package com.vwo.mobile.logging;

import android.content.Context;

import com.vwo.mobile.VWO;
import com.vwo.mobile.data.VWOMessageQueue;
import com.vwo.mobile.listeners.VWOActivityLifeCycle;
import com.vwo.mobile.mock.ShadowVWOLog;
import com.vwo.mobile.mock.VWOMock;
import com.vwo.mobile.utils.NetworkUtils;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

/**
 * Created by aman on Fri 19/01/18 19:25.
 */

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 22, shadows = {ShadowVWOLog.class})
@PrepareForTest({VWOActivityLifeCycle.class, NetworkUtils.class, VWOLoggingClient.class})
@PowerMockIgnore({"org.mockito.*", "org.robolectric.*", "android.*", "org.json.*",
        "com.vwo.mobile.utils.VWOLog"})
public class LoggingClientTest {

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    @Test
    public void sendLogTest() {
        VWO vwo = new VWOMock().getVWOMockObject();
        VWOMessageQueue messageQueue = Mockito.mock(VWOMessageQueue.class);
        VWOLoggingClient vwoLoggingClient = Mockito.mock(VWOLoggingClient.class);

        PowerMockito.mockStatic(VWOActivityLifeCycle.class);
        PowerMockito.when(VWOActivityLifeCycle.isApplicationInForeground()).thenReturn(true);
        PowerMockito.mockStatic(NetworkUtils.class);
        PowerMockito.when(NetworkUtils.shouldAttemptNetworkCall(ArgumentMatchers.any(Context.class))).thenReturn(true);

        VWOLoggingClient client = VWOLoggingClient.getInstance();
        client.init(vwo, null);

        VWOLoggingClient.log(new Exception("This is a test Exception"));
    }
}
