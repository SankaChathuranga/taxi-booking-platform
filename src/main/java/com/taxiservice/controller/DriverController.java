package com.taxiservice.controller;

import com.taxiservice.dto.ApiResponse;
import com.taxiservice.model.Driver;
import com.taxiservice.service.DriverService;
import com.taxiservice.util.BubbleSortImplementation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/drivers")
public class DriverController {
    private final DriverService driverService;

    public DriverController(DriverService driverService) {
        this.driverService = driverService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Driver>> registerDriver(@RequestBody Driver driver, @RequestParam String userId) {
        Driver registeredDriver = driverService.registerDriver(driver, userId);
        return ResponseEntity.ok(ApiResponse.success("Driver registered successfully", registeredDriver));
    }

    @PostMapping("/register/{type}")
    public ResponseEntity<ApiResponse<Driver>> registerDriverWithType(
            @RequestBody Driver driver,
            @RequestParam String userId,
            @PathVariable("type") String type) {

        Driver.DriverType driverType = Driver.DriverType.valueOf(type.toUpperCase());
        Driver registeredDriver = driverService.registerDriverWithType(driver, userId, driverType);
        return ResponseEntity.ok(ApiResponse.success("Driver registered successfully", registeredDriver));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Driver>> getDriverDetails(@PathVariable String id) {
        Driver driver = driverService.getDriverById(id);
        return ResponseEntity.ok(ApiResponse.success(driver));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Driver>> updateDriver(@PathVariable String id, @RequestBody Driver driver) {
        Driver updatedDriver = driverService.updateDriverDetails(id, driver);
        return ResponseEntity.ok(ApiResponse.success("Driver updated successfully", updatedDriver));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Driver>>> getAllDrivers() {
        List<Driver> drivers = driverService.getAllDrivers();
        return ResponseEntity.ok(ApiResponse.success(drivers));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<ApiResponse<List<Driver>>> getDriversByType(@PathVariable("type") String type) {
        Driver.DriverType driverType = Driver.DriverType.valueOf(type.toUpperCase());
        List<Driver> drivers = driverService.getDriversByType(driverType);
        return ResponseEntity.ok(ApiResponse.success(drivers));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<Driver>> getDriverByUserId(@PathVariable String userId) {
        Optional<Driver> driver = driverService.getDriverByUserId(userId);
        return driver.map(d -> ResponseEntity.ok(ApiResponse.success(d)))
                .orElseGet(() -> ResponseEntity.ok(ApiResponse.error("Driver not found")));
    }

    @PutMapping("/{id}/availability")
    public ResponseEntity<ApiResponse<Driver>> updateAvailability(
            @PathVariable String id,
            @RequestParam boolean availability) {
        Driver driver = driverService.updateAvailability(id, availability);
        return ResponseEntity.ok(ApiResponse.success("Availability updated successfully", driver));
    }

    @GetMapping("/available")
    public ResponseEntity<ApiResponse<List<Driver>>> getAvailableDrivers() {
        List<Driver> drivers = driverService.getAvailableDrivers();
        return ResponseEntity.ok(ApiResponse.success(drivers));
    }

    @GetMapping("/sorted-by-rating")
    public ResponseEntity<ApiResponse<List<Driver>>> getSortedDriversByRating() {
        List<Driver> drivers = driverService.getSortedDriversByRating();
        return ResponseEntity.ok(ApiResponse.success(drivers));
    }

    @GetMapping("/sorted/{criteria}")
    public ResponseEntity<ApiResponse<List<Driver>>> getSortedDrivers(@PathVariable String criteria) {
        BubbleSortImplementation.SortCriteria sortCriteria = BubbleSortImplementation.SortCriteria.valueOf(criteria.toUpperCase());
        List<Driver> drivers = driverService.getSortedDrivers(sortCriteria);
        return ResponseEntity.ok(ApiResponse.success(drivers));
    }

    @GetMapping("/top-performers/{limit}")
    public ResponseEntity<ApiResponse<List<Driver>>> getTopPerformers(@PathVariable int limit) {
        List<Driver> drivers = driverService.getTopPerformingDrivers(limit);
        return ResponseEntity.ok(ApiResponse.success(drivers));
    }
    
    /**
     * Get driver name by ID - simplified endpoint specifically for UI components like ratings
     * @param id The ID of the driver
     * @return The driver's name or "Unknown Driver" if not found
     */
    @GetMapping("/{id}/name")
    public ResponseEntity<ApiResponse<String>> getDriverName(@PathVariable String id) {
        try {
            Driver driver = driverService.getDriverById(id);
            return ResponseEntity.ok(ApiResponse.success(driver.getFullName()));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.success("Unknown Driver"));
        }
    }

    @GetMapping("/minimum-rating/{rating}")
    public ResponseEntity<ApiResponse<List<Driver>>> getDriversByMinimumRating(@PathVariable double rating) {
        List<Driver> drivers = driverService.getDriversByMinimumRating(rating);
        return ResponseEntity.ok(ApiResponse.success(drivers));
    }

    @GetMapping("/minimum-experience/{experience}")
    public ResponseEntity<ApiResponse<List<Driver>>> getDriversByExperience(@PathVariable int experience) {
        List<Driver> drivers = driverService.getDriversByExperience(experience);
        return ResponseEntity.ok(ApiResponse.success(drivers));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteDriver(@PathVariable String id) {
        driverService.deleteDriver(id);
        return ResponseEntity.ok(ApiResponse.success("Driver deleted successfully", null));
    }

    @GetMapping("/{id}/calculate-fare")
    public ResponseEntity<ApiResponse<Double>> calculateFare(
            @PathVariable String id,
            @RequestParam double distance,
            @RequestParam double time) {
        double fare = driverService.calculateDriverFare(id, distance, time);
        return ResponseEntity.ok(ApiResponse.success(fare));
    }
}