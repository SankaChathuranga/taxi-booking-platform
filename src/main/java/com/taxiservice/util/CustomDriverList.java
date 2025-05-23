package com.taxiservice.util;

import com.taxiservice.model.Driver;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A custom array-based list implementation specifically for Driver objects
 * Implemented from scratch without using Java collections
 */
public class CustomDriverList implements Iterable<Driver> {
    private Driver[] drivers;
    private int size;
    private static final int DEFAULT_CAPACITY = 10;
    
    /**
     * Constructs an empty driver list with default capacity
     */
    public CustomDriverList() {
        this.drivers = new Driver[DEFAULT_CAPACITY];
        this.size = 0;
    }
    
    /**
     * Constructs an empty driver list with the specified capacity
     * @param initialCapacity The initial capacity of the list
     * @throws IllegalArgumentException if initialCapacity is negative
     */
    public CustomDriverList(int initialCapacity) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("Initial capacity cannot be negative: " + initialCapacity);
        }
        this.drivers = new Driver[initialCapacity];
        this.size = 0;
    }
    
    /**
     * Adds a driver to the end of the list
     * @param driver The driver to add
     */
    public void add(Driver driver) {
        ensureCapacity(size + 1);
        drivers[size++] = driver;
    }
    
    /**
     * Gets the driver at the specified index
     * @param index The index of the driver to retrieve
     * @return The driver at the specified index
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public Driver get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        return drivers[index];
    }
    
    /**
     * Sets the driver at the specified index
     * @param index The index of the driver to set
     * @param driver The driver to set
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public void set(int index, Driver driver) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        drivers[index] = driver;
    }
    
    /**
     * Removes and returns the driver at the specified index
     * @param index The index of the driver to remove
     * @return The removed driver
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public Driver remove(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        
        Driver removedDriver = drivers[index];
        
        // Shift elements to fill the gap
        for (int i = index; i < size - 1; i++) {
            drivers[i] = drivers[i + 1];
        }
        
        // Clear the last element and decrement size
        drivers[--size] = null;
        
        return removedDriver;
    }
    
    /**
     * Returns the number of drivers in the list
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
     * Clears all drivers from the list
     */
    public void clear() {
        // Clear references to allow garbage collection
        for (int i = 0; i < size; i++) {
            drivers[i] = null;
        }
        size = 0;
    }
    
    /**
     * Converts the driver list to an array
     * @return An array containing all drivers in the list
     */
    public Driver[] toArray() {
        Driver[] result = new Driver[size];
        System.arraycopy(drivers, 0, result, 0, size);
        return result;
    }
    
    /**
     * Ensures that the underlying array has enough capacity
     * @param minCapacity The minimum required capacity
     */
    private void ensureCapacity(int minCapacity) {
        if (minCapacity > drivers.length) {
            int newCapacity = Math.max(drivers.length * 2, minCapacity);
            Driver[] newDrivers = new Driver[newCapacity];
            System.arraycopy(drivers, 0, newDrivers, 0, size);
            drivers = newDrivers;
        }
    }
    
    /**
     * Returns an iterator over the drivers in the list
     * @return An iterator
     */
    @Override
    public Iterator<Driver> iterator() {
        return new Iterator<Driver>() {
            private int currentIndex = 0;
            
            @Override
            public boolean hasNext() {
                return currentIndex < size;
            }
            
            @Override
            public Driver next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                return drivers[currentIndex++];
            }
        };
    }
    
    /**
     * Returns a string representation of the driver list
     * @return String representation of the list
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < size; i++) {
            sb.append(drivers[i]);
            if (i < size - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }
}
