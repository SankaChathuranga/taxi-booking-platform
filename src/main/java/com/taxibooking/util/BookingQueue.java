package com.taxibooking.util;

import com.taxibooking.model.Booking;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.ReentrantLock;

public class BookingQueue {
    private final Queue<Booking> queue;
    private final ReentrantLock lock;
    private static final int MAX_QUEUE_SIZE = 100;

    public BookingQueue() {
        this.queue = new LinkedList<>();
        this.lock = new ReentrantLock();
    }

    public boolean addBooking(Booking booking) {
        lock.lock();
        try {
            if (queue.size() >= MAX_QUEUE_SIZE) {
                return false;
            }
            return queue.offer(booking);
        } finally {
            lock.unlock();
        }
    }

    public Booking getNextBooking() {
        lock.lock();
        try {
            return queue.poll();
        } finally {
            lock.unlock();
        }
    }

    public Booking peekNextBooking() {
        lock.lock();
        try {
            return queue.peek();
        } finally {
            lock.unlock();
        }
    }

    public boolean removeBooking(String bookingId) {
        lock.lock();
        try {
            return queue.removeIf(booking -> booking.getBookingId().equals(bookingId));
        } finally {
            lock.unlock();
        }
    }

    public int getQueueSize() {
        lock.lock();
        try {
            return queue.size();
        } finally {
            lock.unlock();
        }
    }

    public boolean isEmpty() {
        lock.lock();
        try {
            return queue.isEmpty();
        } finally {
            lock.unlock();
        }
    }

    public void clear() {
        lock.lock();
        try {
            queue.clear();
        } finally {
            lock.unlock();
        }
    }

    public Queue<Booking> getQueueSnapshot() {
        lock.lock();
        try {
            return new LinkedList<>(queue);
        } finally {
            lock.unlock();
        }
    }
} 