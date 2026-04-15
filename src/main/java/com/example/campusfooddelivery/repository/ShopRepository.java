package com.example.campusfooddelivery.repository;

import com.example.campusfooddelivery.entity.Shop;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopRepository extends JpaRepository<Shop, Long> {
}