package com.example.campusfooddelivery.repository;

import com.example.campusfooddelivery.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Long userId);
    List<Order> findByShopId(Long shopId);
}