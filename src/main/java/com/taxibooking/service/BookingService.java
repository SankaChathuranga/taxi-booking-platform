package com.taxibooking.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taxibooking.model.Booking;
import com.taxibooking.model.AbstractDriver;
import com.taxibooking.util.BookingQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BookingService {
    
    @Value("${app.file.storage.path}/${app.file.bookings}")
    private String bookingsFile;
    
    private final ObjectMapper objectMapper;
    private final BookingQueue bookingQueue;
    private final DriverManagementService driverService;
    
    @Autowired
    public BookingService(DriverManagementService driverService) {
        this.objectMapper = new ObjectMapper();
        this.bookingQueue = new BookingQueue();
        this.driverService = driverService;
    }
    
    @PostConstruct
    public void init() {
        createFileIfNotExists();
    }
    
    private void createFileIfNotExists() {
        try {
            Path filePath = Paths.get(bookingsFile);
            if (!Files.exists(filePath)) {
                Files.createDirectories(filePath.getParent());
                Files.createFile(filePath);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not create bookings file: " + e.getMessage(), e);
        }
    }
    
    public Booking createBooking(String customerId, String pickupLocation, String dropoffLocation,
                               String vehicleType, boolean isScheduled, LocalDateTime scheduledTime) throws IOException {
        Booking booking = new Booking(customerId, pickupLocation, dropoffLocation, vehicleType, isScheduled, scheduledTime);
        saveBooking(booking);
        if (!isScheduled) {
            bookingQueue.addBooking(booking);
        }
        return booking;
    }
    
    public void saveBooking(Booking booking) throws IOException {
        List<Booking> bookings = getAllBookings();
        bookings.removeIf(b -> b.getBookingId().equals(booking.getBookingId()));
        bookings.add(booking);
        writeBookingsToFile(bookings);
    }
    
    public List<Booking> getAllBookings() throws IOException {
        if (Files.size(Paths.get(bookingsFile)) == 0) {
            return new ArrayList<>();
        }
        
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(bookingsFile))) {
            return reader.lines()
                    .map(line -> {
                        try {
                            return objectMapper.readValue(line, Booking.class);
                        } catch (Exception e) {
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }
    }
    
    public Optional<Booking> getBookingById(String bookingId) throws IOException {
        return getAllBookings().stream()
                .filter(booking -> booking.getBookingId().equals(bookingId))
                .findFirst();
    }
    
    public List<Booking> getCustomerBookings(String customerId) throws IOException {
        return getAllBookings().stream()
                .filter(booking -> booking.getCustomerId().equals(customerId))
                .collect(Collectors.toList());
    }
    
    public List<Booking> getDriverBookings(String driverId) throws IOException {
        return getAllBookings().stream()
                .filter(booking -> booking.getDriverId() != null && booking.getDriverId().equals(driverId))
                .collect(Collectors.toList());
    }
    
    public List<Booking> getActiveBookings() throws IOException {
        return getAllBookings().stream()
                .filter(Booking::isActive)
                .collect(Collectors.toList());
    }
    
    public List<Booking> getPendingBookings() throws IOException {
        return getAllBookings().stream()
                .filter(booking -> booking.getStatus() == Booking.BookingStatus.PENDING)
                .collect(Collectors.toList());
    }
    
    public void assignDriver(String bookingId, String driverId) throws IOException {
        Optional<Booking> bookingOpt = getBookingById(bookingId);
        Optional<AbstractDriver> driverOpt = driverService.getDriverById(driverId);
        
        if (bookingOpt.isPresent() && driverOpt.isPresent()) {
            Booking booking = bookingOpt.get();
            AbstractDriver driver = driverOpt.get();
            
            if (booking.getStatus() == Booking.BookingStatus.PENDING && driver.isAvailable()) {
                booking.confirm(driverId);
                driver.setAvailable(false);
                saveBooking(booking);
                driverService.saveDriver(driver);
                bookingQueue.removeBooking(bookingId);
            }
        }
    }
    
    public void startRide(String bookingId) throws IOException {
        Optional<Booking> bookingOpt = getBookingById(bookingId);
        if (bookingOpt.isPresent()) {
            Booking booking = bookingOpt.get();
            booking.startRide();
            saveBooking(booking);
        }
    }
    
    public void completeRide(String bookingId, double distance, double fare) throws IOException {
        Optional<Booking> bookingOpt = getBookingById(bookingId);
        Optional<AbstractDriver> driverOpt = bookingOpt.flatMap(booking -> {
            try {
                return driverService.getDriverById(booking.getDriverId());
            } catch (IOException e) {
                return Optional.empty();
            }
        });
        
        if (bookingOpt.isPresent() && driverOpt.isPresent()) {
            Booking booking = bookingOpt.get();
            AbstractDriver driver = driverOpt.get();
            
            booking.completeRide(distance, fare);
            driver.setAvailable(true);
            saveBooking(booking);
            driverService.saveDriver(driver);
        }
    }
    
    public void cancelBooking(String bookingId) throws IOException {
        Optional<Booking> bookingOpt = getBookingById(bookingId);
        Optional<AbstractDriver> driverOpt = bookingOpt.flatMap(booking -> {
            try {
                return booking.getDriverId() != null ? 
                       driverService.getDriverById(booking.getDriverId()) : 
                       Optional.empty();
            } catch (IOException e) {
                return Optional.empty();
            }
        });
        
        if (bookingOpt.isPresent()) {
            Booking booking = bookingOpt.get();
            if (booking.canBeCancelled()) {
                booking.cancel();
                saveBooking(booking);
                bookingQueue.removeBooking(bookingId);
                
                // If a driver was assigned, make them available again
                driverOpt.ifPresent(driver -> {
                    driver.setAvailable(true);
                    try {
                        driverService.saveDriver(driver);
                    } catch (IOException e) {
                        // Log error
                    }
                });
            }
        }
    }
    
    public void markAsNoShow(String bookingId) throws IOException {
        Optional<Booking> bookingOpt = getBookingById(bookingId);
        Optional<AbstractDriver> driverOpt = bookingOpt.flatMap(booking -> {
            try {
                return driverService.getDriverById(booking.getDriverId());
            } catch (IOException e) {
                return Optional.empty();
            }
        });
        
        if (bookingOpt.isPresent() && driverOpt.isPresent()) {
            Booking booking = bookingOpt.get();
            AbstractDriver driver = driverOpt.get();
            
            booking.markAsNoShow();
            driver.setAvailable(true);
            saveBooking(booking);
            driverService.saveDriver(driver);
        }
    }
    
    public void updatePaymentStatus(String bookingId, Booking.PaymentStatus status) throws IOException {
        Optional<Booking> bookingOpt = getBookingById(bookingId);
        if (bookingOpt.isPresent()) {
            Booking booking = bookingOpt.get();
            booking.updatePaymentStatus(status);
            saveBooking(booking);
        }
    }
    
    public Booking getNextPendingBooking() {
        return bookingQueue.getNextBooking();
    }
    
    public int getPendingQueueSize() {
        return bookingQueue.getQueueSize();
    }
    
    private void writeBookingsToFile(List<Booking> bookings) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(bookingsFile))) {
            for (Booking booking : bookings) {
                writer.write(objectMapper.writeValueAsString(booking));
                writer.newLine();
            }
        }
    }
} 