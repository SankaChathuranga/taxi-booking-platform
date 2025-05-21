package com.taxibooking.model;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class Booking {
    private String bookingId;
    private String customerId;
    private String driverId;
    private String pickupLocation;
    private String dropoffLocation;
    private LocalDateTime bookingTime;
    private LocalDateTime pickupTime;
    private LocalDateTime dropoffTime;
    private double distance;
    private double fare;
    private BookingStatus status;
    private PaymentStatus paymentStatus;
    private String notes;
    private String vehicleType;
    private boolean isScheduled;
    private LocalDateTime scheduledTime;

    public enum BookingStatus {
        PENDING,    // Initial state when booking is created
        CONFIRMED,  // Driver has accepted the booking
        IN_PROGRESS,// Driver has picked up the customer
        COMPLETED,  // Ride has been completed
        CANCELLED,  // Booking was cancelled
        NO_SHOW     // Customer didn't show up
    }

    public enum PaymentStatus {
        PENDING,
        PAID,
        REFUNDED,
        FAILED
    }

    public Booking() {
        this.bookingId = UUID.randomUUID().toString();
        this.bookingTime = LocalDateTime.now();
        this.status = BookingStatus.PENDING;
        this.paymentStatus = PaymentStatus.PENDING;
    }

    public Booking(String customerId, String pickupLocation, String dropoffLocation, 
                  String vehicleType, boolean isScheduled, LocalDateTime scheduledTime) {
        this();
        this.customerId = customerId;
        this.pickupLocation = pickupLocation;
        this.dropoffLocation = dropoffLocation;
        this.vehicleType = vehicleType;
        this.isScheduled = isScheduled;
        this.scheduledTime = scheduledTime;
    }

    public boolean isActive() {
        return status == BookingStatus.CONFIRMED || status == BookingStatus.IN_PROGRESS;
    }

    public boolean canBeCancelled() {
        return status == BookingStatus.PENDING || status == BookingStatus.CONFIRMED;
    }

    public void confirm(String driverId) {
        if (status == BookingStatus.PENDING) {
            this.driverId = driverId;
            this.status = BookingStatus.CONFIRMED;
        }
    }

    public void startRide() {
        if (status == BookingStatus.CONFIRMED) {
            this.status = BookingStatus.IN_PROGRESS;
            this.pickupTime = LocalDateTime.now();
        }
    }

    public void completeRide(double distance, double fare) {
        if (status == BookingStatus.IN_PROGRESS) {
            this.status = BookingStatus.COMPLETED;
            this.dropoffTime = LocalDateTime.now();
            this.distance = distance;
            this.fare = fare;
        }
    }

    public void cancel() {
        if (canBeCancelled()) {
            this.status = BookingStatus.CANCELLED;
            if (paymentStatus == PaymentStatus.PAID) {
                this.paymentStatus = PaymentStatus.REFUNDED;
            }
        }
    }

    public void markAsNoShow() {
        if (status == BookingStatus.CONFIRMED) {
            this.status = BookingStatus.NO_SHOW;
        }
    }

    public void updatePaymentStatus(PaymentStatus newStatus) {
        this.paymentStatus = newStatus;
    }

    public long getDurationInMinutes() {
        if (pickupTime != null && dropoffTime != null) {
            return java.time.Duration.between(pickupTime, dropoffTime).toMinutes();
        }
        return 0;
    }

    public boolean isOverdue() {
        if (isScheduled && scheduledTime != null) {
            return LocalDateTime.now().isAfter(scheduledTime.plusMinutes(15));
        }
        return false;
    }
} 