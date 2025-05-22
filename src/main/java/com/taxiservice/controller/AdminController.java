package com.taxiservice.controller;

import com.taxiservice.dto.ApiResponse;
import com.taxiservice.dto.BookingDTO;
import com.taxiservice.dto.UserDTO;
import com.taxiservice.model.Driver;
import com.taxiservice.service.AdminService;
<<<<<<< HEAD
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
=======
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
>>>>>>> ff35299 (FIXED SOME ERRORS)
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<UserDTO>>> getAllUsers() {
        List<UserDTO> users = adminService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    @GetMapping("/drivers")
<<<<<<< HEAD
    public ResponseEntity<ApiResponse<List<Driver>>> getAllDrivers() {
        List<Driver> drivers = adminService.getAllDrivers();
        return ResponseEntity.ok(ApiResponse.success(drivers));
    }

=======
    public ResponseEntity<ApiResponse<List<Driver>>> getAllDrivers(
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortOrder) {
        List<Driver> drivers = adminService.getAllDrivers(sortBy, sortOrder);
        return ResponseEntity.ok(ApiResponse.success(drivers));
    }

    @GetMapping("/drivers/{id}")
    public ResponseEntity<ApiResponse<Driver>> getDriverById(@PathVariable String id) {
        Driver driver = adminService.getDriverById(id);
        return ResponseEntity.ok(ApiResponse.success(driver));
    }

    @PostMapping("/drivers")
    public ResponseEntity<ApiResponse<Driver>> createDriver(@Valid @RequestBody Driver driver) {
        Driver createdDriver = adminService.createDriver(driver);
        return new ResponseEntity<>(ApiResponse.success("Driver created successfully", createdDriver), HttpStatus.CREATED);
    }

    @PutMapping("/drivers/{id}")
    public ResponseEntity<ApiResponse<Driver>> updateDriver(
            @PathVariable String id, 
            @Valid @RequestBody Driver driverDetails) {
        Driver updatedDriver = adminService.updateDriver(id, driverDetails);
        return ResponseEntity.ok(ApiResponse.success("Driver updated successfully", updatedDriver));
    }

    @DeleteMapping("/drivers/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteDriver(@PathVariable String id) {
        adminService.deleteDriver(id);
        return ResponseEntity.ok(ApiResponse.success("Driver deleted successfully", null));
    }

>>>>>>> ff35299 (FIXED SOME ERRORS)
    @PutMapping("/drivers/{id}/approve")
    public ResponseEntity<ApiResponse<Driver>> approveDriver(@PathVariable String id) {
        Driver driver = adminService.approveDriver(id);
        return ResponseEntity.ok(ApiResponse.success("Driver approved successfully", driver));
    }

    @PutMapping("/drivers/{id}/suspend")
    public ResponseEntity<ApiResponse<Driver>> suspendDriver(@PathVariable String id) {
        Driver driver = adminService.suspendDriver(id);
        return ResponseEntity.ok(ApiResponse.success("Driver suspended successfully", driver));
    }

    @GetMapping("/bookings")
    public ResponseEntity<ApiResponse<List<BookingDTO>>> getAllBookings() {
        List<BookingDTO> bookings = adminService.getAllBookings();
        return ResponseEntity.ok(ApiResponse.success(bookings));
    }

    @GetMapping("/reports")
    public ResponseEntity<ApiResponse<String>> generateReport() {
        String report = adminService.generateReport();
        return ResponseEntity.ok(ApiResponse.success(report));
    }
<<<<<<< HEAD
} 
=======
}
>>>>>>> ff35299 (FIXED SOME ERRORS)
