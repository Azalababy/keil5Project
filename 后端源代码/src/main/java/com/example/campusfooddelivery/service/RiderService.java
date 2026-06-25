package com.example.campusfooddelivery.service;

import com.example.campusfooddelivery.dto.ShopLoginDTO;
import com.example.campusfooddelivery.entity.Order;
import com.example.campusfooddelivery.entity.Rider;
import com.example.campusfooddelivery.repository.OrderRepository;
import com.example.campusfooddelivery.repository.ReviewRepository;
import com.example.campusfooddelivery.repository.RiderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class RiderService {

    @Autowired
    private RiderRepository riderRepository;
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private ReviewRepository reviewRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<Rider> findAll() {
        return riderRepository.findAll();
    }

    public Optional<Rider> findById(Long id) {
        return riderRepository.findById(id);
    }

    public Optional<Rider> findByPhone(String phone) {
        return riderRepository.findByPhone(phone);
    }

    public Rider save(Rider rider) {
        rider.setUpdatedAt(LocalDateTime.now());
        if (rider.getId() == null) {
            rider.setCreatedAt(LocalDateTime.now());
            // 对新骑手的密码进行加密
            rider.setPassword(passwordEncoder.encode(rider.getPassword()));
        }
        return riderRepository.save(rider);
    }

    public void deleteById(Long id) {
        riderRepository.deleteById(id);
    }

    public boolean existsByPhone(String phone) {
        return riderRepository.existsByPhone(phone);
    }

    public boolean existsByIdCard(String idCard) {
        return riderRepository.existsByIdCard(idCard);
    }

    public Rider login(ShopLoginDTO loginDTO) {
        Optional<Rider> rider = riderRepository.findByPhone(loginDTO.getPhone());
        if (rider.isPresent() && passwordEncoder.matches(loginDTO.getPassword(), rider.get().getPassword())) {
            return rider.get();
        }
        return null;
    }

    public Rider updateStatus(Long id, Rider.Status status) {
        Optional<Rider> rider = riderRepository.findById(id);
        if (rider.isPresent()) {
            Rider existingRider = rider.get();
            existingRider.setStatus(status);
            existingRider.setUpdatedAt(LocalDateTime.now());
            return riderRepository.save(existingRider);
        }
        return null;
    }
    
    public Map<String, Object> getRiderStats(Long riderId) {
        Map<String, Object> stats = new HashMap<>();
        
        // 获取今日开始和结束时间
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime todayEnd = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        
        // 获取骑手所有订单
        List<Order> allOrders = orderRepository.findByRiderId(riderId);
        
        // 今日订单数（按送达时间统计，若deliveredAt为空则使用updatedAt）
        long todayOrderCount = allOrders.stream()
                .filter(o -> o.getStatus() != null && o.getStatus() == Order.Status.delivered)
                .filter(o -> {
                    LocalDateTime deliveryTime = o.getDeliveredAt() != null ? o.getDeliveredAt() : o.getUpdatedAt();
                    return deliveryTime != null 
                            && !deliveryTime.isBefore(todayStart) 
                            && !deliveryTime.isAfter(todayEnd);
                })
                .count();
        stats.put("todayOrders", todayOrderCount);
        
        // 今日收入（订单总金额的20%，按送达时间统计）
        BigDecimal todayEarnings = allOrders.stream()
                .filter(o -> o.getStatus() != null && o.getStatus() == Order.Status.delivered)
                .filter(o -> {
                    LocalDateTime deliveryTime = o.getDeliveredAt() != null ? o.getDeliveredAt() : o.getUpdatedAt();
                    return deliveryTime != null 
                            && !deliveryTime.isBefore(todayStart) 
                            && !deliveryTime.isAfter(todayEnd);
                })
                .map(o -> o.getTotalPrice() != null ? o.getTotalPrice().multiply(new BigDecimal("0.2")) : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.put("todayEarnings", todayEarnings);
        
        // 总订单数
        long totalOrderCount = allOrders.stream()
                .filter(o -> o.getStatus() != null && o.getStatus() == Order.Status.delivered)
                .count();
        stats.put("totalOrders", totalOrderCount);
        
        // 总收入（订单总金额的20%）
        BigDecimal totalEarnings = allOrders.stream()
                .filter(o -> o.getStatus() != null && o.getStatus() == Order.Status.delivered)
                .map(o -> o.getTotalPrice() != null ? o.getTotalPrice().multiply(new BigDecimal("0.2")) : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.put("totalEarnings", totalEarnings);
        
        // 平均配送时长（根据订单配送时间计算）
        double avgDeliveryTime = allOrders.stream()
                .filter(o -> o.getStatus() != null && o.getStatus() == Order.Status.delivered)
                .mapToDouble(o -> {
                    // 优先使用deliveryTime字段
                    if (o.getDeliveryTime() != null && !o.getDeliveryTime().isEmpty() && !o.getDeliveryTime().equals("0")) {
                        try {
                            return Double.parseDouble(o.getDeliveryTime());
                        } catch (NumberFormatException e) {
                            // fall through to calculate
                        }
                    }
                    // 如果deliveryTime无效，根据创建时间和送达时间计算
                    if (o.getCreatedAt() != null && o.getDeliveredAt() != null) {
                        return java.time.Duration.between(o.getCreatedAt(), o.getDeliveredAt()).toMinutes();
                    }
                    return 0;
                })
                .filter(time -> time > 0) // 过滤无效的配送时间
                .average()
                .orElse(0);
        stats.put("avgDeliveryTime", Math.round(avgDeliveryTime * 100) / 100.0);
        
        // 配送完成率（根据骑手评分计算：评分总和/最高可能评分 × 100%）
        List<com.example.campusfooddelivery.entity.Review> riderReviews = reviewRepository.findByRiderId(riderId);
        int totalRating = riderReviews.stream()
                .filter(r -> r.getRiderRating() != null)
                .mapToInt(com.example.campusfooddelivery.entity.Review::getRiderRating)
                .sum();
        int reviewedOrderCount = riderReviews.size();
        // 最高可能评分为：有评价的订单数 × 5星
        double deliveryRate = reviewedOrderCount > 0 ? (totalRating * 100.0 / (reviewedOrderCount * 5)) : 100;
        stats.put("deliveryRate", Math.round(deliveryRate * 100) / 100.0);
        
        return stats;
    }
}