package com.vwo.mobile.data;

import android.Manifest;
import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;

import com.vwo.mobile.data.io.QueueFile;
import com.vwo.mobile.models.Entry;
import com.vwo.mobile.utils.VWOLog;
import com.vwo.mobile.utils.VWOUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by aman on 18/09/17.
 */

public class VWOMessageQueue {

    private static final String FILE_QUEUE_NAME = "queue_v1.vwo";
    private static VWOMessageQueue vwoMessageQueueInstance;
    private Context mContext;
    private QueueFile queueFile;

    private VWOMessageQueue(Context context) throws IOException {
        this.mContext = context;
        File file = new File(getCacheDirectory(), FILE_QUEUE_NAME);
        queueFile = new QueueFile.Builder(file).build();
    }

    public static VWOMessageQueue getInstance(Context context) throws IOException {
        if(vwoMessageQueueInstance == null) {
            vwoMessageQueueInstance = new VWOMessageQueue(context);
        }
        return vwoMessageQueueInstance;
    }

    /**
     * Add to queue.
     *
     * @param entry the queue entry
     */
    public void add(Entry entry) {
        VWOLog.i(VWOLog.STORAGE_LOGS, "Adding to queue, Entry : " + entry.toString(), true);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream;
        try {
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(entry);
            objectOutputStream.flush();
            queueFile.add(byteArrayOutputStream.toByteArray());
        } catch (IOException exception) {
            VWOLog.e(VWOLog.STORAGE_LOGS, "Unable to create Object", exception, true, true);
        } finally {
            try {
                byteArrayOutputStream.close();
            } catch (Exception exception) {
                VWOLog.e(VWOLog.STORAGE_LOGS, exception, true, true);
            }
        }
    }

    @Nullable
    public Entry peek() {
        try {
            byte[] data = queueFile.peek();
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
            try {
                return (Entry) objectInputStream.readObject();
            } catch (ClassNotFoundException exception) {
                VWOLog.e(VWOLog.STORAGE_LOGS, exception, true, true);
            }
        } catch (IOException exception) {
            VWOLog.e(VWOLog.STORAGE_LOGS, exception, true, false);
        }

        return null;
    }

    public void removeAll() {
        try {
            queueFile.clear();
        } catch (IOException exception) {
            VWOLog.e(VWOLog.STORAGE_LOGS, "Failed to remove elements from queue.", exception,
                    true, false);
        }
    }

    private File getCacheDirectory() {
        File dir;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            if (VWOUtils.checkForPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                dir = mContext.getExternalFilesDir(null);
            } else {
                dir = mContext.getCacheDir();
            }
        } else {
            dir = mContext.getExternalFilesDir(null);
        }

        return dir;
    }

    public void remove() {
        try {
            queueFile.remove();
            VWOLog.i(VWOLog.STORAGE_LOGS, "Removed top element from queue", true);
        } catch (IOException exception) {
            VWOLog.e(VWOLog.STORAGE_LOGS, "Failed to remove top element from queue.", exception,
                    true, false);
        }
    }

    public int getSize() {
        return queueFile.size();
    }

    public void pushToTop(Entry entry) {
        remove();
        add(entry);
    }
}
