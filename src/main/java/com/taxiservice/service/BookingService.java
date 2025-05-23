package com.taxiservice.service;

import com.taxiservice.dto.BookingDTO;
import com.taxiservice.exception.TaxiServiceException;
import com.taxiservice.model.Booking;
import com.taxiservice.model.Driver;
import com.taxiservice.repository.BookingRepository;
import com.taxiservice.repository.DriverRepository;
import com.taxiservice.util.CustomQueueImplementation;
import com.taxiservice.util.CustomDriverList;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookingService {
    private final BookingRepository bookingRepository;
    private final DriverRepository driverRepository;
    private final CustomQueueImplementation<Booking> bookingQueue;

    public BookingService(BookingRepository bookingRepository, DriverRepository driverRepository) {
        this.bookingRepository = bookingRepository;
        this.driverRepository = driverRepository;
        this.bookingQueue = new CustomQueueImplementation<>();
        
        // Load existing pending bookings into the queue
        initializeBookingQueue();
    }

    /**
     * Initialize the booking queue with existing pending bookings from the repository
     * This ensures bookings are preserved when the application restarts
     */
    private void initializeBookingQueue() {
        List<Booking> pendingBookings = bookingRepository.findAll().stream()
                .filter(booking -> booking.getStatus() == Booking.BookingStatus.PENDING)
                .collect(Collectors.toList());
        
        // Sort by booking time to maintain FIFO order
        pendingBookings.sort((b1, b2) -> b1.getBookingTime().compareTo(b2.getBookingTime()));
        
        // Add all pending bookings to the queue
        for (Booking booking : pendingBookings) {
            bookingQueue.enqueue(booking);
        }
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
        
        // Assign a random driver from available drivers
        int randomIndex = (int) (Math.random() * availableDrivers.size());
        Driver selectedDriver = availableDrivers.get(randomIndex);
        booking.setDriverId(selectedDriver.getId());
        System.out.println("Assigned driver " + selectedDriver.getFullName() + " (ID: " + selectedDriver.getId() + ") to booking");
        
        // Save booking first to get an ID
        booking = bookingRepository.save(booking);
        
        // Add to queue after it has an ID
        bookingQueue.enqueue(booking);
        
        return BookingDTO.fromEntity(booking);
    }

    /**
     * Get all bookings currently in the queue (pending status)
     * @return List of booking DTOs in FIFO order
     */
    public List<BookingDTO> getQueuedBookings() {
        if (bookingQueue.isEmpty()) {
            return new ArrayList<>();
        }
        
        // Since we can't directly access queue elements without removing them,
        // we'll get them from the repository and order them by booking time
        List<Booking> pendingBookings = bookingRepository.findAll().stream()
                .filter(booking -> booking.getStatus() == Booking.BookingStatus.PENDING)
                .sorted((b1, b2) -> b1.getBookingTime().compareTo(b2.getBookingTime()))
                .collect(Collectors.toList());
                
        return pendingBookings.stream()
                .map(BookingDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Get the next booking from the queue
     * @return The next booking in the queue or empty if queue is empty
     */
    public Optional<BookingDTO> getNextBooking() {
        if (bookingQueue.isEmpty()) {
            return Optional.empty();
        }
        
        Booking nextBooking = bookingQueue.peek();
        return Optional.of(BookingDTO.fromEntity(nextBooking));
    }

    public BookingDTO acceptBooking(String id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new TaxiServiceException("Booking not found"));

        if (booking.getStatus() != Booking.BookingStatus.PENDING) {
            throw new TaxiServiceException("Booking is not in pending status");
        }

        // Update status
        booking.setStatus(Booking.BookingStatus.ACCEPTED);
        booking = bookingRepository.save(booking);
        
        // Remove from queue - find and remove the booking with matching ID
        removeBookingFromQueue(id);
        
        return BookingDTO.fromEntity(booking);
    }
    
    /**
     * Helper method to remove a booking from the queue by ID
     */
    private void removeBookingFromQueue(String id) {
        // Create a temporary queue
        CustomQueueImplementation<Booking> tempQueue = new CustomQueueImplementation<>();
        
        // Process the entire queue
        while (!bookingQueue.isEmpty()) {
            Booking booking = bookingQueue.dequeue();
            // If this is not the booking we want to remove, add it to the temp queue
            if (!booking.getId().equals(id)) {
                tempQueue.enqueue(booking);
            }
        }
        
        // Restore the queue without the removed booking
        while (!tempQueue.isEmpty()) {
            bookingQueue.enqueue(tempQueue.dequeue());
        }
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
        
        // If the booking is still in the queue, remove it
        if (booking.getStatus() == Booking.BookingStatus.PENDING) {
            removeBookingFromQueue(id);
        }
        
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
    
    /**
     * Get all bookings in the system with optional status filter
     */
    public List<BookingDTO> getAllBookings(String status) {
        List<Booking> bookings = bookingRepository.findAll();
        
        if (status != null && !status.isEmpty() && !status.equalsIgnoreCase("ALL")) {
            Booking.BookingStatus bookingStatus = Booking.BookingStatus.valueOf(status.toUpperCase());
            bookings = bookings.stream()
                    .filter(booking -> booking.getStatus() == bookingStatus)
                    .collect(Collectors.toList());
        }
        
        return bookings.stream()
                .map(BookingDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public BookingDTO rateBooking(String id, double rating) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new TaxiServiceException("Booking not found"));

        // If booking is not completed, set it to completed
        if (booking.getStatus() != Booking.BookingStatus.COMPLETED) {
            booking.setStatus(Booking.BookingStatus.COMPLETED);
            System.out.println("Setting booking " + booking.getId() + " to COMPLETED status for rating");
        }

        // Check if there's a driver assigned
        if (booking.getDriverId() == null || booking.getDriverId().isEmpty()) {
            // Find and assign a driver if none exists
            List<Driver> availableDrivers = driverRepository.findAvailableDrivers();
            if (availableDrivers.isEmpty()) {
                availableDrivers = driverRepository.findAll();
            }
            
            if (availableDrivers.isEmpty()) {
                throw new TaxiServiceException("No drivers available in the system");
            }
            
            // Assign the first available driver
            Driver driver = availableDrivers.get(0);
            booking.setDriverId(driver.getId());
            System.out.println("Auto-assigned driver " + driver.getFullName() + " to booking " + booking.getId() + " for rating");
        }
        
        // Save the rating
        booking.setRating(rating);
        booking = bookingRepository.save(booking);
        System.out.println("Saved rating " + rating + " for booking " + booking.getId());

        try {
            // Update driver's average rating
            Driver driver = driverRepository.findById(booking.getDriverId())
                    .orElseThrow(() -> new TaxiServiceException("Driver not found"));
            
            // Get all completed bookings for this driver that have ratings
            List<Booking> completedBookings = bookingRepository.findByDriverId(driver.getId()).stream()
                    .filter(b -> b.getStatus() == Booking.BookingStatus.COMPLETED && b.getRating() != null)
                    .collect(Collectors.toList());

            // Calculate the average rating
            double averageRating = completedBookings.stream()
                    .mapToDouble(Booking::getRating)
                    .average()
                    .orElse(rating); // If no previous ratings, use this rating

            System.out.println("Calculated average rating " + averageRating + " for driver " + driver.getFullName());
            
            // Update and save the driver's rating
            driver.setRating(averageRating);
            driverRepository.save(driver);
            
            // Optional: Sort drivers by rating using CustomBubbleSortImplementation
            List<Driver> allDriversFromRepo = driverRepository.findAll();
            
            // Convert to our custom driver list
            CustomDriverList customDriverList = new CustomDriverList(allDriversFromRepo.size());
            for (Driver driverItem : allDriversFromRepo) {
                customDriverList.add(driverItem);
            }
            
            // Sort using our custom bubble sort implementation
            com.taxiservice.util.CustomBubbleSortImplementation.sort(customDriverList); // This sorts in descending order (highest rating first)
            
            // Convert back to List for compatibility with the rest of the application
            List<Driver> allDrivers = allDriversFromRepo;
            allDrivers.clear();
            for (int i = 0; i < customDriverList.size(); i++) {
                allDrivers.add(customDriverList.get(i));
            }
            
            System.out.println("Sorted drivers by rating. Top rated driver: " + 
                    (allDrivers.isEmpty() ? "None" : allDrivers.get(0).getFullName() + " (" + allDrivers.get(0).getRating() + ")"));
        } catch (Exception e) {
            // Log the error but don't fail the rating submission
            System.err.println("Warning: Could not update driver rating: " + e.getMessage());
            e.printStackTrace();
        }

        return BookingDTO.fromEntity(booking);
    }

    /**
     * Assigns a driver to a booking if one is not already assigned
     * @param id The booking ID
     * @return The updated booking DTO
     */
    public BookingDTO assignDriverToBooking(String id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new TaxiServiceException("Booking not found"));
        
        // If booking already has a driver assigned, just return it
        if (booking.getDriverId() != null && !booking.getDriverId().isEmpty()) {
            return BookingDTO.fromEntity(booking);
        }
        
        // Find available drivers
        List<Driver> availableDrivers = driverRepository.findAvailableDrivers();
        
        // If no available drivers, try to get any driver
        if (availableDrivers.isEmpty()) {
            availableDrivers = driverRepository.findAll();
        }
        
        // If still no drivers, throw exception
        if (availableDrivers.isEmpty()) {
            throw new TaxiServiceException("No drivers available in the system");
        }
        
        // Assign the first available driver
        Driver driver = availableDrivers.get(0);
        booking.setDriverId(driver.getId());
        
        // Save the updated booking
        booking = bookingRepository.save(booking);
        
        // Log the assignment
        System.out.println("Assigned driver " + driver.getFullName() + " (ID: " + driver.getId() + ") to booking " + booking.getId());
        
        return BookingDTO.fromEntity(booking);
    }

    private double calculateFare(String pickupLocation, String dropLocation) {
        // Simple fare calculation for demo
        // In a real application, this would use distance calculation and pricing rules
        return 50.0; // Base fare
    }
}