package com.example.campusfooddelivery.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "riders")
public class Rider {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, unique = true, length = 20)
    private String phone;

    @Column(nullable = false, length = 100)
    private String password;

    @Column(name = "id_card", nullable = false, unique = true, length = 18)
    private String idCard;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "ENUM('online', 'offline') DEFAULT 'offline'")
    private Status status = Status.offline;

    @Column(name = "total_orders")
    private Integer totalOrders = 0;

    @Column(name = "total_earnings")
    private Double totalEarnings = 0.0;

    @Column(name = "avg_delivery_time")
    private Integer avgDeliveryTime = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public enum Status {
        online, offline
    }
}