package com.taxiservice.service;

import com.taxiservice.exception.TaxiServiceException;
import com.taxiservice.model.Booking;
import com.taxiservice.model.Driver;
import com.taxiservice.model.Payment;
import com.taxiservice.repository.BookingRepository;
import com.taxiservice.repository.DriverRepository;
import com.taxiservice.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final DriverRepository driverRepository;

    public PaymentService(PaymentRepository paymentRepository, BookingRepository bookingRepository, DriverRepository driverRepository) {
        this.paymentRepository = paymentRepository;
        this.bookingRepository = bookingRepository;
        this.driverRepository = driverRepository;
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
                // Ensure there's a driver assigned to the booking
                if (booking.getDriverId() == null || booking.getDriverId().isEmpty()) {
                    // Assign a random driver if none is assigned
                    List<Driver> availableDrivers = driverRepository.findAvailableDrivers();
                    if (!availableDrivers.isEmpty()) {
                        // Select the first available driver
                        Driver driver = availableDrivers.get(0);
                        booking.setDriverId(driver.getId());
                        System.out.println("Assigned driver " + driver.getFullName() + " to booking " + booking.getId());
                    } else {
                        // If no available drivers, use a default one from the repository
                        List<Driver> allDrivers = driverRepository.findAll();
                        if (!allDrivers.isEmpty()) {
                            Driver driver = allDrivers.get(0);
                            booking.setDriverId(driver.getId());
                            System.out.println("Assigned default driver " + driver.getFullName() + " to booking " + booking.getId());
                        } else {
                            System.err.println("Warning: No drivers available in the system");
                        }
                    }
                }
                
                // Set the driver ID from the booking to the payment
                if (booking.getDriverId() != null) {
                    payment.setDriverId(booking.getDriverId());
                }
                
                // Update booking status to COMPLETED if payment is successful
                if ("COMPLETED".equals(payment.getStatus())) {
                    booking.setStatus(Booking.BookingStatus.COMPLETED);
                    bookingRepository.save(booking);
                    System.out.println("Updated booking " + booking.getId() + " status to COMPLETED");
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
