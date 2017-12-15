package com.vwo.mobile.data;

import android.content.Context;
import android.content.pm.PackageManager;

import com.vwo.mobile.BuildConfig;
import com.vwo.mobile.data.io.QueueFile;
import com.vwo.mobile.models.Entry;
import com.vwo.mobile.models.GoalEntry;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.io.File;
import java.io.IOException;

/**
 * Created by aman on Thu 14/12/17 11:09.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 16)
@PowerMockIgnore({"org.mockito.*", "org.robolectric.*", "android.*"})
@PrepareForTest({VWOMessageQueue.class})
public class VWOMessageQueueTest {
    VWOMessageQueue vwoMessageQueue;

    @Mock(name = "queueFile")
    QueueFile queueFile;

    @Mock
    Context context;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Before
    public void setup() throws IOException {
        MockitoAnnotations.initMocks(VWOMessageQueue.class);
        PackageManager packageManager = Mockito.mock(PackageManager.class);
//        mockStatic(IOUtils.class);
        File parent = temporaryFolder.getRoot();
        File file = new File(parent, "object-queue");
//        Mockito.when(IOUtils.getCacheDirectory(ArgumentMatchers.any(Context.class))).thenReturn(file);
//        PowerMockito.verifyStatic(IOUtils.class);
        queueFile = new QueueFile(file);
        vwoMessageQueue = VWOMessageQueue.getInstance(RuntimeEnvironment.application.getApplicationContext(), "test");
    }

    @Test
    public void addTest() throws IOException {
        GoalEntry goalEntry = new GoalEntry("http://www.abc.com", 1, 2, 3);
        GoalEntry goalEntry2 = new GoalEntry("https://www.abc1.com", 11, 21, 31);
        vwoMessageQueue.add(goalEntry);
        vwoMessageQueue.add(goalEntry2);

        Entry entry = vwoMessageQueue.peek();
        Assert.assertNotNull(entry);
        Assert.assertEquals(goalEntry.getUrl(), entry.getUrl());

//        byte[] data = queueFile.peek();
//        Assert.assertArrayEquals(test, data);
    }
}
