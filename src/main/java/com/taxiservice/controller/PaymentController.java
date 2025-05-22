package com.taxiservice.controller;

import com.taxiservice.dto.ApiResponse;
import com.taxiservice.model.Payment;
import com.taxiservice.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Payment>>> getAllPayments() {
        List<Payment> payments = paymentService.getAllPayments();
        return ResponseEntity.ok(ApiResponse.success(payments));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Payment>> getPaymentById(@PathVariable String id) {
        Payment payment = paymentService.getPaymentById(id);
        return ResponseEntity.ok(ApiResponse.success(payment));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<Payment>>> getPaymentsByUserId(@PathVariable String userId) {
        List<Payment> payments = paymentService.getPaymentsByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success(payments));
    }

    @GetMapping("/driver/{driverId}")
    public ResponseEntity<ApiResponse<List<Payment>>> getPaymentsByDriverId(@PathVariable String driverId) {
        List<Payment> payments = paymentService.getPaymentsByDriverId(driverId);
        return ResponseEntity.ok(ApiResponse.success(payments));
    }

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<ApiResponse<List<Payment>>> getPaymentsByBookingId(@PathVariable String bookingId) {
        List<Payment> payments = paymentService.getPaymentsByBookingId(bookingId);
        return ResponseEntity.ok(ApiResponse.success(payments));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Payment>> processPayment(@Valid @RequestBody Payment payment) {
        Payment processedPayment = paymentService.processPayment(payment);
        return new ResponseEntity<>(ApiResponse.success("Payment processed successfully", processedPayment), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Payment>> updatePayment(
            @PathVariable String id, 
            @Valid @RequestBody Payment paymentDetails) {
        Payment updatedPayment = paymentService.updatePayment(id, paymentDetails);
        return ResponseEntity.ok(ApiResponse.success("Payment updated successfully", updatedPayment));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePayment(@PathVariable String id) {
        paymentService.deletePayment(id);
        return ResponseEntity.ok(ApiResponse.success("Payment deleted successfully", null));
    }
}
