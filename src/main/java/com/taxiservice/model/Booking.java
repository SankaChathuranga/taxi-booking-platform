package com.taxiservice.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Booking {
    private String id;
    private String userId;
    private String driverId;
    private String pickupLocation;
    private String dropLocation;
    private LocalDateTime bookingTime;
    private BookingStatus status;
    private double fare;
    private Double rating;

    public enum BookingStatus {
        PENDING,
        ACCEPTED,
        COMPLETED,
        CANCELLED
    }
} 