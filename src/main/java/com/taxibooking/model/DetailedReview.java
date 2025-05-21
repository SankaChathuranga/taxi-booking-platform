package com.taxibooking.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.HashMap;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
public class DetailedReview extends Review {
    private String detailedComment;
    private Map<String, Integer> categoryRatings;
    private static final int MAX_COMMENT_LENGTH = 500;
    private static final String[] RATING_CATEGORIES = {
        "Punctuality",
        "Professionalism",
        "Vehicle Condition",
        "Driving Skills",
        "Customer Service"
    };

    public DetailedReview() {
        super();
        this.categoryRatings = new HashMap<>();
        initializeCategoryRatings();
    }

    public DetailedReview(String bookingId, String customerId, String driverId, 
                         int rating, String detailedComment, 
                         Map<String, Integer> categoryRatings) {
        super(bookingId, customerId, driverId, rating);
        this.detailedComment = detailedComment != null ? 
            detailedComment.substring(0, Math.min(detailedComment.length(), MAX_COMMENT_LENGTH)) : null;
        this.categoryRatings = categoryRatings != null ? categoryRatings : new HashMap<>();
        initializeCategoryRatings();
    }

    private void initializeCategoryRatings() {
        for (String category : RATING_CATEGORIES) {
            if (!categoryRatings.containsKey(category)) {
                categoryRatings.put(category, 0);
            }
        }
    }

    @Override
    public String getReviewSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("★".repeat(rating));
        summary.append("☆".repeat(5 - rating));
        summary.append("\n\nCategory Ratings:\n");
        
        for (String category : RATING_CATEGORIES) {
            int categoryRating = categoryRatings.getOrDefault(category, 0);
            summary.append(category).append(": ");
            summary.append("★".repeat(categoryRating));
            summary.append("☆".repeat(5 - categoryRating));
            summary.append("\n");
        }
        
        if (detailedComment != null && !detailedComment.isEmpty()) {
            summary.append("\nDetailed Feedback:\n").append(detailedComment);
        }
        
        return summary.toString();
    }

    // Method to validate category ratings
    public boolean areValidCategoryRatings() {
        return categoryRatings.values().stream()
                .allMatch(rating -> rating >= 1 && rating <= 5);
    }

    // Method to validate detailed comment length
    public boolean isValidCommentLength() {
        return detailedComment == null || detailedComment.length() <= MAX_COMMENT_LENGTH;
    }

    // Method to get average category rating
    public double getAverageCategoryRating() {
        return categoryRatings.values().stream()
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0.0);
    }

    // Method to get category rating stars as HTML
    public String getCategoryRatingStarsHtml(String category) {
        int categoryRating = categoryRatings.getOrDefault(category, 0);
        StringBuilder stars = new StringBuilder();
        stars.append("<div class=\"category-rating-stars\">");
        for (int i = 1; i <= 5; i++) {
            if (i <= categoryRating) {
                stars.append("<i class=\"bi bi-star-fill text-warning\"></i>");
            } else {
                stars.append("<i class=\"bi bi-star text-warning\"></i>");
            }
        }
        stars.append("</div>");
        return stars.toString();
    }

    // Method to get all category ratings as HTML
    public String getAllCategoryRatingsHtml() {
        StringBuilder html = new StringBuilder();
        html.append("<div class=\"category-ratings\">");
        for (String category : RATING_CATEGORIES) {
            html.append("<div class=\"category-rating\">");
            html.append("<span class=\"category-name\">").append(category).append("</span>");
            html.append(getCategoryRatingStarsHtml(category));
            html.append("</div>");
        }
        html.append("</div>");
        return html.toString();
    }
} 