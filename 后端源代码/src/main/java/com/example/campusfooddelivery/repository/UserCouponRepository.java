package com.example.campusfooddelivery.repository;

import com.example.campusfooddelivery.entity.UserCoupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserCouponRepository extends JpaRepository<UserCoupon, Long> {
    List<UserCoupon> findByUserId(Long userId);
    List<UserCoupon> findByUserIdAndStatus(Long userId, UserCoupon.Status status);
    long countByUserIdAndCouponId(Long userId, Long couponId);
}