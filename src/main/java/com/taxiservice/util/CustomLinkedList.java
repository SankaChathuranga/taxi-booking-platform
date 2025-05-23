package com.taxiservice.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A custom generic linked list implementation from scratch without using Java collections
 * @param <T> The type of elements in the list
 */
public class CustomLinkedList<T> implements Iterable<T> {
    
    /**
     * Node class to represent elements in the linked list
     */
    private class Node {
        T data;
        Node next;
        
        public Node(T data) {
            this.data = data;
            this.next = null;
        }
    }
    
    private Node head;
    private Node tail;
    private int size;
    
    /**
     * Constructs an empty linked list
     */
    public CustomLinkedList() {
        this.head = null;
        this.tail = null;
        this.size = 0;
    }
    
    /**
     * Adds an element to the end of the list
     * @param element The element to add
     */
    public void add(T element) {
        Node newNode = new Node(element);
        
        if (head == null) {
            head = newNode;
            tail = newNode;
        } else {
            tail.next = newNode;
            tail = newNode;
        }
        
        size++;
    }
    
    /**
     * Gets an element at the specified index
     * @param index The index of the element to retrieve
     * @return The element at the specified index
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public T get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        
        Node current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        
        return current.data;
    }
    
    /**
     * Sets the element at the specified index
     * @param index The index of the element to set
     * @param element The element to set
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public void set(int index, T element) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        
        Node current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        
        current.data = element;
    }
    
    /**
     * Removes and returns the element at the specified index
     * @param index The index of the element to remove
     * @return The removed element
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public T remove(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        
        T removed;
        
        if (index == 0) {
            removed = head.data;
            head = head.next;
            if (head == null) {
                tail = null;
            }
        } else {
            Node current = head;
            for (int i = 0; i < index - 1; i++) {
                current = current.next;
            }
            
            removed = current.next.data;
            current.next = current.next.next;
            
            if (current.next == null) {
                tail = current;
            }
        }
        
        size--;
        return removed;
    }
    
    /**
     * Removes the first occurrence of the specified element
     * @param element The element to remove
     * @return true if the element was found and removed, false otherwise
     */
    public boolean remove(T element) {
        if (head == null) {
            return false;
        }
        
        if (head.data.equals(element)) {
            head = head.next;
            if (head == null) {
                tail = null;
            }
            size--;
            return true;
        }
        
        Node current = head;
        while (current.next != null && !current.next.data.equals(element)) {
            current = current.next;
        }
        
        if (current.next != null) {
            current.next = current.next.next;
            if (current.next == null) {
                tail = current;
            }
            size--;
            return true;
        }
        
        return false;
    }
    
    /**
     * Returns the number of elements in the list
     * @return The size of the list
     */
    public int size() {
        return size;
    }
    
    /**
     * Checks if the list is empty
     * @return true if the list is empty, false otherwise
     */
    public boolean isEmpty() {
        return size == 0;
    }
    
    /**
     * Clears all elements from the list
     */
    public void clear() {
        head = null;
        tail = null;
        size = 0;
    }
    
    /**
     * Returns an iterator over the elements in the list
     * @return An iterator
     */
    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private Node current = head;
            
            @Override
            public boolean hasNext() {
                return current != null;
            }
            
            @Override
            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                
                T data = current.data;
                current = current.next;
                return data;
            }
        };
    }
    
    /**
     * Returns a string representation of the list
     * @return String representation of the list
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        Node current = head;
        
        while (current != null) {
            sb.append(current.data);
            if (current.next != null) {
                sb.append(", ");
            }
            current = current.next;
        }
        
        sb.append("]");
        return sb.toString();
    }
}
