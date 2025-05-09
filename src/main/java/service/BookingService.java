package com.example.taxi.service;

import com.example.taxi.model.Booking;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;

@Service
public class BookingService {
    private static final String FILE = "bookings.txt";

    public void save(Booking booking) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE, true))) {
            pw.println(booking.getId() + "," + booking.getCustomerId() + "," + booking.getDriverId() + "," + booking.getTimestamp() + "," + booking.getStatus());
        }
    }

    public List<Booking> findAll() throws IOException {
        List<Booking> bookings = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] f = line.split(",");
                bookings.add(new Booking(f[0], f[1], f[2], f[3], f[4]));
            }
        }
        return bookings;
    }
}