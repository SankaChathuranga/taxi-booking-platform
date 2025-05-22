package com.taxiservice.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    private String id;
    
    @NotBlank(message = "Booking ID is required")
    private String bookingId;
    
    @NotBlank(message = "User ID is required")
    private String userId;
    
    private String driverId;
    
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private Double amount;
    
    @NotBlank(message = "Payment method is required")
    private String paymentMethod; // CASH, CREDIT_CARD, DEBIT_CARD, etc.
    
    private String transactionId;
    
    private LocalDateTime paymentTime;
    
    private String status; // PENDING, COMPLETED, FAILED
    
    private String notes;
}
