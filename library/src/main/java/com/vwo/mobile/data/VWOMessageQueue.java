package com.vwo.mobile.data;

import android.content.Context;
import android.support.annotation.Nullable;

import com.vwo.mobile.data.io.QueueFile;
import com.vwo.mobile.models.Entry;
import com.vwo.mobile.utils.VWOLog;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Locale;

/**
 * Created by aman on 18/09/17.
 */

public class VWOMessageQueue implements MessageQueue<Entry> {

    private QueueFile queueFile;
    private String filename;

    private VWOMessageQueue(Context context, String fileName) throws IOException {
        File file = new File(IOUtils.getCacheDirectory(context), fileName);
        try {
            queueFile = new QueueFile(file);
            this.filename = fileName;
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
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                VWOLog.i(VWOLog.STORAGE_LOGS, String.format(Locale.ENGLISH, "Adding to queue %s\n%s", filename, entry.toString()), true);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ObjectOutputStream objectOutputStream;
                try {
                    objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
                    objectOutputStream.writeObject(entry);
                    objectOutputStream.flush();
                    queueFile.add(byteArrayOutputStream.toByteArray());
                } catch (IOException exception) {
                    VWOLog.e(VWOLog.STORAGE_LOGS, String.format(Locale.ENGLISH, "File %s corrupted. Clearing last entry...", filename), true, false);
                    remove();
                    VWOLog.e(VWOLog.STORAGE_LOGS, "Unable to create Object", exception, true, true);
                } finally {
                    try {
                        byteArrayOutputStream.close();
                    } catch (Exception exception) {
                        VWOLog.e(VWOLog.STORAGE_LOGS, exception, true, true);
                    }
                }
            }
        });

        thread.start();

    }

    @Override
    @Nullable
    public Entry peek() {
        VWOLog.i(VWOLog.STORAGE_LOGS, String.format(Locale.ENGLISH, "Reading from queue %s", this.filename), true);
        try {
            byte[] data = queueFile.peek();
            if (data == null) {
                return null;
            }
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
            try {
                return (Entry) objectInputStream.readObject();
            } catch (ClassNotFoundException exception) {
                VWOLog.e(VWOLog.STORAGE_LOGS, exception, true, true);
            }
        } catch (IOException exception) {
            VWOLog.e(VWOLog.STORAGE_LOGS, String.format(Locale.ENGLISH, "File %s corrupted. Clearing last entry...", filename), true, false);
            remove();
            VWOLog.e(VWOLog.STORAGE_LOGS, exception, true, false);
        }

        return null;
    }

    @Override
    public void removeAll() {
        VWOLog.i(VWOLog.STORAGE_LOGS, String.format(Locale.ENGLISH, "Emptying queue %s", this.filename), true);
        try {
            queueFile.clear();
        } catch (IOException exception) {
            VWOLog.e(VWOLog.STORAGE_LOGS, "Unable to clear corrupted file data..", exception, true, false);
        }
    }

    @Override
    public void remove() {
        VWOLog.i(VWOLog.STORAGE_LOGS, String.format(Locale.ENGLISH, "Removing top element from queue %s", this.filename), true);
        try {
            queueFile.remove();
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
        return queueFile.size();
    }

    @Override
    public String toString() {
        return String.format(Locale.ENGLISH, "Tag: %s\nSize: %d", this.filename, size());
    }
}
