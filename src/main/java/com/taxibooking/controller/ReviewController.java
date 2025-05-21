package com.taxibooking.controller;

import com.taxibooking.model.Review;
import com.taxibooking.model.QuickReview;
import com.taxibooking.model.DetailedReview;
import com.taxibooking.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/reviews")
public class ReviewController {
    
    private final ReviewService reviewService;
    
    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }
    
    // Review listing and search
    @GetMapping
    public String listReviews(
            @RequestParam(required = false) String driverId,
            @RequestParam(required = false) String customerId,
            @RequestParam(required = false) String status,
            Model model) {
        try {
            List<Review> reviews;
            if (driverId != null) {
                reviews = reviewService.getReviewsByDriver(driverId);
            } else if (customerId != null) {
                reviews = reviewService.getReviewsByCustomer(customerId);
            } else if (status != null) {
                reviews = reviewService.getReviewsByStatus(Review.ReviewStatus.valueOf(status));
            } else {
                reviews = reviewService.getAllReviews();
            }
            
            model.addAttribute("reviews", reviews);
            model.addAttribute("statuses", Review.ReviewStatus.values());
            return "review/list";
        } catch (IOException e) {
            return "error";
        }
    }
    
    // Review submission
    @GetMapping("/submit")
    public String showSubmissionForm(
            @RequestParam String bookingId,
            @RequestParam String driverId,
            Model model) {
        model.addAttribute("bookingId", bookingId);
        model.addAttribute("driverId", driverId);
        return "review/submit";
    }
    
    @PostMapping("/submit/quick")
    public String submitQuickReview(
            @RequestParam String bookingId,
            @RequestParam String customerId,
            @RequestParam String driverId,
            @RequestParam int rating,
            @RequestParam(required = false) String shortComment) {
        try {
            QuickReview review = new QuickReview(bookingId, customerId, driverId, rating, shortComment);
            reviewService.createReview(review);
            return "redirect:/bookings/" + bookingId;
        } catch (IOException e) {
            return "error";
        }
    }
    
    @PostMapping("/submit/detailed")
    public String submitDetailedReview(
            @RequestParam String bookingId,
            @RequestParam String customerId,
            @RequestParam String driverId,
            @RequestParam int rating,
            @RequestParam(required = false) String detailedComment,
            @RequestParam Map<String, String> categoryRatings) {
        try {
            Map<String, Integer> ratings = new HashMap<>();
            categoryRatings.forEach((category, value) -> {
                if (!category.equals("bookingId") && !category.equals("customerId") && 
                    !category.equals("driverId") && !category.equals("rating") && 
                    !category.equals("detailedComment")) {
                    ratings.put(category, Integer.parseInt(value));
                }
            });
            
            DetailedReview review = new DetailedReview(bookingId, customerId, driverId, 
                                                     rating, detailedComment, ratings);
            reviewService.createReview(review);
            return "redirect:/bookings/" + bookingId;
        } catch (IOException e) {
            return "error";
        }
    }
    
    // Review details and update
    @GetMapping("/{reviewId}")
    public String viewReview(@PathVariable String reviewId, Model model) {
        try {
            Optional<Review> reviewOpt = reviewService.getReviewById(reviewId);
            if (reviewOpt.isPresent()) {
                model.addAttribute("review", reviewOpt.get());
                return "review/details";
            }
            return "error";
        } catch (IOException e) {
            return "error";
        }
    }
    
    @GetMapping("/{reviewId}/edit")
    public String showEditForm(@PathVariable String reviewId, Model model) {
        try {
            Optional<Review> reviewOpt = reviewService.getReviewById(reviewId);
            if (reviewOpt.isPresent()) {
                model.addAttribute("review", reviewOpt.get());
                return "review/edit";
            }
            return "error";
        } catch (IOException e) {
            return "error";
        }
    }
    
    @PostMapping("/{reviewId}/update")
    public String updateReview(
            @PathVariable String reviewId,
            @ModelAttribute Review review) {
        try {
            reviewService.updateReview(review);
            return "redirect:/reviews/" + reviewId;
        } catch (IOException e) {
            return "error";
        }
    }
    
    // Review status updates (admin)
    @PostMapping("/{reviewId}/status")
    @ResponseBody
    public ResponseEntity<String> updateReviewStatus(
            @PathVariable String reviewId,
            @RequestParam Review.ReviewStatus status) {
        try {
            reviewService.updateReviewStatus(reviewId, status);
            return ResponseEntity.ok("Review status updated successfully");
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Failed to update review status");
        }
    }
    
    // Review deletion
    @PostMapping("/{reviewId}/delete")
    @ResponseBody
    public ResponseEntity<String> deleteReview(@PathVariable String reviewId) {
        try {
            reviewService.deleteReview(reviewId);
            return ResponseEntity.ok("Review deleted successfully");
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Failed to delete review");
        }
    }
    
    // API endpoints
    @GetMapping("/api/driver/{driverId}")
    @ResponseBody
    public ResponseEntity<List<Review>> getDriverReviews(@PathVariable String driverId) {
        try {
            return ResponseEntity.ok(reviewService.getReviewsByDriver(driverId));
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/api/driver/{driverId}/rating")
    @ResponseBody
    public ResponseEntity<Double> getDriverAverageRating(@PathVariable String driverId) {
        try {
            return ResponseEntity.ok(reviewService.getAverageDriverRating(driverId));
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/api/driver/{driverId}/categories")
    @ResponseBody
    public ResponseEntity<Map<String, Double>> getDriverCategoryRatings(@PathVariable String driverId) {
        try {
            return ResponseEntity.ok(reviewService.getDriverCategoryRatings(driverId));
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
} 