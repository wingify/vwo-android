package com.vwo.mobile.data;

/**
 * Created by aman on 19/09/17.
 * Modified by aman on 19/09/17 1:22 PM.
 */

public interface MessageQueue<T> {
    T peek();
    void add(T t);
    void remove();
    int size();
    void removeAll();

}
