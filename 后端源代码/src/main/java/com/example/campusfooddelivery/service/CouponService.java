package com.example.campusfooddelivery.service;

import com.example.campusfooddelivery.entity.Coupon;
import com.example.campusfooddelivery.entity.UserCoupon;
import com.example.campusfooddelivery.repository.CouponRepository;
import com.example.campusfooddelivery.repository.UserCouponRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CouponService {

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private UserCouponRepository userCouponRepository;

    public List<Coupon> findAllActive() {
        return couponRepository.findByStatusAndExpireDateAfter(Coupon.Status.active, LocalDate.now());
    }

    public Optional<Coupon> findById(Long id) {
        return couponRepository.findById(id);
    }

    public Coupon save(Coupon coupon) {
        coupon.setUpdatedAt(LocalDateTime.now());
        if (coupon.getId() == null) {
            coupon.setCreatedAt(LocalDateTime.now());
            coupon.setRemainingQuantity(coupon.getTotalQuantity());
        }
        return couponRepository.save(coupon);
    }

    public void deleteById(Long id) {
        couponRepository.deleteById(id);
    }

    @Transactional
    public Map<String, Object> grabCoupon(Long userId, Long couponId) {
        Map<String, Object> response = new HashMap<>();
        
        Optional<Coupon> couponOpt = couponRepository.findById(couponId);
        if (couponOpt.isEmpty()) {
            response.put("success", false);
            response.put("message", "优惠券不存在");
            return response;
        }
        
        Coupon coupon = couponOpt.get();
        
        if (coupon.getStatus() != Coupon.Status.active) {
            response.put("success", false);
            response.put("message", "优惠券已下架");
            return response;
        }
        
        if (coupon.getExpireDate().isBefore(LocalDate.now())) {
            response.put("success", false);
            response.put("message", "优惠券已过期");
            return response;
        }
        
        if (coupon.getRemainingQuantity() <= 0) {
            response.put("success", false);
            response.put("message", "优惠券已抢光");
            return response;
        }
        
        long count = userCouponRepository.countByUserIdAndCouponId(userId, couponId);
        if (count >= 1) {
            response.put("success", false);
            response.put("message", "您已经领取过该优惠券");
            return response;
        }
        
        coupon.setRemainingQuantity(coupon.getRemainingQuantity() - 1);
        couponRepository.save(coupon);
        
        UserCoupon userCoupon = new UserCoupon();
        userCoupon.setUserId(userId);
        userCoupon.setCouponId(couponId);
        userCoupon.setStatus(UserCoupon.Status.unused);
        userCoupon.setCreatedAt(LocalDateTime.now());
        userCouponRepository.save(userCoupon);
        
        response.put("success", true);
        response.put("message", "领取成功");
        response.put("coupon", coupon);
        return response;
    }

    public List<UserCoupon> getUserCoupons(Long userId) {
        return userCouponRepository.findByUserIdAndStatus(userId, UserCoupon.Status.unused);
    }

    @Transactional
    public boolean useCoupon(Long userCouponId, Long orderId) {
        Optional<UserCoupon> userCouponOpt = userCouponRepository.findById(userCouponId);
        if (userCouponOpt.isEmpty()) {
            return false;
        }
        
        UserCoupon userCoupon = userCouponOpt.get();
        if (userCoupon.getStatus() != UserCoupon.Status.unused) {
            return false;
        }
        
        Optional<Coupon> couponOpt = couponRepository.findById(userCoupon.getCouponId());
        if (couponOpt.isEmpty()) {
            return false;
        }
        
        Coupon coupon = couponOpt.get();
        if (coupon.getExpireDate().isBefore(LocalDate.now())) {
            userCoupon.setStatus(UserCoupon.Status.expired);
            userCouponRepository.save(userCoupon);
            return false;
        }
        
        userCoupon.setStatus(UserCoupon.Status.used);
        userCoupon.setOrderId(orderId);
        userCoupon.setUsedAt(LocalDateTime.now());
        userCouponRepository.save(userCoupon);
        return true;
    }

    public BigDecimal getCouponValue(Long couponId) {
        Optional<Coupon> couponOpt = couponRepository.findById(couponId);
        return couponOpt.map(Coupon::getValue).orElse(BigDecimal.ZERO);
    }

    public BigDecimal getCouponMinAmount(Long couponId) {
        Optional<Coupon> couponOpt = couponRepository.findById(couponId);
        return couponOpt.map(Coupon::getMinAmount).orElse(BigDecimal.ZERO);
    }
}