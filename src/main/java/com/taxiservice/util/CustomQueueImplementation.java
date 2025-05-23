package com.taxiservice.util;

/**
 * A custom generic Queue implementation from scratch without using Java collections
 * Implements FIFO (First-In-First-Out) behavior
 * @param <T> The type of elements in the queue
 */
public class CustomQueueImplementation<T> {
    // Using our custom linked list as the underlying structure
    private final CustomLinkedList<T> list;
    
    /**
     * Constructs an empty queue
     */
    public CustomQueueImplementation() {
        this.list = new CustomLinkedList<>();
    }
    
    /**
     * Adds an element to the end of the queue
     * @param element The element to add
     */
    public void enqueue(T element) {
        list.add(element);
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
        return list.remove(0);
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
        return list.get(0);
    }
    
    /**
     * Checks if the queue is empty
     * @return true if the queue is empty, false otherwise
     */
    public boolean isEmpty() {
        return list.isEmpty();
    }
    
    /**
     * Returns the number of elements in the queue
     * @return The size of the queue
     */
    public int size() {
        return list.size();
    }
    
    /**
     * Returns a string representation of the queue
     * @return String representation of the queue
     */
    @Override
    public String toString() {
        return list.toString();
    }
}
