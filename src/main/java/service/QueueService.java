package com.example.taxi.service;
import com.example.taxi.model.Booking;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class QueueService {
    private Queue<Booking> queue = new LinkedList<>();
    public void enqueue(Booking b) { queue.offer(b); }
    public Booking dequeue() { return queue.poll(); }
    public List<Booking> list() { return new ArrayList<>(queue); }
}