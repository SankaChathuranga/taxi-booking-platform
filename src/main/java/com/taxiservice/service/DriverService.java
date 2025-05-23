package com.taxiservice.service;

import com.taxiservice.exception.TaxiServiceException;
import com.taxiservice.model.Driver;
import com.taxiservice.model.User;
import com.taxiservice.repository.DriverRepository;
import com.taxiservice.repository.UserRepository;
import com.taxiservice.util.BubbleSortImplementation;
import com.taxiservice.util.CustomDriverList;
import com.taxiservice.util.CustomBubbleSortImplementation;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class for managing drivers
 */
@Service
public class DriverService {
    private final DriverRepository driverRepository;
    private final UserRepository userRepository;

    public DriverService(DriverRepository driverRepository, UserRepository userRepository) {
        this.driverRepository = driverRepository;
        this.userRepository = userRepository;
    }

    /**
     * Register a new driver
     * @param driver The driver to register
     * @param userId The user ID to associate with the driver
     * @return The registered driver
     */
    public Driver registerDriver(Driver driver, String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new TaxiServiceException("User not found"));
        
        if (user.getRole() != User.UserRole.DRIVER) {
            throw new TaxiServiceException("User is not registered as a driver");
        }

        driver.setUserId(userId);
        driver.setRating(0.0);
        driver.setAvailability(true);
        driver.setRegistrationDate(LocalDate.now());
        
        // Default to FULL_TIME if not specified
        if (driver.getDriverType() == null) {
            driver.setDriverType(Driver.DriverType.FULL_TIME);
        }
        
        return driverRepository.save(driver);
    }
    
    /**
     * Register a driver of a specific type
     * @param driver The driver details
     * @param userId The user ID to associate with the driver
     * @param driverType The type of driver to register
     * @return The registered driver
     */
    public Driver registerDriverWithType(Driver driver, String userId, Driver.DriverType driverType) {
        driver.setDriverType(driverType);
        return registerDriver(driver, userId);
    }

    /**
     * Update driver details
     * @param id The ID of the driver to update
     * @param driver The updated driver details
     * @return The updated driver
     */
    public Driver updateDriverDetails(String id, Driver driver) {
        Driver existingDriver = driverRepository.findById(id)
                .orElseThrow(() -> new TaxiServiceException("Driver not found"));
        
        // Preserve important fields
        driver.setId(id);
        driver.setUserId(existingDriver.getUserId());
        driver.setRating(existingDriver.getRating());
        driver.setAvailability(existingDriver.isAvailability());
        driver.setDriverType(existingDriver.getDriverType());
        driver.setRegistrationDate(existingDriver.getRegistrationDate());
        
        return driverRepository.save(driver);
    }

    /**
     * Get a driver by ID
     * @param id The ID of the driver to get
     * @return The driver
     */
    public Driver getDriverById(String id) {
        return driverRepository.findById(id)
                .orElseThrow(() -> new TaxiServiceException("Driver not found"));
    }
    
    /**
     * Get a driver by user ID
     * @param userId The user ID of the driver to get
     * @return The driver if found
     */
    public Optional<Driver> getDriverByUserId(String userId) {
        return driverRepository.findByUserId(userId);
    }

    /**
     * Get all drivers
     * @return List of all drivers
     */
    public List<Driver> getAllDrivers() {
        return driverRepository.findAll();
    }
    
    /**
     * Get all drivers of a specific type
     * @param driverType The type of drivers to get
     * @return List of drivers of the specified type
     */
    public List<Driver> getDriversByType(Driver.DriverType driverType) {
        return driverRepository.findAll().stream()
                .filter(driver -> driver.getDriverType() == driverType)
                .collect(Collectors.toList());
    }

    /**
     * Update driver availability
     * @param id The ID of the driver to update
     * @param availability The new availability status
     * @return The updated driver
     */
    public Driver updateAvailability(String id, boolean availability) {
        Driver driver = getDriverById(id);
        driver.setAvailability(availability);
        return driverRepository.save(driver);
    }

    /**
     * Update driver rating
     * @param id The ID of the driver to update
     * @param rating The new rating
     * @return The updated driver
     */
    public Driver updateRating(String id, double rating) {
        if (rating < 0 || rating > 5) {
            throw new TaxiServiceException("Rating must be between 0 and 5");
        }
        
        Driver driver = getDriverById(id);
        driver.setRating(rating);
        return driverRepository.save(driver);
    }

    /**
     * Get all drivers sorted by rating using our custom bubble sort implementation
     * @return List of drivers sorted by rating in descending order
     */
    public List<Driver> getSortedDriversByRating() {
        // First get all drivers from repository
        List<Driver> allDrivers = driverRepository.findAll();
        
        // Convert to our custom driver list
        CustomDriverList customDriverList = new CustomDriverList(allDrivers.size());
        for (Driver driver : allDrivers) {
            customDriverList.add(driver);
        }
        
        // Sort using our custom bubble sort implementation
        CustomBubbleSortImplementation.sort(customDriverList, CustomBubbleSortImplementation.SortCriteria.RATING);
        
        // Convert back to List for compatibility with the rest of the application
        List<Driver> result = allDrivers;
        result.clear();
        for (int i = 0; i < customDriverList.size(); i++) {
            result.add(customDriverList.get(i));
        }
        
        return result;
    }
    
    /**
     * Get all drivers sorted by the specified criteria using our custom bubble sort implementation
     * @param criteria The criteria to sort by
     * @return List of sorted drivers
     */
    public List<Driver> getSortedDrivers(BubbleSortImplementation.SortCriteria criteria) {
        // First get all drivers from repository
        List<Driver> allDrivers = driverRepository.findAll();
        
        // Convert to our custom driver list
        CustomDriverList customDriverList = new CustomDriverList(allDrivers.size());
        for (Driver driver : allDrivers) {
            customDriverList.add(driver);
        }
        
        // Convert the sorting criteria
        CustomBubbleSortImplementation.SortCriteria customCriteria;
        switch (criteria) {
            case RATING:
                customCriteria = CustomBubbleSortImplementation.SortCriteria.RATING;
                break;
            case NAME:
                customCriteria = CustomBubbleSortImplementation.SortCriteria.NAME;
                break;
            case REGISTRATION_DATE:
                customCriteria = CustomBubbleSortImplementation.SortCriteria.REGISTRATION_DATE;
                break;
            default:
                customCriteria = CustomBubbleSortImplementation.SortCriteria.RATING;
        }
        
        // Sort using our custom bubble sort implementation
        CustomBubbleSortImplementation.sort(customDriverList, customCriteria);
        
        // Convert back to List for compatibility with the rest of the application
        List<Driver> result = allDrivers;
        result.clear();
        for (int i = 0; i < customDriverList.size(); i++) {
            result.add(customDriverList.get(i));
        }
        
        return result;
    }

    /**
     * Get all available drivers
     * @return List of available drivers
     */
    public List<Driver> getAvailableDrivers() {
        return driverRepository.findAvailableDrivers();
    }
    
    /**
     * Find drivers by their experience level
     * @param minExperience Minimum years of experience required
     * @return List of drivers with the required experience
     */
    public List<Driver> getDriversByExperience(int minExperience) {
        return driverRepository.findByExperienceGreaterThanEqual(minExperience);
    }
    
    /**
     * Find drivers by minimum rating
     * @param minRating Minimum rating required
     * @return List of drivers with the required rating
     */
    public List<Driver> getDriversByMinimumRating(double minRating) {
        return driverRepository.findByRatingGreaterThanEqual(minRating);
    }
    
    /**
     * Get top performing drivers
     * @param limit Maximum number of drivers to return
     * @return List of top performing drivers
     */
    public List<Driver> getTopPerformingDrivers(int limit) {
        return driverRepository.findTopPerformers(limit);
    }
    
    /**
     * Delete a driver by ID
     * @param id The ID of the driver to delete
     */
    public void deleteDriver(String id) {
        // Check if the driver exists first
        getDriverById(id);
        driverRepository.deleteById(id);
    }
    
    /**
     * Calculate fare based on driver type and trip details
     * @param driverId The ID of the driver
     * @param distance The distance of the trip in kilometers
     * @param time The time of the trip in minutes
     * @return The calculated fare
     */
    public double calculateDriverFare(String driverId, double distance, double time) {
        Driver driver = getDriverById(driverId);
        return driver.calculateFare(distance, time);
    }
}