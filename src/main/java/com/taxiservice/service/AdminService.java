package com.taxiservice.service;

import com.taxiservice.dto.BookingDTO;
import com.taxiservice.dto.UserDTO;
import com.taxiservice.exception.TaxiServiceException;
import com.taxiservice.model.Booking;
import com.taxiservice.model.Driver;
import com.taxiservice.model.User;
import com.taxiservice.repository.BookingRepository;
import com.taxiservice.repository.DriverRepository;
import com.taxiservice.repository.UserRepository;
import com.taxiservice.util.DriverSorter;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AdminService {
    private final UserRepository userRepository;
    private final DriverRepository driverRepository;
    private final BookingRepository bookingRepository;

    public AdminService(UserRepository userRepository, 
                       DriverRepository driverRepository,
                       BookingRepository bookingRepository) {
        this.userRepository = userRepository;
        this.driverRepository = driverRepository;
        this.bookingRepository = bookingRepository;
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<Driver> getAllDrivers(String sortBy, String sortOrder) {
        List<Driver> drivers = driverRepository.findAll();
        
        if (sortBy != null && sortBy.equalsIgnoreCase("rating")) {
            boolean ascending = sortOrder != null && sortOrder.equalsIgnoreCase("asc");
            DriverSorter.sortByRating(drivers, ascending);
        }
        
        return drivers;
    }
    
    /**
     * Get all drivers sorted by rating using bubble sort
     * 
     * @param ascending true to sort in ascending order, false for descending
     * @return List of drivers sorted by rating
     */
    public List<Driver> getDriversSortedByRating(boolean ascending) {
        List<Driver> drivers = driverRepository.findAll();
        DriverSorter.sortByRating(drivers, ascending);
        return drivers;
    }
    
    public Driver getDriverById(String id) {
        return driverRepository.findById(id)
                .orElseThrow(() -> new TaxiServiceException("Driver not found with id: " + id));
    }
    
    public Driver createDriver(Driver driver) {
        if (driver.getUserId() == null || driver.getUserId().trim().isEmpty()) {
            throw new TaxiServiceException("User ID is required");
        }
        
        // Set default values if not provided
        if (driver.getId() == null) {
            driver.setId(UUID.randomUUID().toString());
        }
        if (driver.getRating() == 0) {
            driver.setRating(5.0); // Default rating
        }
        
        return driverRepository.save(driver);
    }
    
    public Driver updateDriver(String id, Driver driverDetails) {
        Driver existingDriver = getDriverById(id);
        
        // Update only the fields that are allowed to be updated
        if (driverDetails.getFullName() != null) {
            existingDriver.setFullName(driverDetails.getFullName());
        }
        if (driverDetails.getContactNumber() != null) {
            existingDriver.setContactNumber(driverDetails.getContactNumber());
        }
        if (driverDetails.getLicensePlate() != null) {
            existingDriver.setLicensePlate(driverDetails.getLicensePlate());
        }
        if (driverDetails.getVehicleModel() != null) {
            existingDriver.setVehicleModel(driverDetails.getVehicleModel());
        }
        if (driverDetails.getCurrentLocation() != null) {
            existingDriver.setCurrentLocation(driverDetails.getCurrentLocation());
        }
        if (driverDetails.getRating() > 0) {
            existingDriver.setRating(driverDetails.getRating());
        }
        
        return driverRepository.save(existingDriver);
    }
    
    public void deleteDriver(String id) {
        // Just pass the ID to delete
        driverRepository.delete(id);
    }

    public Driver approveDriver(String driverId) {
        Driver driver = getDriverById(driverId);
        
        User user = userRepository.findById(driver.getUserId())
                .orElseThrow(() -> new TaxiServiceException("User not found"));
        
        if (user.getRole() != User.UserRole.DRIVER) {
            throw new TaxiServiceException("User is not registered as a driver");
        }

        driver.setAvailability(true);
        return driverRepository.save(driver);
    }

    public Driver suspendDriver(String driverId) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new TaxiServiceException("Driver not found"));
        
        driver.setAvailability(false);
        return driverRepository.save(driver);
    }

    public List<BookingDTO> getAllBookings() {
        return bookingRepository.findAll().stream()
                .map(BookingDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public String generateReport() {
        StringBuilder report = new StringBuilder();
        
        // Total users
        long totalUsers = userRepository.findAll().size();
        report.append("Total Users: ").append(totalUsers).append("\n");
        
        // Total drivers
        long totalDrivers = driverRepository.findAll().size();
        report.append("Total Drivers: ").append(totalDrivers).append("\n");
        
        // Total bookings
        long totalBookings = bookingRepository.findAll().size();
        report.append("Total Bookings: ").append(totalBookings).append("\n");
        
        // Booking status breakdown
        long pendingBookings = bookingRepository.findAll().stream()
                .filter(b -> b.getStatus() == Booking.BookingStatus.PENDING)
                .count();
        report.append("Pending Bookings: ").append(pendingBookings).append("\n");
        
        long completedBookings = bookingRepository.findAll().stream()
                .filter(b -> b.getStatus() == Booking.BookingStatus.COMPLETED)
                .count();
        report.append("Completed Bookings: ").append(completedBookings).append("\n");
        
        // Total revenue
        double totalRevenue = bookingRepository.findAll().stream()
                .filter(b -> b.getStatus() == Booking.BookingStatus.COMPLETED)
                .mapToDouble(Booking::getFare)
                .sum();
        report.append("Total Revenue: $").append(String.format("%.2f", totalRevenue)).append("\n");
        
        return report.toString();
    }
} 