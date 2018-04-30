package com.vwo.mobile.data;

import android.content.Context;
import android.support.annotation.Nullable;

import com.vwo.mobile.data.io.QueueFile;
import com.vwo.mobile.models.Entry;
import com.vwo.mobile.utils.Serializer;
import com.vwo.mobile.utils.VWOLog;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by aman on 18/09/17.
 */

public class VWOMessageQueue implements MessageQueue<Entry> {

    private final QueueFile queueFile;
    private String filename;
    private Queue<Entry> waitingQueue;
    private ExecutorService executorService;
    private Thread thread;

    private VWOMessageQueue(Context context, String fileName) throws IOException {
        File file = new File(IOUtils.getCacheDirectory(context), fileName);
        try {
            queueFile = new QueueFile(file);
            this.filename = fileName;
            this.waitingQueue = new ConcurrentLinkedQueue<>();
            executorService = Executors.newSingleThreadExecutor();

            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (!waitingQueue.isEmpty()) {
                        Entry entry = waitingQueue.poll();
                        VWOLog.i(VWOLog.STORAGE_LOGS, String.format(Locale.ENGLISH, "Adding to queue %s\n%s", filename, entry.toString()), true);
                        try {
                            byte[] data = Serializer.marshall(entry);

                            synchronized (queueFile) {
                                queueFile.add(data);
                            }
                        } catch (IOException exception) {
                            VWOLog.e(VWOLog.STORAGE_LOGS, String.format(Locale.ENGLISH, "File %s corrupted. Clearing last entry...", filename), true, false);
                            remove();
                            VWOLog.e(VWOLog.STORAGE_LOGS, "Unable to create Object", exception, true, true);
                        }
                    }
                }
            });
        } catch (IOException exception) {
            VWOLog.e(VWOLog.STORAGE_LOGS, "Failed to initialize queue: " + fileName, exception,
                    false, true);
            throw exception;
        }
    }

    public static VWOMessageQueue getInstance(Context context, String fileName) throws IOException {
        return new VWOMessageQueue(context, fileName);
    }

    /**
     * Add to queue.
     *
     * @param entry the queue entry
     */
    @Override
    public void add(final Entry entry) {
        waitingQueue.add(entry);
        startInsertionThread();
    }

    @Override
    @Nullable
    public Entry peek() {
        VWOLog.i(VWOLog.STORAGE_LOGS, String.format(Locale.ENGLISH, "Reading from queue %s", this.filename), true);
        try {
            byte[] data;
            synchronized (queueFile) {
                data = queueFile.peek();
            }
            if (data == null) {
                return null;
            }
            return Serializer.unmarshall(data, Entry.class);
        } catch (IOException exception) {
            VWOLog.e(VWOLog.STORAGE_LOGS, "Entry corrupted. Removing..", exception,
                    true, true);
            remove();
        } catch (ClassNotFoundException exception) {
            VWOLog.e(VWOLog.STORAGE_LOGS, "Entry corrupted. Removing...", exception,
                    true, true);
            remove();
        } catch (Exception exception) {
            VWOLog.e(VWOLog.STORAGE_LOGS, "Entry corrupted. Removing...", exception,
                    true, true);
            remove();
        }
        return null;
    }

    @Override
    public void removeAll() {
        VWOLog.i(VWOLog.STORAGE_LOGS, String.format(Locale.ENGLISH, "Emptying queue %s", this.filename), true);
        try {
            synchronized (queueFile) {
                queueFile.clear();
            }
        } catch (IOException exception) {
            VWOLog.e(VWOLog.STORAGE_LOGS, "Unable to clear corrupted file data..", exception, true, false);
        }
    }

    @Override
    public void remove() {
        VWOLog.i(VWOLog.STORAGE_LOGS, String.format(Locale.ENGLISH, "Removing top element from queue %s", this.filename), true);
        try {
            synchronized (queueFile) {
                queueFile.remove();
            }
            VWOLog.i(VWOLog.STORAGE_LOGS, "Removed top element from queue", true);
        } catch (IOException exception) {
            VWOLog.e(VWOLog.STORAGE_LOGS, String.format(Locale.ENGLISH, "File %s corrupted. Clearing file data...", filename), true, false);
            removeAll();
            VWOLog.e(VWOLog.STORAGE_LOGS, "Failed to remove top element from queue.", exception,
                    true, false);
        }
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String fileName) {
        this.filename = fileName;
    }

    @Override
    public int size() {
        synchronized (queueFile) {
            return queueFile.size();
        }
    }

    @Override
    public String toString() {
        return String.format(Locale.ENGLISH, "Tag: %s\nSize: %d", this.filename, size());
    }

    /**
     * Retrieves and removes the head of this queue, or returns null if this queue is empty.
     *
     * @return the element {@link Entry}
     */
    @Override
    public Entry poll() {
        Entry entry = peek();
        remove();
        return entry;
    }

    private void startInsertionThread() {
        executorService.execute(thread);
    }
}
