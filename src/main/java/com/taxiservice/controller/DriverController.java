package com.taxiservice.controller;

import com.taxiservice.dto.ApiResponse;
import com.taxiservice.model.Driver;
import com.taxiservice.service.DriverService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@lombok.Data
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
} 