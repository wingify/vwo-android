package com.vwo.mobile.data.io;

import com.vwo.mobile.utils.VWOLog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Iterator;

/**
 * Created by aman on 11/09/17.
 */

public class FileBuffer implements Buffer<Entry> {

    private int maxEntries;
    private File directory;

    public static final String FILE_EXT = ".vwo";

    public FileBuffer(File directory, int maxEntries) {
        this.directory = directory;
        this.maxEntries = maxEntries;

        String errMessage = "Could not write data to persistent storage directory : " + directory.getAbsolutePath();
        try {
            if(directory.mkdirs()) {
                if(!directory.isDirectory() || !directory.canWrite()) {
                    throw new RuntimeException(errMessage);
                }
            } else {
                VWOLog.e(VWOLog.STORAGE_LOGS, errMessage, false, false);
            }
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    /**
     * @param entry is the object to be added to the buffer
     */
    @Override
    public boolean add(Entry entry) {
        if(getStoredEntriesCount() >= maxEntries) {
            VWOLog.e(VWOLog.STORAGE_LOGS, "Not storing this entry", true, false);
            return false;
        }

        File fileEntry = new File(directory.getAbsolutePath(), entry.getId() + FILE_EXT);
        if(fileEntry.exists()) {
            VWOLog.e(VWOLog.STORAGE_LOGS, "Duplicate event entry", true, false);
            return false;
        }

        VWOLog.d(VWOLog.STORAGE_LOGS, "Added entry to disk buffer.", true);

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(fileEntry);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(entry);
            return true;
        } catch (Exception exception) {
            VWOLog.e(VWOLog.STORAGE_LOGS, "Unable to write entry to file", exception, true, false);
        }

        return false;
    }

    /**
     * @param entry is the object to be removed from the buffer
     */
    @Override
    public boolean remove(Entry entry) {
        File file = new File(directory, entry.getId() + FILE_EXT);
        if(file.exists()) {
            VWOLog.i(VWOLog.STORAGE_LOGS, "Removing entry from disk buffer: " + file.getAbsolutePath(), true);
            return file.delete();
        }
        return false;
    }

    /**
     * @return the list of all all objects in the buffer
     */
    @Override
    public Iterator<Entry> getAllEntries() {
        final Iterator<File> fileIterator = Arrays.asList(directory.listFiles()).iterator();
        
        return new Iterator<Entry>() {
            private Entry next = getNextEntry(fileIterator);
                    
            @Override
            public boolean hasNext() {
                return next != null;
            }

            @Override
            public Entry next() {
                Entry entryToReturn = next;
                next = getNextEntry(fileIterator);
                return entryToReturn;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    private int getStoredEntriesCount() {
        int count = 0;
        for(File file : directory.listFiles()) {
            if(file.getAbsolutePath().endsWith(FILE_EXT)) {
                ++count;
            }
        }

        return count;
    }
    
    private Entry getNextEntry(Iterator<File> fileIterator) {
        while(fileIterator.hasNext()) {
            File file = fileIterator.next();
            
            if(!file.getAbsolutePath().endsWith(FILE_EXT)) {
                continue;
            }
            
            Entry entry = getEntryFromFile(file);
            if(entry != null) {
                return entry;
            }
        }

        return null;
    }

    private Entry getEntryFromFile(File file) {
        Object object;

        try {
            FileInputStream fileInputStream = new FileInputStream(new File(file.getAbsolutePath()));
            ObjectInputStream objectOutputStream = new ObjectInputStream(fileInputStream);
            object = objectOutputStream.readObject();
        } catch (Exception exception) {
            VWOLog.e(VWOLog.STORAGE_LOGS, "Unable to find entry", exception,
                    true, false);
            file.delete();
            return null;
        }

        try {
            return (Entry) object;
        } catch (Exception exception) {
            VWOLog.e(VWOLog.STORAGE_LOGS, "Error casting object to entry: " + file.getAbsolutePath(),
                    exception, true, true);
            VWOLog.e(VWOLog.STORAGE_LOGS, "Deleting entry : " + file.getAbsolutePath(),
                    true, false);
            file.delete();
            return null;
        }
    }
}
