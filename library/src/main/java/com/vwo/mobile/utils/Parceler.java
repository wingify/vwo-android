package com.vwo.mobile.utils;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by aman on Fri 03/11/17 17:04.
 */

public class Parceler {
    public static byte[] marshall(Parcelable parcelable) {
        Parcel parcel = Parcel.obtain();
        parcelable.writeToParcel(parcel, 0);
        byte[] bytes = parcel.marshall();
        parcel.recycle();
        return bytes;
    }

    public static Parcel unmarshall(byte[] bytes) {
        Parcel parcel = Parcel.obtain();
        parcel.unmarshall(bytes, 0, bytes.length);
        parcel.setDataPosition(0); // This is extremely important!
        return parcel;
    }

    public static <T> T unmarshall(byte[] bytes, Parcelable.Creator<T> creator) {
        Parcel parcel = unmarshall(bytes);
        T result = creator.createFromParcel(parcel);
        parcel.recycle();
        return result;
    }

    public static void writeStringMapToParcel(Map<String, String> map, Parcel dest) {
        if(map == null || map.size() == 0) {
            dest.writeInt(0);
            return;
        }
        Set<String> keys = map.keySet();
        dest.writeInt(map.size());
        for(String key : keys) {
            dest.writeString(key);
            dest.writeString(map.get(key));
        }
    }

    public static Map<String, String> readStringMapFromParcel(Parcel in) {
        int size = in.readInt();
        Map<String, String> data = new HashMap<>(size);
        for(int i = 0; i < size; i++) {
            data.put(in.readString(), in.readString());
        }

        return data;
    }
}
