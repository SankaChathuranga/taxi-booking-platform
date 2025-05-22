package com.taxiservice.controller;

import com.taxiservice.dto.ApiResponse;
import com.taxiservice.dto.BookingDTO;
import com.taxiservice.service.BookingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {
    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<BookingDTO>> createBooking(@RequestBody BookingDTO bookingDTO) {
        BookingDTO booking = bookingService.createBooking(bookingDTO);
        return ResponseEntity.ok(ApiResponse.success("Booking created successfully", booking));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<BookingDTO>>> getUserBookings(@PathVariable String userId) {
        List<BookingDTO> bookings = bookingService.getBookingHistory(userId);
        return ResponseEntity.ok(ApiResponse.success(bookings));
    }

    @GetMapping("/driver/{driverId}")
    public ResponseEntity<ApiResponse<List<BookingDTO>>> getDriverBookings(@PathVariable String driverId) {
        List<BookingDTO> bookings = bookingService.getDriverBookings(driverId);
        return ResponseEntity.ok(ApiResponse.success(bookings));
    }

    @PutMapping("/{id}/accept")
    public ResponseEntity<ApiResponse<BookingDTO>> acceptBooking(@PathVariable String id) {
        BookingDTO booking = bookingService.acceptBooking(id);
        return ResponseEntity.ok(ApiResponse.success("Booking accepted successfully", booking));
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<ApiResponse<BookingDTO>> completeBooking(@PathVariable String id) {
        BookingDTO booking = bookingService.completeBooking(id);
        return ResponseEntity.ok(ApiResponse.success("Booking completed successfully", booking));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<BookingDTO>> cancelBooking(@PathVariable String id) {
        BookingDTO booking = bookingService.cancelBooking(id);
        return ResponseEntity.ok(ApiResponse.success("Booking cancelled successfully", booking));
    }

    @PutMapping("/{id}/rate")
    public ResponseEntity<ApiResponse<BookingDTO>> rateBooking(
            @PathVariable String id,
            @RequestParam double rating) {
        BookingDTO booking = bookingService.rateBooking(id, rating);
        return ResponseEntity.ok(ApiResponse.success("Rating submitted successfully", booking));
    }
} 