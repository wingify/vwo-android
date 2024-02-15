package com.vwo.mobile.utils;

import androidx.annotation.Nullable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Created by aman on Tue 24/04/18 15:48.
 */
public class Serializer {

    @Nullable
    public static <T extends Serializable> byte[] marshall(T serializable) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = null;
        ObjectOutputStream objectOutputStream = null;
        try {
            byteArrayOutputStream = new ByteArrayOutputStream();
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(serializable);
            return byteArrayOutputStream.toByteArray();
        } finally {
            if (byteArrayOutputStream != null) {
                try {
                    byteArrayOutputStream.close();
                } catch (IOException ignored) {
                }
            }
            if (objectOutputStream != null) {
                try {
                    objectOutputStream.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    @Nullable
    public static Object unmarshaal(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteArrayInputStream = null;
        try {
            byteArrayInputStream = new ByteArrayInputStream(data);
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
            return objectInputStream.readObject();
        } finally {
            if (byteArrayInputStream != null) {
                try {
                    byteArrayInputStream.close();
                } catch (IOException ignore) {
                }
            }
        }
    }

    @Nullable
    public static <T> T unmarshall(byte[] data, Class<T> clazz) throws IOException, ClassNotFoundException, ClassCastException {
        try {
            return clazz.cast(unmarshaal(data));
        } catch (ClassCastException exception) {
            VWOLog.e(VWOLog.FILE_LOGS, "Unable to cast the object: " + clazz.getName(), exception, true, true);
            throw exception;
        }
    }
}
