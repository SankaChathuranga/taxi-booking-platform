package com.taxiservice.service;

import com.taxiservice.exception.TaxiServiceException;
import com.taxiservice.model.Booking;
import com.taxiservice.model.Payment;
import com.taxiservice.repository.BookingRepository;
import com.taxiservice.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;

    public PaymentService(PaymentRepository paymentRepository, BookingRepository bookingRepository) {
        this.paymentRepository = paymentRepository;
        this.bookingRepository = bookingRepository;
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public Payment getPaymentById(String id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new TaxiServiceException("Payment not found with id: " + id));
    }

    public List<Payment> getPaymentsByUserId(String userId) {
        return paymentRepository.findByUserId(userId);
    }

    public List<Payment> getPaymentsByDriverId(String driverId) {
        return paymentRepository.findByDriverId(driverId);
    }

    public List<Payment> getPaymentsByBookingId(String bookingId) {
        return paymentRepository.findByBookingId(bookingId);
    }

    public Payment processPayment(Payment payment) {
        // Set default values if not provided
        if (payment.getId() == null) {
            payment.setId(UUID.randomUUID().toString());
        }
        if (payment.getPaymentTime() == null) {
            payment.setPaymentTime(LocalDateTime.now());
        }
        if (payment.getStatus() == null) {
            payment.setStatus("COMPLETED");
        }
        
        // Try to find the booking, but don't throw an exception if not found
        try {
            Booking booking = bookingRepository.findById(payment.getBookingId()).orElse(null);
            
            // If booking exists, update it
            if (booking != null) {
                // Set the driver ID from the booking if not provided
                if (payment.getDriverId() == null && booking.getDriverId() != null) {
                    payment.setDriverId(booking.getDriverId());
                }
                
                // Update booking status to COMPLETED if payment is successful
                if ("COMPLETED".equals(payment.getStatus())) {
                    booking.setStatus(Booking.BookingStatus.COMPLETED);
                    bookingRepository.save(booking);
                }
            }
        } catch (Exception e) {
            // Log the error but continue with payment processing
            System.err.println("Warning: Could not find or update booking: " + e.getMessage());
        }
        
        // Generate a random realistic fare amount if not provided or zero
        if (payment.getAmount() == null || payment.getAmount() == 0) {
            // Generate a random amount between $15 and $50
            double randomFare = 15 + Math.random() * 35;
            // Round to 2 decimal places
            randomFare = Math.round(randomFare * 100.0) / 100.0;
            payment.setAmount(randomFare);
        }
        
        // In a real application, we would process the payment with a payment gateway here
        // For this assignment, we'll just simulate a successful payment
        String transactionId = "TXN" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        payment.setTransactionId(transactionId);
        
        return paymentRepository.save(payment);
    }

    public Payment updatePayment(String id, Payment paymentDetails) {
        Payment existingPayment = getPaymentById(id);
        
        // Update only the fields that are allowed to be updated
        if (paymentDetails.getAmount() != null) {
            existingPayment.setAmount(paymentDetails.getAmount());
        }
        if (paymentDetails.getPaymentMethod() != null) {
            existingPayment.setPaymentMethod(paymentDetails.getPaymentMethod());
        }
        if (paymentDetails.getStatus() != null) {
            existingPayment.setStatus(paymentDetails.getStatus());
        }
        if (paymentDetails.getNotes() != null) {
            existingPayment.setNotes(paymentDetails.getNotes());
        }
        
        return paymentRepository.save(existingPayment);
    }

    public void deletePayment(String id) {
        // Check if payment exists
        getPaymentById(id);
        
        // Delete the payment
        paymentRepository.delete(id);
    }
}
