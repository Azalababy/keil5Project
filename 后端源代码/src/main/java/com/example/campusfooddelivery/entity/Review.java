package com.example.campusfooddelivery.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "reviews")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "shop_id", nullable = false)
    private Long shopId;

    @Column(name = "rider_id")
    private Long riderId;

    @Column(name = "shop_rating")
    private Integer shopRating;

    @Column(name = "rider_rating")
    private Integer riderRating;

    @Column(name = "shop_comment", length = 500)
    private String shopComment;

    @Column(name = "rider_comment", length = 500)
    private String riderComment;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
