package com.example.campusfooddelivery.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "shop_id", nullable = false)
    private Long shopId;
    
    @Column(name = "rider_id")
    private Long riderId;

    @Column(name = "total_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;
    
    @Column(name = "delivery_fee", precision = 10, scale = 2)
    private BigDecimal deliveryFee;

    @Column(nullable = false, length = 200)
    private String address;

    @Column(nullable = false, length = 20)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status = Status.pending;
    
    @Column(name = "order_status", length = 20)
    private String orderStatus = "pending";

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(name = "remark", length = 500)
    private String remark;

    @Column(name = "shop_accepted")
    private Boolean shopAccepted = false;
    
    @Column(name = "rider_name", length = 50)
    private String riderName;
    
    @Column(name = "rider_phone", length = 20)
    private String riderPhone;
    
    @Column(name = "delivery_time", length = 50)
    private String deliveryTime;

    @Column(name = "rider_accepted_at")
    private LocalDateTime riderAcceptedAt;

    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<OrderItem> orderItems = new ArrayList<>();

    public enum Status {
        pending,           // 待接单（顾客下单后）
        processing,        // 处理中（兼容旧数据）
        accepted,          // 商家已接单
        preparing,         // 商家制作中
        ready_for_pickup,  // 待骑手取餐（商家制作完成）
        picked_up,         // 骑手已取餐
        delivering,        // 配送中
        delivered,         // 已送达
        cancelled          // 已取消
    }
}