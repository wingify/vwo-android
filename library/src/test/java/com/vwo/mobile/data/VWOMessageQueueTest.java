package com.vwo.mobile.data;

import com.vwo.mobile.models.Entry;
import com.vwo.mobile.models.GoalEntry;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.io.IOException;

/**
 * Created by aman on Thu 14/12/17 11:09.
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 22)
@PowerMockIgnore({"org.mockito.*", "org.robolectric.*", "android.*"})
@PrepareForTest({VWOMessageQueue.class})
public class VWOMessageQueueTest {
    private VWOMessageQueue vwoMessageQueue;

    private final Object lock = new Object();

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Before
    public void setup() throws IOException {
        MockitoAnnotations.initMocks(VWOMessageQueue.class);
        vwoMessageQueue = VWOMessageQueue.getInstance(RuntimeEnvironment.application.getApplicationContext(), "queue.vwo");
    }

    @Test
    public void insertTest() throws InterruptedException {
        GoalEntry goalEntry = new GoalEntry("http://www.abc.com", 1, 2, 3);

        vwoMessageQueue.add(goalEntry);
        System.out.println("Added: " + goalEntry.getUrl());

        synchronized (lock) {
            lock.wait(1000);
        }

        Entry entry = vwoMessageQueue.poll();
        Assert.assertNotNull(entry);
        Assert.assertEquals(entry.getUrl(), goalEntry.getUrl());
        System.out.println("Verified: " + goalEntry.getUrl());

        Assert.assertNull(vwoMessageQueue.peek());
    }

    @Test
    public void bulkInsertTest() throws InterruptedException {
        int count = 10000;
        for(int i = 0; i < count; i++) {
            GoalEntry goalEntry = new GoalEntry("http://www.abc" + i + ".com", 1, 2, 3);
            vwoMessageQueue.add(goalEntry);
            System.out.println("Added: " + goalEntry.getUrl());
        }
        GoalEntry goalEntry2 = new GoalEntry("https://www.abcFinal.com", 11, 21, 31);

        vwoMessageQueue.add(goalEntry2);

        synchronized (lock) {
            lock.wait(count * 2);
        }

        int size = vwoMessageQueue.size();
        Assert.assertEquals(10001, size);

        for(int i = 0; i < count; i++) {
            Entry entry = vwoMessageQueue.poll();
            Assert.assertNotNull(entry);
            Assert.assertEquals(entry.getUrl(), "http://www.abc" + i + ".com");
            System.out.println("Verified: " + entry.getUrl());
        }

        Entry entry = vwoMessageQueue.poll();
        Assert.assertNotNull(entry);
        Assert.assertEquals(entry.getUrl(), goalEntry2.getUrl());
    }

    @Test
    public void delayedInsertTest() throws InterruptedException {
        int count = 1000;
        for(int i = 0; i < count; i++) {
            GoalEntry goalEntry = new GoalEntry("http://www.abc" + i + ".com", 1, 2, 3);
            vwoMessageQueue.add(goalEntry);
            System.out.println("Added: " + goalEntry.getUrl());
            if(i % 20 == 0) {
                Thread.sleep(100);
            }
        }

        GoalEntry goalEntry2 = new GoalEntry("https://www.abcFinal.com", 11, 21, 31);

        vwoMessageQueue.add(goalEntry2);

        synchronized (lock) {
            lock.wait(count * 2);
        }

        int size = vwoMessageQueue.size();
        Assert.assertEquals(count + 1, size);

        for(int i = 0; i < count; i++) {
            Entry entry = vwoMessageQueue.poll();
            Assert.assertNotNull(entry);
            Assert.assertEquals(entry.getUrl(), "http://www.abc" + i + ".com");
            System.out.println("Verified: " + entry.getUrl());
        }

        Entry entry = vwoMessageQueue.poll();
        Assert.assertNotNull(entry);
        Assert.assertEquals(entry.getUrl(), goalEntry2.getUrl());
    }

    @Test
    public void bulkRemoveTest() throws InterruptedException {
        int count = 10000;
        for(int i = 0; i < count; i++) {
            GoalEntry goalEntry = new GoalEntry("http://www.abc" + i + ".com", 1, 2, 3);
            vwoMessageQueue.add(goalEntry);
            System.out.println("Added: " + goalEntry.getUrl());
        }

        GoalEntry goalEntry2 = new GoalEntry("https://www.abcFinal.com", 11, 21, 31);

        vwoMessageQueue.add(goalEntry2);

        synchronized (lock) {
            lock.wait(2000);
        }

        for(int i = 0; i < count; i++) {
            Entry entry = vwoMessageQueue.poll();
            Assert.assertNotNull(entry);
            Assert.assertEquals(entry.getUrl(), "http://www.abc" + i + ".com");
            System.out.println("Verified: " + entry.getUrl());
        }

        Entry entry = vwoMessageQueue.poll();
        Assert.assertNotNull(entry);
        Assert.assertEquals(entry.getUrl(), goalEntry2.getUrl());
    }

    @Test
    public void delayedInsertRemoveTest() throws InterruptedException {
        final int count = 100;
        Thread writeThread = new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i = 0; i < count; i++) {
                    GoalEntry goalEntry = new GoalEntry("http://www.abc" + i + ".com", 1, 2, 3);
                    vwoMessageQueue.add(goalEntry);
                    System.out.println("Added: " + goalEntry.getUrl());
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        Thread readThread = new Thread(new Runnable() {
            @Override
            public void run() {
                int readCounter = 0;
                while(readCounter < count) {
                    if(vwoMessageQueue.peek() == null) {
                        continue;
                    }
                    Entry entry = vwoMessageQueue.poll();
                    Assert.assertNotNull(entry);
                    Assert.assertEquals(entry.getUrl(), "http://www.abc" + readCounter + ".com");
                    System.out.println("Verified: " + entry.getUrl());
                    if(readCounter % 20 == 0) {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    readCounter++;
                }

                synchronized (lock) {
                    lock.notify();
                }

            }
        });

        writeThread.start();
        readThread.start();

        synchronized (lock) {
            lock.wait();
        }
    }

    @Test
    public void insertRemoveTest() throws InterruptedException {
        final int count = 10000;
        Thread writeThread = new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i = 0; i < count; i++) {
                    GoalEntry goalEntry = new GoalEntry("http://www.abc" + i + ".com", 1, 2, 3);
                    vwoMessageQueue.add(goalEntry);
                    System.out.println("Added: " + goalEntry.getUrl());
                }
            }
        });

        Thread readThread = new Thread(new Runnable() {
            @Override
            public void run() {
                int readCounter = 0;
                while(readCounter < count) {
                    if(vwoMessageQueue.peek() == null) {
                        continue;
                    }
                    Entry entry = vwoMessageQueue.poll();
                    Assert.assertNotNull(entry);
                    Assert.assertEquals(entry.getUrl(), "http://www.abc" + readCounter + ".com");
                    System.out.println("Verified: " + entry.getUrl());
                    readCounter++;
                }

                synchronized (lock) {
                    lock.notify();
                }

            }
        });

        readThread.start();
        writeThread.start();

        synchronized (lock) {
            lock.wait();
        }
    }

    @Test
    public void peekTest() throws InterruptedException {
        GoalEntry goalEntry = new GoalEntry("http://www.abc.com", 1, 2, 3);
        vwoMessageQueue.add(goalEntry);

        vwoMessageQueue.add(goalEntry);
        System.out.println("Added: " + goalEntry.getUrl());

        synchronized (lock) {
            lock.wait(2000);
        }

        Entry entry = vwoMessageQueue.peek();
        Assert.assertNotNull(entry);
        Assert.assertEquals(entry.getUrl(), goalEntry.getUrl());
        System.out.println("Verified: " + entry.getUrl());

    }
}
