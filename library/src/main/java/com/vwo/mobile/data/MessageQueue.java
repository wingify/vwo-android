package com.vwo.mobile.data;

/**
 * Created by aman on 19/09/17.
 */

public interface MessageQueue<T> {
    /**
     * Retrieves, but does not remove, the head of this queue, or returns null if this queue is empty.
     */
    T peek();

    /**
     * Inserts the specified element asynchronously into this queue if it is possible to do so immediately without violating capacity restrictions.
     * @param t {@link T} is the element to be inserted
     */
    void add(T t);

    /**
     * Removes the head of this queue.
     */
    void remove();

    /**
     *
     * @return the total count of elements in the queue
     */
    int size();

    /**
     * Removes all elements from the queue
     */
    void removeAll();

    /**
     * Retrieves and removes the head of this queue, or returns null if this queue is empty.
     * @return the element {@link T}
     */
    T poll();
}
