package com.vwo.mobile.data;

import android.content.Context;

import com.vwo.mobile.TestUtils;
import com.vwo.mobile.VWO;
import com.vwo.mobile.mock.ShadowVWOLog;
import com.vwo.mobile.mock.VWOMock;
import com.vwo.mobile.mock.VWOPersistDataShadow;
import com.vwo.mobile.utils.VWOPreference;
import com.vwo.mobile.utils.VWOUrlBuilder;
import com.vwo.mobile.utils.VWOUtils;

import junit.framework.Assert;

import org.json.JSONArray;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.io.IOException;
import java.util.HashMap;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;

/**
 * Created by aman on Wed 10/01/18 13:34.
 */

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 21, shadows = {VWOPersistDataShadow.class, ShadowVWOLog.class})
@PowerMockIgnore({"org.mockito.*", "org.robolectric.*", "android.*", "org.json.*",
        "com.vwo.mobile.utils.VWOLog"})
@PrepareForTest({VWOUtils.class, VWOPersistData.class, VWOMessageQueue.class})
public class VWODataTest {

    private final Object lock = new Object();

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    private VWO vwo;


    @Before
    public void setup() throws IOException {
        vwo = new VWOMock().getVWOMockObject();

        PowerMockito.mockStatic(VWOUtils.class);
        PowerMockito.when(VWOUtils.applicationVersion(any(Context.class))).thenReturn(1);
        PowerMockito.when(VWOUtils.androidVersion()).thenReturn("21");

        PowerMockito.mockStatic(VWOPersistData.class);
        PowerMockito.when(VWOPersistData.isExistingCampaign(ArgumentMatchers.any(VWO.class), ArgumentMatchers.anyString())).thenReturn(false);

        VWOMessageQueue vwoMessageQueue = VWOMessageQueue.getInstance(RuntimeEnvironment.application.getApplicationContext(), "queue.vwo");

        Mockito.when(vwo.getMessageQueue()).thenReturn(vwoMessageQueue);

        VWOUrlBuilder vwoUrlBuilder = Mockito.mock(VWOUrlBuilder.class);
        Mockito.when(vwoUrlBuilder.getCampaignUrl(anyLong(), anyInt()))
                .thenReturn("https://dacdn.visualwebsiteoptimizer.com/track-user?experiment_id=14&account_id=295087&combination=2&u=68fde50e3c26412c86c05c61f0d4917b&s=1&random=0.32&ed=%7B%22lt%22%3A1515580228%2C%22v%22%3A14%2C%22ai%22%3A%2290d22643c5c730732cf5c48ba2cdcf85%22%2C%22av%22%3A1%2C%22dt%22%3A%22android%22%2C%22os%22%3A%2226%22%7D");
        Mockito.when(vwoUrlBuilder.getGoalUrl(anyLong(), anyInt(), anyInt()))
                .thenReturn("https://dacdn.visualwebsiteoptimizer.com/track-goal?experiment_id=14&account_id=295087&combination=2&u=193e45b8608c4821868a0e7162e0938f&s=7&random=0.55&goal_id=351&ed=%7B%22lt%22%3A1515579976%2C%22v%22%3A14%2C%22ai%22%3A%2290d22643c5c730732cf5c48ba2cdcf85%22%2C%22av%22%3A1%2C%22dt%22%3A%22android%22%2C%22os%22%3A%2226%22%7D");
        Mockito.when(vwoUrlBuilder.getGoalUrl(anyLong(), anyInt(), anyInt(), anyFloat()))
                .thenReturn("http://dacdn.visualwebsiteoptimizer.com/track-goal?experiment_id=14&account_id=295087&combination=2&u=a2c7a1df793b43b08ff8e55cd5faf6e6&s=1&random=0.93&goal_id=2&ed=%7B%22lt%22%3A1515584290%2C%22v%22%3A14%2C%22ai%22%3A%2290d22643c5c730732cf5c48ba2cdcf85%22%2C%22av%22%3A1%2C%22dt%22%3A%22android%22%2C%22os%22%3A%2226%22%7D&r=399.0");

        Mockito.when(vwo.getVwoUrlBuilder()).thenReturn(vwoUrlBuilder);
    }

    @Test
    public void variationForKeyTest() throws IOException, JSONException {
        vwo.getMessageQueue().removeAll();

        final HashMap<String, String> savedCampaignMap = new HashMap<>();

        VWOPreference vwoPreference = Mockito.mock(VWOPreference.class);

        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) {
                String argument1 = invocation.getArgument(0);
                String argument2 = invocation.getArgument(1);
                savedCampaignMap.put(argument1, argument2);
                return null;
            }
        }).when(vwoPreference).putString(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
        Mockito.when(vwo.getVwoPreference()).thenReturn(vwoPreference);

        String data = TestUtils.readJsonFile(getClass(), "com/vwo/mobile/data/campaigns.json");

        VWOData vwoData = new VWOData(vwo);
        vwoData.parseData(new JSONArray(data));

        Assert.assertEquals(vwoData.getVariationForKey("layout"), "grid");
        Assert.assertEquals(vwoData.getVariationForKey("socialMedia"), true);
        Assert.assertEquals("{\"campaignId\":14,\"variationId\":2,\"goals\":[]}", savedCampaignMap.get("campaign_14"));
    }

    @Test
    public void saveGoalTest() throws IOException, JSONException, InterruptedException {
        vwo.getMessageQueue().removeAll();
        final HashMap<String, String> savedCampaignMap = new HashMap<>();

        String campaignData = "{\"campaignId\":14,\"variationId\":2,\"goals\":[]}";

        VWOPreference vwoPreference = Mockito.mock(VWOPreference.class);
        Mockito.when(vwoPreference.isPartOfCampaign(anyString())).thenReturn(true);

        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) {
                String argument1 = invocation.getArgument(0);
                String argument2 = invocation.getArgument(1);
                savedCampaignMap.put(argument1, argument2);
                return null;
            }
        }).when(vwoPreference).putString(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());


        Mockito.when(vwoPreference.getString(ArgumentMatchers.eq("campaign_14"))).thenReturn(campaignData);
        Mockito.when(vwo.getVwoPreference()).thenReturn(vwoPreference);

        String data = TestUtils.readJsonFile(getClass(), "com/vwo/mobile/data/campaigns.json");
        VWOData vwoData = new VWOData(vwo);
        vwoData.parseData(new JSONArray(data));

        vwoData.saveGoal("goal");

        synchronized (lock) {
            lock.wait(1000);
        }

        Assert.assertEquals("{\"campaignId\":14,\"variationId\":2,\"goals\":[349]}", savedCampaignMap.get("campaign_14"));
        Assert.assertEquals("https://dacdn.visualwebsiteoptimizer.com/track-goal?experiment_id=14&account_id=295087&combination=2&u=193e45b8608c4821868a0e7162e0938f&s=7&random=0.55&goal_id=351&ed=%7B%22lt%22%3A1515579976%2C%22v%22%3A14%2C%22ai%22%3A%2290d22643c5c730732cf5c48ba2cdcf85%22%2C%22av%22%3A1%2C%22dt%22%3A%22android%22%2C%22os%22%3A%2226%22%7D", vwo.getMessageQueue().poll().getUrl());
    }

    @Test
    public void saveRevenueGoalTest() throws IOException, JSONException, InterruptedException {
        vwo.getMessageQueue().removeAll();
        final HashMap<String, String> savedCampaignMap = new HashMap<>();

        String campaignData = "{\"campaignId\":14,\"variationId\":2,\"goals\":[]}";

        VWOPreference vwoPreference = Mockito.mock(VWOPreference.class);
        Mockito.when(vwoPreference.isPartOfCampaign(anyString())).thenReturn(true);

        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) {
                String argument1 = invocation.getArgument(0);
                String argument2 = invocation.getArgument(1);
                savedCampaignMap.put(argument1, argument2);
                return null;
            }
        }).when(vwoPreference).putString(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());


        Mockito.when(vwoPreference.getString(ArgumentMatchers.eq("campaign_14"))).thenReturn(campaignData);
        Mockito.when(vwo.getVwoPreference()).thenReturn(vwoPreference);


        String data = TestUtils.readJsonFile(getClass(), "com/vwo/mobile/data/campaigns.json");
        VWOData vwoData = new VWOData(vwo);
        vwoData.parseData(new JSONArray(data));

        vwoData.saveGoal("goal", 10);

        synchronized (lock) {
            lock.wait(1000);
        }

        Assert.assertEquals("{\"campaignId\":14,\"variationId\":2,\"goals\":[349]}", savedCampaignMap.get("campaign_14"));

        Assert.assertEquals("http://dacdn.visualwebsiteoptimizer.com/track-goal?experiment_id=14&account_id=295087&combination=2&u=a2c7a1df793b43b08ff8e55cd5faf6e6&s=1&random=0.93&goal_id=2&ed=%7B%22lt%22%3A1515584290%2C%22v%22%3A14%2C%22ai%22%3A%2290d22643c5c730732cf5c48ba2cdcf85%22%2C%22av%22%3A1%2C%22dt%22%3A%22android%22%2C%22os%22%3A%2226%22%7D&r=399.0", vwo.getMessageQueue().poll().getUrl());
    }

    @Test
    public void trackUserManuallyTest() throws IOException, JSONException, InterruptedException {
        vwo.getMessageQueue().removeAll();
        final HashMap<String, String> savedCampaignMap = new HashMap<>();

        String campaignData = "{\"campaignId\":14,\"variationId\":2,\"goals\":[]}";

        VWOPreference vwoPreference = Mockito.mock(VWOPreference.class);
        Mockito.when(vwoPreference.isPartOfCampaign(anyString())).thenReturn(false);

        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) {
                String argument1 = invocation.getArgument(0);
                String argument2 = invocation.getArgument(1);
                savedCampaignMap.put(argument1, argument2);
                return null;
            }
        }).when(vwoPreference).putString(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());


        Mockito.when(vwoPreference.getString(ArgumentMatchers.eq("campaign_14"))).thenReturn(campaignData);
        Mockito.when(vwo.getVwoPreference()).thenReturn(vwoPreference);
        String data = TestUtils.readJsonFile(getClass(), "com/vwo/mobile/data/track_user_manually.json");
        VWOData vwoData = new VWOData(vwo);
        vwoData.parseData(new JSONArray(data));

        Assert.assertEquals(vwo.getMessageQueue().size(), 0);

        vwoData.getVariationForKey("layout");
        synchronized (lock) {
            lock.wait(3000);
        }
        Assert.assertEquals(vwo.getMessageQueue().size(), 1);
        Assert.assertEquals(vwo.getMessageQueue().poll().getUrl(),
                "https://dacdn.visualwebsiteoptimizer.com/track-user?experiment_id=14&account_id=295087&combination=2&u=68fde50e3c26412c86c05c61f0d4917b&s=1&random=0.32&ed=%7B%22lt%22%3A1515580228%2C%22v%22%3A14%2C%22ai%22%3A%2290d22643c5c730732cf5c48ba2cdcf85%22%2C%22av%22%3A1%2C%22dt%22%3A%22android%22%2C%22os%22%3A%2226%22%7D");
    }


    @Test
    public void multipleCampaignsTest() throws IOException, JSONException, InterruptedException {
        vwo.getMessageQueue().removeAll();
        final HashMap<String, String> savedCampaignMap = new HashMap<>();

        String campaignData = "{\"campaignId\":14,\"variationId\":2,\"goals\":[]}";

        VWOPreference vwoPreference = Mockito.mock(VWOPreference.class);
        Mockito.when(vwoPreference.isPartOfCampaign(anyString())).thenReturn(false);

        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) {
                String argument1 = invocation.getArgument(0);
                String argument2 = invocation.getArgument(1);
                savedCampaignMap.put(argument1, argument2);
                return null;
            }
        }).when(vwoPreference).putString(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());


        Mockito.when(vwoPreference.getString(ArgumentMatchers.eq("campaign_14"))).thenReturn(campaignData);
        Mockito.when(vwo.getVwoPreference()).thenReturn(vwoPreference);
        String data = TestUtils.readJsonFile(getClass(), "com/vwo/mobile/data/multiple_campaigns.json");
        VWOData vwoData = new VWOData(vwo);
        vwoData.parseData(new JSONArray(data));

        synchronized (lock) {
            lock.wait(3000);
        }
        Assert.assertEquals(vwo.getMessageQueue().size(), 1);

        vwoData.getVariationForKey("layout");
        synchronized (lock) {
            lock.wait(3000);
        }
        Assert.assertEquals(vwo.getMessageQueue().size(), 2);
    }
}
