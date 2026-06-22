package com.example.campusfooddelivery.controller;

import com.example.campusfooddelivery.entity.Review;
import com.example.campusfooddelivery.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {
    @Autowired
    private ReviewService reviewService;

    @PostMapping
    public ResponseEntity<Review> createReview(@RequestBody Review review) {
        Review savedReview = reviewService.save(review);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedReview);
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<Review> getReviewByOrderId(@PathVariable Long orderId) {
        return reviewService.findByOrderId(orderId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/shop/{shopId}")
    public ResponseEntity<List<Review>> getReviewsByShopId(@PathVariable Long shopId) {
        List<Review> reviews = reviewService.findByShopId(shopId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/rider/{riderId}")
    public ResponseEntity<List<Review>> getReviewsByRiderId(@PathVariable Long riderId) {
        List<Review> reviews = reviewService.findByRiderId(riderId);
        return ResponseEntity.ok(reviews);
    }
}
