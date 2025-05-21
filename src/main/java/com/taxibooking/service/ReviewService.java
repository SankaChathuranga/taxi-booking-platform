package com.taxibooking.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.taxibooking.model.Review;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReviewService {
    
    @Value("${app.file.storage.path}/${app.file.reviews}")
    private String reviewsFile;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final CollectionType reviewListType;
    
    public ReviewService() {
        this.reviewListType = objectMapper.getTypeFactory()
            .constructCollectionType(List.class, Review.class);
    }
    
    @PostConstruct
    public void init() {
        createFileIfNotExists();
    }
    
    private void createFileIfNotExists() {
        try {
            Path filePath = Paths.get(reviewsFile);
            if (!Files.exists(filePath)) {
                Files.createDirectories(filePath.getParent());
                Files.createFile(filePath);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not create reviews file: " + e.getMessage(), e);
        }
    }
    
    private List<Review> readReviews() throws IOException {
        File file = new File(reviewsFile);
        return objectMapper.readValue(file, reviewListType);
    }
    
    private void writeReviews(List<Review> reviews) throws IOException {
        File file = new File(reviewsFile);
        objectMapper.writeValue(file, reviews);
    }
    
    public void createReview(Review review) throws IOException {
        if (!review.isValidRating()) {
            throw new IllegalArgumentException("Invalid rating value");
        }
        
        List<Review> reviews = readReviews();
        reviews.add(review);
        writeReviews(reviews);
    }
    
    public Optional<Review> getReviewById(String reviewId) throws IOException {
        return readReviews().stream()
            .filter(review -> review.getReviewId().equals(reviewId))
            .findFirst();
    }
    
    public List<Review> getAllReviews() throws IOException {
        return readReviews();
    }
    
    public List<Review> getReviewsByDriver(String driverId) throws IOException {
        return readReviews().stream()
            .filter(review -> review.getDriverId().equals(driverId))
            .collect(Collectors.toList());
    }
    
    public List<Review> getReviewsByCustomer(String customerId) throws IOException {
        return readReviews().stream()
            .filter(review -> review.getCustomerId().equals(customerId))
            .collect(Collectors.toList());
    }
    
    public List<Review> getReviewsByStatus(Review.ReviewStatus status) throws IOException {
        return readReviews().stream()
            .filter(review -> review.getStatus().equals(status.name()))
            .collect(Collectors.toList());
    }
    
    public void updateReview(Review updatedReview) throws IOException {
        List<Review> reviews = readReviews();
        boolean found = false;
        
        for (int i = 0; i < reviews.size(); i++) {
            if (reviews.get(i).getReviewId().equals(updatedReview.getReviewId())) {
                reviews.set(i, updatedReview);
                found = true;
                break;
            }
        }
        
        if (!found) {
            throw new NoSuchElementException("Review not found");
        }
        
        writeReviews(reviews);
    }
    
    public void deleteReview(String reviewId) throws IOException {
        List<Review> reviews = readReviews();
        boolean removed = reviews.removeIf(review -> review.getReviewId().equals(reviewId));
        
        if (!removed) {
            throw new NoSuchElementException("Review not found");
        }
        
        writeReviews(reviews);
    }
    
    public void updateReviewStatus(String reviewId, Review.ReviewStatus newStatus) throws IOException {
        Optional<Review> reviewOpt = getReviewById(reviewId);
        if (reviewOpt.isPresent()) {
            Review review = reviewOpt.get();
            review.updateStatus(newStatus);
            updateReview(review);
        } else {
            throw new NoSuchElementException("Review not found");
        }
    }
    
    public double getAverageDriverRating(String driverId) throws IOException {
        List<Review> reviews = getReviewsByDriver(driverId);
        if (reviews.isEmpty()) {
            return 0.0;
        }
        
        return reviews.stream()
            .mapToInt(Review::getRating)
            .average()
            .orElse(0.0);
    }
    
    public Map<String, Double> getDriverCategoryRatings(String driverId) throws IOException {
        List<Review> reviews = getReviewsByDriver(driverId);
        Map<String, List<Integer>> categoryRatings = new HashMap<>();
        
        reviews.stream()
            .filter(review -> review instanceof com.taxibooking.model.DetailedReview)
            .map(review -> (com.taxibooking.model.DetailedReview) review)
            .forEach(review -> {
                review.getCategoryRatings().forEach((category, rating) -> {
                    categoryRatings.computeIfAbsent(category, k -> new ArrayList<>())
                        .add(rating);
                });
            });
        
        return categoryRatings.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> entry.getValue().stream()
                    .mapToInt(Integer::intValue)
                    .average()
                    .orElse(0.0)
            ));
    }
} 