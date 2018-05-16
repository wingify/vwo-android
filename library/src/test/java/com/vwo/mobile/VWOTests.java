package com.vwo.mobile;

/**
 * Created by aman on Wed 16/05/18 13:22.
 */

import com.vwo.mobile.utils.VWOUtils;

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
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 21)
@PrepareForTest({VWOUtils.class, VWO.class})
@PowerMockIgnore({"org.mockito.*", "org.robolectric.*", "android.*", "org.json.*", "com.vwo.mobile.utils.VWOLog"})
public class VWOTests {

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Test
    public void variationForKeyTest() {
        PowerMockito.mockStatic(VWOUtils.class);
        Mockito.when(VWOUtils.isValidVwoAppKey(ArgumentMatchers.anyString())).thenReturn(true);

        VWO.with(RuntimeEnvironment.application.getApplicationContext(), "adssafsdf-1234");
        PowerMockito.mockStatic(VWO.class);

        Mockito.when(VWO.getIntegerForKey(ArgumentMatchers.anyString(), ArgumentMatchers.anyInt())).thenReturn(20);

        System.out.println(VWO.getIntegerForKey("test", 1));

    }
}
