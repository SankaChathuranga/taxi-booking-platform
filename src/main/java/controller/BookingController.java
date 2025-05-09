package com.example.taxi.controller;

import com.example.taxi.model.Booking;
import com.example.taxi.service.BookingService;
import com.example.taxi.service.QueueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Controller
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private QueueService queueService;

    // Show booking form
    @GetMapping("/bookings")
    public String showBookingForm() {
        return "static-html/booking_form.html";
    }

    // API: create booking
    @PostMapping("/api/bookings")
    public String bookRide(@RequestParam String customerId,
                           @RequestParam String driverId) throws IOException {
        String bookingId = UUID.randomUUID().toString();
        String timestamp = LocalDateTime.now().toString();
        String status = "Pending";

        Booking booking = new Booking(bookingId, customerId, driverId, timestamp, status);
        bookingService.save(booking);
        queueService.enqueue(booking);
        return "redirect:/queue";
    }

    // Show queue page
    @GetMapping("/queue")
    public String showQueuePage(Model model) {
        List<Booking> list = queueService.list();
        model.addAttribute("bookings", list);
        return "static-html/queue.html";
    }

    // API: get queue list
    @GetMapping("/api/queue")
    @ResponseBody
    public List<Booking> getQueue() {
        return queueService.list();
    }

    // API: dequeue next booking
    @PostMapping("/api/queue/dequeue")
    @ResponseBody
    public Booking dequeue() {
        return queueService.dequeue();
    }