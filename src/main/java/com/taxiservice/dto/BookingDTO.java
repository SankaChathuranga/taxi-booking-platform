package com.taxiservice.dto;

import com.taxiservice.model.Booking;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BookingDTO {
    private String id;
    private String userId;
    private String driverId;
    private String pickupLocation;
    private String dropLocation;
    private LocalDateTime bookingTime;
    private Booking.BookingStatus status;
    private double fare;
    private Double rating;

    public static BookingDTO fromEntity(Booking booking) {
        BookingDTO dto = new BookingDTO();
        dto.setId(booking.getId());
        dto.setUserId(booking.getUserId());
        dto.setDriverId(booking.getDriverId());
        dto.setPickupLocation(booking.getPickupLocation());
        dto.setDropLocation(booking.getDropLocation());
        dto.setBookingTime(booking.getBookingTime());
        dto.setStatus(booking.getStatus());
        dto.setFare(booking.getFare());
        dto.setRating(booking.getRating());
        return dto;
    }

    public Booking toEntity() {
        Booking booking = new Booking();
        booking.setId(this.id);
        booking.setUserId(this.userId);
        booking.setDriverId(this.driverId);
        booking.setPickupLocation(this.pickupLocation);
        booking.setDropLocation(this.dropLocation);
        booking.setBookingTime(this.bookingTime);
        booking.setStatus(this.status);
        booking.setFare(this.fare);
        booking.setRating(this.rating);
        return booking;
    }
} 