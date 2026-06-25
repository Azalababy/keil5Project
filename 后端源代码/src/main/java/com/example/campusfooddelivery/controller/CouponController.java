package com.example.campusfooddelivery.controller;

import com.example.campusfooddelivery.entity.Coupon;
import com.example.campusfooddelivery.entity.UserCoupon;
import com.example.campusfooddelivery.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/coupons")
@CrossOrigin(origins = "*")
public class CouponController {

    @Autowired
    private CouponService couponService;

    @GetMapping
    public ResponseEntity<List<Coupon>> getActiveCoupons() {
        List<Coupon> coupons = couponService.findAllActive();
        return ResponseEntity.ok(coupons);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Coupon> getCouponById(@PathVariable Long id) {
        return couponService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Coupon> createCoupon(@RequestBody Coupon coupon) {
        Coupon savedCoupon = couponService.save(coupon);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCoupon);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Coupon> updateCoupon(@PathVariable Long id, @RequestBody Coupon coupon) {
        if (!couponService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        coupon.setId(id);
        Coupon updatedCoupon = couponService.save(coupon);
        return ResponseEntity.ok(updatedCoupon);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCoupon(@PathVariable Long id) {
        if (!couponService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        couponService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{couponId}/grab")
    public ResponseEntity<Map<String, Object>> grabCoupon(@PathVariable Long couponId, @RequestBody Map<String, Long> request) {
        Long userId = request.get("userId");
        Map<String, Object> response = couponService.grabCoupon(userId, couponId);
        if ((Boolean) response.get("success")) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserCoupon>> getUserCoupons(@PathVariable Long userId) {
        List<UserCoupon> userCoupons = couponService.getUserCoupons(userId);
        return ResponseEntity.ok(userCoupons);
    }

    @PostMapping("/user-coupon/{userCouponId}/use")
    public ResponseEntity<Map<String, Object>> useCoupon(@PathVariable Long userCouponId, @RequestBody Map<String, Long> request) {
        Long orderId = request.get("orderId");
        boolean success = couponService.useCoupon(userCouponId, orderId);
        Map<String, Object> response = new HashMap<>();
        if (success) {
            response.put("success", true);
            response.put("message", "使用成功");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "使用失败");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}