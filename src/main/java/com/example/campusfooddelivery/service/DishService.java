package com.example.campusfooddelivery.service;

import com.example.campusfooddelivery.entity.Dish;
import com.example.campusfooddelivery.repository.DishRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DishService {
    @Autowired
    private DishRepository dishRepository;

    public List<Dish> findAll() {
        return dishRepository.findAll();
    }

    public Optional<Dish> findById(Long id) {
        return dishRepository.findById(id);
    }

    public List<Dish> findByShopId(Long shopId) {
        return dishRepository.findByShopId(shopId);
    }

    public Dish save(Dish dish) {
        return dishRepository.save(dish);
    }

    public void deleteById(Long id) {
        dishRepository.deleteById(id);
    }
}