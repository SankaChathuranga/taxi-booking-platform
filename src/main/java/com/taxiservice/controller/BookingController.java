package com.taxiservice.controller;

import com.taxiservice.dto.ApiResponse;
import com.taxiservice.dto.BookingDTO;
import com.taxiservice.service.BookingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

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

    /**
     * Get all bookings with optional status filter
     */
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<BookingDTO>>> getAllBookings(
            @RequestParam(required = false) String status) {
        List<BookingDTO> bookings = bookingService.getAllBookings(status);
        return ResponseEntity.ok(ApiResponse.success(bookings));
    }

    /**
     * Get all bookings in the queue (FIFO order)
     */
    @GetMapping("/queue")
    public ResponseEntity<ApiResponse<List<BookingDTO>>> getBookingQueue() {
        List<BookingDTO> queuedBookings = bookingService.getQueuedBookings();
        return ResponseEntity.ok(ApiResponse.success(queuedBookings));
    }

    /**
     * Get the next booking in the queue
     */
    @GetMapping("/queue/next")
    public ResponseEntity<ApiResponse<BookingDTO>> getNextBooking() {
        Optional<BookingDTO> nextBooking = bookingService.getNextBooking();
        if (nextBooking.isPresent()) {
            return ResponseEntity.ok(ApiResponse.success(nextBooking.get()));
        } else {
            return ResponseEntity.ok(ApiResponse.success("No bookings in queue", null));
        }
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

    @PostMapping("/{id}/rate")
    public ResponseEntity<ApiResponse<BookingDTO>> rateBooking(
            @PathVariable String id,
            @RequestBody RatingRequest ratingRequest) {
        BookingDTO booking = bookingService.rateBooking(id, ratingRequest.getRating());
        return ResponseEntity.ok(ApiResponse.success("Rating submitted successfully", booking));
    }

    @PostMapping("/{id}/assign-driver")
    public ResponseEntity<ApiResponse<BookingDTO>> assignDriverToBooking(@PathVariable String id) {
        BookingDTO booking = bookingService.assignDriverToBooking(id);
        return ResponseEntity.ok(ApiResponse.success("Driver assigned successfully", booking));
    }
    
    /**
     * Simple class to hold rating request data
     */
    static class RatingRequest {
        private double rating;
        private String comment; // Optional comment field
        
        public double getRating() {
            return rating;
        }
        
        public void setRating(double rating) {
            this.rating = rating;
        }
        
        public String getComment() {
            return comment;
        }
        
        public void setComment(String comment) {
            this.comment = comment;
        }
    }
} 