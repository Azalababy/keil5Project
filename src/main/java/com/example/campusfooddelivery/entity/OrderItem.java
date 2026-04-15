package com.example.campusfooddelivery.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "order_items")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "dish_id", nullable = false)
    private Long dishId;

    @Column(nullable = false)
    private Integer quantity;

    // 修改为
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
}