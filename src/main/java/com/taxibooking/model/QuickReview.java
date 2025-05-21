package com.taxibooking.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class QuickReview extends Review {
    private String shortComment;
    private static final int MAX_COMMENT_LENGTH = 100;

    public QuickReview() {
        super();
    }

    public QuickReview(String bookingId, String customerId, String driverId, int rating, String shortComment) {
        super(bookingId, customerId, driverId, rating);
        this.shortComment = shortComment != null ? 
            shortComment.substring(0, Math.min(shortComment.length(), MAX_COMMENT_LENGTH)) : null;
    }

    @Override
    public String getReviewSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("★".repeat(rating));
        summary.append("☆".repeat(5 - rating));
        if (shortComment != null && !shortComment.isEmpty()) {
            summary.append("\n\n").append(shortComment);
        }
        return summary.toString();
    }

    // Method to validate short comment length
    public boolean isValidCommentLength() {
        return shortComment == null || shortComment.length() <= MAX_COMMENT_LENGTH;
    }

    // Method to get rating stars as HTML
    public String getRatingStarsHtml() {
        StringBuilder stars = new StringBuilder();
        stars.append("<div class=\"rating-stars\">");
        for (int i = 1; i <= 5; i++) {
            if (i <= rating) {
                stars.append("<i class=\"bi bi-star-fill text-warning\"></i>");
            } else {
                stars.append("<i class=\"bi bi-star text-warning\"></i>");
            }
        }
        stars.append("</div>");
        return stars.toString();
    }
} 