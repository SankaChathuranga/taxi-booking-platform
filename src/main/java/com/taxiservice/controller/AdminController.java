package com.taxiservice.controller;

import com.taxiservice.dto.ApiResponse;
import com.taxiservice.dto.BookingDTO;
import com.taxiservice.dto.UserDTO;
import com.taxiservice.model.Driver;
import com.taxiservice.service.AdminService;
import com.taxiservice.service.BookingService;
import com.taxiservice.service.DriverService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final AdminService adminService;
    private final BookingService bookingService;
    private final DriverService driverService;

    public AdminController(AdminService adminService, BookingService bookingService, DriverService driverService) {
        this.adminService = adminService;
        this.bookingService = bookingService;
        this.driverService = driverService;
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<UserDTO>>> getAllUsers() {
        List<UserDTO> users = adminService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    @GetMapping("/drivers")
    public ResponseEntity<ApiResponse<List<Driver>>> getAllDrivers(
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortOrder) {
        List<Driver> drivers = adminService.getAllDrivers(sortBy, sortOrder);
        return ResponseEntity.ok(ApiResponse.success(drivers));
    }
    
    @GetMapping("/drivers/sort-by-rating")
    public ResponseEntity<ApiResponse<List<Driver>>> getDriversSortedByRating(
            @RequestParam(defaultValue = "false") boolean ascending) {
        List<Driver> drivers = adminService.getDriversSortedByRating(ascending);
        return ResponseEntity.ok(ApiResponse.success(drivers));
    }

    @GetMapping("/drivers/{id}")
    public ResponseEntity<ApiResponse<Driver>> getDriverById(@PathVariable String id) {
        Driver driver = adminService.getDriverById(id);
        return ResponseEntity.ok(ApiResponse.success(driver));
    }
    
    /**
     * Get driver name by ID - simplified endpoint specifically for UI components like ratings
     * @param id The ID of the driver
     * @return The driver's name or "Unknown Driver" if not found
     */
    @GetMapping("/drivers/{id}/name")
    public ResponseEntity<ApiResponse<String>> getDriverName(@PathVariable String id) {
        try {
            Driver driver = adminService.getDriverById(id);
            return ResponseEntity.ok(ApiResponse.success(driver.getFullName()));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.success("Unknown Driver"));
        }
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
    public ResponseEntity<ApiResponse<List<BookingDTO>>> getAllBookings(
            @RequestParam(required = false) String status) {
        List<BookingDTO> bookings = bookingService.getAllBookings(status);
        return ResponseEntity.ok(ApiResponse.success(bookings));
    }
    
    /**
     * Get all bookings in the queue (FIFO order)
     */
    @GetMapping("/bookings/queue")
    public ResponseEntity<ApiResponse<List<BookingDTO>>> getBookingQueue() {
        List<BookingDTO> queuedBookings = bookingService.getQueuedBookings();
        return ResponseEntity.ok(ApiResponse.success(queuedBookings));
    }
    
    /**
     * Get the next booking in the queue
     */
    @GetMapping("/bookings/queue/next")
    public ResponseEntity<ApiResponse<BookingDTO>> getNextBooking() {
        return bookingService.getNextBooking()
                .map(booking -> ResponseEntity.ok(ApiResponse.success(booking)))
                .orElse(ResponseEntity.ok(ApiResponse.success("No bookings in queue", null)));
    }
    
    /**
     * Get available drivers for assignment
     */
    @GetMapping("/drivers/available")
    public ResponseEntity<ApiResponse<List<Driver>>> getAvailableDrivers() {
        List<Driver> availableDrivers = driverService.getAvailableDrivers();
        return ResponseEntity.ok(ApiResponse.success(availableDrivers));
    }

    @GetMapping("/reports")
    public ResponseEntity<ApiResponse<String>> generateReport() {
        String report = adminService.generateReport();
        return ResponseEntity.ok(ApiResponse.success(report));
    }
}
