package com.example.campusfooddelivery.repository;

import com.example.campusfooddelivery.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    Optional<Review> findByOrderId(Long orderId);
    List<Review> findByShopId(Long shopId);
    List<Review> findByRiderId(Long riderId);
    long countByRiderId(Long riderId);
}
