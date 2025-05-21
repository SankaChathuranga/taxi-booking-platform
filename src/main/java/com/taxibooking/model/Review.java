package com.taxibooking.model;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public abstract class Review {
    private String reviewId;
    private String bookingId;
    private String customerId;
    private String driverId;
    protected int rating;
    private LocalDateTime reviewTime;
    private boolean isEdited;
    private LocalDateTime lastEditedTime;
    private String status;

    public enum ReviewStatus {
        PENDING,
        APPROVED,
        REJECTED
    }

    public Review() {
        this.reviewId = UUID.randomUUID().toString();
        this.reviewTime = LocalDateTime.now();
        this.status = ReviewStatus.PENDING.name();
        this.isEdited = false;
    }

    public Review(String bookingId, String customerId, String driverId, int rating) {
        this();
        this.bookingId = bookingId;
        this.customerId = customerId;
        this.driverId = driverId;
        this.rating = rating;
    }

    // Abstract method to get review summary
    public abstract String getReviewSummary();

    // Method to validate rating
    public boolean isValidRating() {
        return rating >= 1 && rating <= 5;
    }

    // Method to mark review as edited
    public void markAsEdited() {
        this.isEdited = true;
        this.lastEditedTime = LocalDateTime.now();
    }

    // Method to update review status
    public void updateStatus(ReviewStatus newStatus) {
        this.status = newStatus.name();
    }

    // Method to check if review is approved
    public boolean isApproved() {
        return ReviewStatus.APPROVED.name().equals(status);
    }

    // Method to check if review is pending
    public boolean isPending() {
        return ReviewStatus.PENDING.name().equals(status);
    }

    // Method to check if review is rejected
    public boolean isRejected() {
        return ReviewStatus.REJECTED.name().equals(status);
    }
} 