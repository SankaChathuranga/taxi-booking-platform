package com.taxiservice.util;

import java.util.LinkedList;

/**
 * A generic Queue implementation using LinkedList
 * @param <T> The type of elements in the queue
 */
public class QueueImplementation<T> {
    private final LinkedList<T> queue;

    public QueueImplementation() {
        this.queue = new LinkedList<>();
    }

    /**
     * Adds an element to the end of the queue
     * @param element The element to add
     */
    public void enqueue(T element) {
        queue.addLast(element);
    }

    /**
     * Removes and returns the first element from the queue
     * @return The first element in the queue
     * @throws IllegalStateException if the queue is empty
     */
    public T dequeue() {
        if (isEmpty()) {
            throw new IllegalStateException("Queue is empty");
        }
        return queue.removeFirst();
    }

    /**
     * Returns the first element in the queue without removing it
     * @return The first element in the queue
     * @throws IllegalStateException if the queue is empty
     */
    public T peek() {
        if (isEmpty()) {
            throw new IllegalStateException("Queue is empty");
        }
        return queue.getFirst();
    }

    /**
     * Checks if the queue is empty
     * @return true if the queue is empty, false otherwise
     */
    public boolean isEmpty() {
        return queue.isEmpty();
    }

    /**
     * Returns the number of elements in the queue
     * @return The size of the queue
     */
    public int size() {
        return queue.size();
    }

    /**
     * Returns a string representation of the queue
     * @return String representation of the queue
     */
    @Override
    public String toString() {
        return queue.toString();
    }
} 