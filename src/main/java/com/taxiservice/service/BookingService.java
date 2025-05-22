package com.taxiservice.service;

import com.taxiservice.dto.BookingDTO;
import com.taxiservice.exception.TaxiServiceException;
import com.taxiservice.model.Booking;
import com.taxiservice.model.Driver;
import com.taxiservice.repository.BookingRepository;
import com.taxiservice.repository.DriverRepository;
import com.taxiservice.util.QueueImplementation;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingService {
    private final BookingRepository bookingRepository;
    private final DriverRepository driverRepository;
    private final QueueImplementation<Booking> bookingQueue;

    public BookingService(BookingRepository bookingRepository, DriverRepository driverRepository) {
        this.bookingRepository = bookingRepository;
        this.driverRepository = driverRepository;
        this.bookingQueue = new QueueImplementation<>();
    }

    public BookingDTO createBooking(BookingDTO bookingDTO) {
        // Validate user and driver
        List<Driver> availableDrivers = driverRepository.findAvailableDrivers();
        if (availableDrivers.isEmpty()) {
            throw new TaxiServiceException("No drivers available");
        }

        // Create booking
        Booking booking = bookingDTO.toEntity();
        booking.setStatus(Booking.BookingStatus.PENDING);
        booking.setBookingTime(LocalDateTime.now());
        
        // Calculate fare (simple calculation for demo)
        booking.setFare(calculateFare(booking.getPickupLocation(), booking.getDropLocation()));
        
        // Add to queue
        bookingQueue.enqueue(booking);
        
        // Save booking
        booking = bookingRepository.save(booking);
        return BookingDTO.fromEntity(booking);
    }

    public BookingDTO acceptBooking(String id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new TaxiServiceException("Booking not found"));

        if (booking.getStatus() != Booking.BookingStatus.PENDING) {
            throw new TaxiServiceException("Booking is not in pending status");
        }

        booking.setStatus(Booking.BookingStatus.ACCEPTED);
        booking = bookingRepository.save(booking);
        return BookingDTO.fromEntity(booking);
    }

    public BookingDTO completeBooking(String id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new TaxiServiceException("Booking not found"));

        if (booking.getStatus() != Booking.BookingStatus.ACCEPTED) {
            throw new TaxiServiceException("Booking is not in accepted status");
        }

        booking.setStatus(Booking.BookingStatus.COMPLETED);
        booking = bookingRepository.save(booking);
        return BookingDTO.fromEntity(booking);
    }

    public BookingDTO cancelBooking(String id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new TaxiServiceException("Booking not found"));

        if (booking.getStatus() == Booking.BookingStatus.COMPLETED) {
            throw new TaxiServiceException("Cannot cancel completed booking");
        }

        booking.setStatus(Booking.BookingStatus.CANCELLED);
        booking = bookingRepository.save(booking);
        return BookingDTO.fromEntity(booking);
    }

    public List<BookingDTO> getBookingHistory(String userId) {
        return bookingRepository.findByUserId(userId).stream()
                .map(BookingDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<BookingDTO> getDriverBookings(String driverId) {
        return bookingRepository.findByDriverId(driverId).stream()
                .map(BookingDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public BookingDTO rateBooking(String id, double rating) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new TaxiServiceException("Booking not found"));

        if (booking.getStatus() != Booking.BookingStatus.COMPLETED) {
            throw new TaxiServiceException("Can only rate completed bookings");
        }

        booking.setRating(rating);
        booking = bookingRepository.save(booking);

        // Update driver's average rating
        Driver driver = driverRepository.findById(booking.getDriverId())
                .orElseThrow(() -> new TaxiServiceException("Driver not found"));
        
        List<Booking> completedBookings = bookingRepository.findByDriverId(driver.getId()).stream()
                .filter(b -> b.getStatus() == Booking.BookingStatus.COMPLETED && b.getRating() != null)
                .collect(Collectors.toList());

        double averageRating = completedBookings.stream()
                .mapToDouble(Booking::getRating)
                .average()
                .orElse(0.0);

        driver.setRating(averageRating);
        driverRepository.save(driver);

        return BookingDTO.fromEntity(booking);
    }

    private double calculateFare(String pickupLocation, String dropLocation) {
        // Simple fare calculation for demo
        // In a real application, this would use distance calculation and pricing rules
        return 50.0; // Base fare
    }
} 