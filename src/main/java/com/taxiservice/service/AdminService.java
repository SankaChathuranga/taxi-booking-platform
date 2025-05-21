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
import org.springframework.stereotype.Service;
import java.util.List;
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

    public List<Driver> getAllDrivers() {
        return driverRepository.findAll();
    }

    public Driver approveDriver(String driverId) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new TaxiServiceException("Driver not found"));
        
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