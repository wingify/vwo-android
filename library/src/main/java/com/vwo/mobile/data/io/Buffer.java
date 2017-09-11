package com.vwo.mobile.data.io;

import java.util.Iterator;

/**
 * Created by aman on 11/09/17.
 */

public interface Buffer<T> {
    /**
     * @param object is the object to be added to the buffer
     */
    boolean add(T object);

    /**
     * @param object is the object to be removed from the buffer
     */
    boolean remove(T object);

    /**
     *
     * @return the list of all all objects in the buffer
     */
    Iterator<T> getAllEntries();
}
