package com.example.campusfooddelivery.repository;

import com.example.campusfooddelivery.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Long userId);
    List<Order> findByShopId(Long shopId);
    List<Order> findByShopIdAndCreatedAtBetween(Long shopId, LocalDateTime start, LocalDateTime end);
    List<Order> findByRiderId(Long riderId);
    List<Order> findByStatus(Order.Status status);
    
    @Query("SELECT SUM(o.totalPrice) FROM Order o")
    BigDecimal sumTotalPrice();
    
    List<Order> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    
    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    
    List<Order> findByRiderIdAndCreatedAtBetween(Long riderId, LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT SUM(o.deliveryFee) FROM Order o WHERE o.riderId = :riderId")
    BigDecimal sumDeliveryFeeByRiderId(@Param("riderId") Long riderId);
    
    @Query("SELECT SUM(o.deliveryFee) FROM Order o WHERE o.riderId = :riderId AND o.createdAt BETWEEN :start AND :end")
    BigDecimal sumDeliveryFeeByRiderIdAndDateRange(@Param("riderId") Long riderId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}