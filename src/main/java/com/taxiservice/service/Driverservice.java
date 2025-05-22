package com.taxiservice.service;

import com.taxiservice.exception.TaxiServiceException;
import com.taxiservice.model.Driver;
import com.taxiservice.model.User;
import com.taxiservice.repository.DriverRepository;
import com.taxiservice.repository.UserRepository;
import com.taxiservice.util.BubbleSortImplementation;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DriverService {
    private final DriverRepository driverRepository;
    private final UserRepository userRepository;

    public DriverService(DriverRepository driverRepository, UserRepository userRepository) {
        this.driverRepository = driverRepository;
        this.userRepository = userRepository;
    }

    public Driver registerDriver(Driver driver, String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new TaxiServiceException("User not found"));
        
        if (user.getRole() != User.UserRole.DRIVER) {
            throw new TaxiServiceException("User is not registered as a driver");
        }

        driver.setUserId(userId);
        driver.setRating(0.0);
        driver.setAvailability(true);
        return driverRepository.save(driver);
    }

    public Driver updateDriverDetails(String id, Driver driver) {
        Driver existingDriver = driverRepository.findById(id)
                .orElseThrow(() -> new TaxiServiceException("Driver not found"));
        
        driver.setId(id);
        driver.setUserId(existingDriver.getUserId());
        driver.setRating(existingDriver.getRating());
        driver.setAvailability(existingDriver.isAvailability());
        
        return driverRepository.save(driver);
    }

    public Driver getDriverById(String id) {
        return driverRepository.findById(id)
                .orElseThrow(() -> new TaxiServiceException("Driver not found"));
    }

    public List<Driver> getAllDrivers() {
        return driverRepository.findAll();
    }

    public Driver updateAvailability(String id, boolean availability) {
        Driver driver = getDriverById(id);
        driver.setAvailability(availability);
        return driverRepository.save(driver);
    }

    public Driver updateRating(String id, double rating) {
        Driver driver = getDriverById(id);
        driver.setRating(rating);
        return driverRepository.save(driver);
    }

    public List<Driver> getSortedDriversByRating() {
        List<Driver> drivers = driverRepository.findAll();
        BubbleSortImplementation.sort(drivers);
        return drivers;
    }

    public List<Driver> getAvailableDrivers() {
        return driverRepository.findAvailableDrivers();
    }
} 