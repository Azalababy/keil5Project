package com.example.campusfooddelivery.service;

import com.example.campusfooddelivery.entity.Review;
import com.example.campusfooddelivery.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {
    @Autowired
    private ReviewRepository reviewRepository;

    public Review save(Review review) {
        return reviewRepository.save(review);
    }

    public Optional<Review> findByOrderId(Long orderId) {
        return reviewRepository.findByOrderId(orderId);
    }

    public List<Review> findByShopId(Long shopId) {
        return reviewRepository.findByShopId(shopId);
    }

    public List<Review> findByRiderId(Long riderId) {
        return reviewRepository.findByRiderId(riderId);
    }
}
