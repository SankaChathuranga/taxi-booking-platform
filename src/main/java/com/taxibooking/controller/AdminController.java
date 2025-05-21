package com.taxiservice.controller;

import com.taxiservice.dto.ApiResponse;
import com.taxiservice.dto.BookingDTO;
import com.taxiservice.dto.UserDTO;
import com.taxiservice.model.Driver;
import com.taxiservice.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
    public ResponseEntity<ApiResponse<List<Driver>>> getAllDrivers() {
        List<Driver> drivers = adminService.getAllDrivers();
        return ResponseEntity.ok(ApiResponse.success(drivers));
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
    public ResponseEntity<ApiResponse<List<BookingDTO>>> getAllBookings() {
        List<BookingDTO> bookings = adminService.getAllBookings();
        return ResponseEntity.ok(ApiResponse.success(bookings));
    }

    @GetMapping("/reports")
    public ResponseEntity<ApiResponse<String>> generateReport() {
        String report = adminService.generateReport();
        return ResponseEntity.ok(ApiResponse.success(report));
    }
} 