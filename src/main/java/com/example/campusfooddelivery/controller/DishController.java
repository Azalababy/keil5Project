package com.example.campusfooddelivery.controller;

import com.example.campusfooddelivery.entity.Dish;
import com.example.campusfooddelivery.service.DishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/dishes")
public class DishController {
    @Autowired
    private DishService dishService;

    @GetMapping
    public ResponseEntity<List<Dish>> findAll() {
        List<Dish> dishes = dishService.findAll();
        return ResponseEntity.ok(dishes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Dish> findById(@PathVariable Long id) {
        Optional<Dish> dish = dishService.findById(id);
        return dish.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/shop/{shopId}")
    public ResponseEntity<List<Dish>> findByShopId(@PathVariable Long shopId) {
        List<Dish> dishes = dishService.findByShopId(shopId);
        return ResponseEntity.ok(dishes);
    }

    @PostMapping
    public ResponseEntity<Dish> save(@RequestBody Dish dish) {
        Dish savedDish = dishService.save(dish);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedDish);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Dish> update(@PathVariable Long id, @RequestBody Dish dish) {
        Optional<Dish> existingDish = dishService.findById(id);
        if (existingDish.isPresent()) {
            dish.setId(id);
            Dish updatedDish = dishService.save(dish);
            return ResponseEntity.ok(updatedDish);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        dishService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}