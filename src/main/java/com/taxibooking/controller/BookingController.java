package com.taxibooking.controller;

import com.taxibooking.model.Booking;
import com.taxibooking.model.AbstractDriver;
import com.taxibooking.service.BookingService;
import com.taxibooking.service.DriverManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/bookings")
public class BookingController {
    
    private final BookingService bookingService;
    private final DriverManagementService driverService;
    
    @Autowired
    public BookingController(BookingService bookingService, DriverManagementService driverService) {
        this.bookingService = bookingService;
        this.driverService = driverService;
    }
    
    // Booking creation and management
    @GetMapping("/new")
    public String showBookingForm(Model model) {
        model.addAttribute("vehicleTypes", List.of("SEDAN", "SUV", "VAN", "LUXURY"));
        return "booking/form";
    }
    
    @PostMapping("/create")
    public String createBooking(
            @RequestParam String customerId,
            @RequestParam String pickupLocation,
            @RequestParam String dropoffLocation,
            @RequestParam String vehicleType,
            @RequestParam(required = false) boolean isScheduled,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime scheduledTime) {
        try {
            Booking booking = bookingService.createBooking(
                customerId, pickupLocation, dropoffLocation, vehicleType, isScheduled, scheduledTime);
            return "redirect:/bookings/" + booking.getBookingId();
        } catch (IOException e) {
            return "error";
        }
    }
    
    @GetMapping("/{bookingId}")
    public String viewBooking(@PathVariable String bookingId, Model model) {
        try {
            Optional<Booking> bookingOpt = bookingService.getBookingById(bookingId);
            if (bookingOpt.isPresent()) {
                Booking booking = bookingOpt.get();
                model.addAttribute("booking", booking);
                
                if (booking.getDriverId() != null) {
                    Optional<AbstractDriver> driverOpt = driverService.getDriverById(booking.getDriverId());
                    driverOpt.ifPresent(driver -> model.addAttribute("driver", driver));
                }
                
                return "booking/details";
            }
            return "error";
        } catch (IOException e) {
            return "error";
        }
    }
    
    @GetMapping("/customer/{customerId}")
    public String viewCustomerBookings(@PathVariable String customerId, Model model) {
        try {
            List<Booking> bookings = bookingService.getCustomerBookings(customerId);
            model.addAttribute("bookings", bookings);
            model.addAttribute("customerId", customerId);
            return "booking/customer-history";
        } catch (IOException e) {
            return "error";
        }
    }
    
    @GetMapping("/driver/{driverId}")
    public String viewDriverBookings(@PathVariable String driverId, Model model) {
        try {
            List<Booking> bookings = bookingService.getDriverBookings(driverId);
            model.addAttribute("bookings", bookings);
            model.addAttribute("driverId", driverId);
            return "booking/driver-history";
        } catch (IOException e) {
            return "error";
        }
    }
    
    @GetMapping("/active")
    public String viewActiveBookings(Model model) {
        try {
            List<Booking> bookings = bookingService.getActiveBookings();
            model.addAttribute("bookings", bookings);
            model.addAttribute("title", "Active Bookings");
            return "booking/list";
        } catch (IOException e) {
            return "error";
        }
    }
    
    @GetMapping("/pending")
    public String viewPendingBookings(Model model) {
        try {
            List<Booking> bookings = bookingService.getPendingBookings();
            model.addAttribute("bookings", bookings);
            model.addAttribute("title", "Pending Bookings");
            return "booking/list";
        } catch (IOException e) {
            return "error";
        }
    }
    
    // Booking status updates
    @PostMapping("/{bookingId}/assign")
    @ResponseBody
    public ResponseEntity<String> assignDriver(
            @PathVariable String bookingId,
            @RequestParam String driverId) {
        try {
            bookingService.assignDriver(bookingId, driverId);
            return ResponseEntity.ok("Driver assigned successfully");
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Failed to assign driver");
        }
    }
    
    @PostMapping("/{bookingId}/start")
    @ResponseBody
    public ResponseEntity<String> startRide(@PathVariable String bookingId) {
        try {
            bookingService.startRide(bookingId);
            return ResponseEntity.ok("Ride started successfully");
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Failed to start ride");
        }
    }
    
    @PostMapping("/{bookingId}/complete")
    @ResponseBody
    public ResponseEntity<String> completeRide(
            @PathVariable String bookingId,
            @RequestParam double distance,
            @RequestParam double fare) {
        try {
            bookingService.completeRide(bookingId, distance, fare);
            return ResponseEntity.ok("Ride completed successfully");
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Failed to complete ride");
        }
    }
    
    @PostMapping("/{bookingId}/cancel")
    @ResponseBody
    public ResponseEntity<String> cancelBooking(@PathVariable String bookingId) {
        try {
            bookingService.cancelBooking(bookingId);
            return ResponseEntity.ok("Booking cancelled successfully");
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Failed to cancel booking");
        }
    }
    
    @PostMapping("/{bookingId}/no-show")
    @ResponseBody
    public ResponseEntity<String> markAsNoShow(@PathVariable String bookingId) {
        try {
            bookingService.markAsNoShow(bookingId);
            return ResponseEntity.ok("Booking marked as no-show");
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Failed to update booking status");
        }
    }
    
    @PostMapping("/{bookingId}/payment")
    @ResponseBody
    public ResponseEntity<String> updatePaymentStatus(
            @PathVariable String bookingId,
            @RequestParam Booking.PaymentStatus status) {
        try {
            bookingService.updatePaymentStatus(bookingId, status);
            return ResponseEntity.ok("Payment status updated successfully");
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Failed to update payment status");
        }
    }
    
    // API endpoints for other components
    @GetMapping("/api/{bookingId}")
    @ResponseBody
    public ResponseEntity<Booking> getBookingById(@PathVariable String bookingId) {
        try {
            Optional<Booking> bookingOpt = bookingService.getBookingById(bookingId);
            return bookingOpt.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/api/active")
    @ResponseBody
    public ResponseEntity<List<Booking>> getActiveBookings() {
        try {
            return ResponseEntity.ok(bookingService.getActiveBookings());
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/api/pending")
    @ResponseBody
    public ResponseEntity<List<Booking>> getPendingBookings() {
        try {
            return ResponseEntity.ok(bookingService.getPendingBookings());
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/api/queue-size")
    @ResponseBody
    public ResponseEntity<Integer> getPendingQueueSize() {
        return ResponseEntity.ok(bookingService.getPendingQueueSize());
    }
} 