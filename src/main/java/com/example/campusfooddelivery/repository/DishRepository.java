package com.example.campusfooddelivery.repository;

import com.example.campusfooddelivery.entity.Dish;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DishRepository extends JpaRepository<Dish, Long> {
    List<Dish> findByShopId(Long shopId);
}