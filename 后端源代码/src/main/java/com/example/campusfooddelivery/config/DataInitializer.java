package com.example.campusfooddelivery.config;

import com.example.campusfooddelivery.entity.Coupon;
import com.example.campusfooddelivery.repository.CouponRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private CouponRepository couponRepository;

    @Override
    public void run(String... args) throws Exception {
        if (couponRepository.count() == 0) {
            createCoupon(new BigDecimal("2"), new BigDecimal("10"), LocalDate.now().plusDays(7), "满10减2", 100);
            createCoupon(new BigDecimal("5"), new BigDecimal("20"), LocalDate.now().plusDays(7), "满20减5", 50);
            createCoupon(new BigDecimal("10"), new BigDecimal("50"), LocalDate.now().plusDays(7), "满50减10", 30);
            createCoupon(new BigDecimal("20"), new BigDecimal("100"), LocalDate.now().plusDays(7), "满100减20", 20);
            createCoupon(new BigDecimal("2"), null, LocalDate.now().plusDays(7), "无门槛2元券", 80);
            createCoupon(new BigDecimal("5"), null, LocalDate.now().plusDays(7), "无门槛5元券", 40);
        }
    }

    private void createCoupon(BigDecimal value, BigDecimal minAmount, LocalDate expireDate, String description, int totalQuantity) {
        Coupon coupon = new Coupon();
        coupon.setValue(value);
        coupon.setMinAmount(minAmount);
        coupon.setExpireDate(expireDate);
        coupon.setDescription(description);
        coupon.setTotalQuantity(totalQuantity);
        coupon.setRemainingQuantity(totalQuantity);
        coupon.setStatus(Coupon.Status.active);
        couponRepository.save(coupon);
    }
}