package com.example.campusfooddelivery.repository;

import com.example.campusfooddelivery.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {
    List<Coupon> findByStatus(Coupon.Status status);
    List<Coupon> findByStatusAndExpireDateAfter(Coupon.Status status, LocalDate date);
}