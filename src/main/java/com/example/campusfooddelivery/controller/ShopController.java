package com.example.campusfooddelivery.controller;

import com.example.campusfooddelivery.entity.Shop;
import com.example.campusfooddelivery.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/shops")
public class ShopController {
    @Autowired
    private ShopService shopService;

    @GetMapping
    public ResponseEntity<List<Shop>> findAll() {
        List<Shop> shops = shopService.findAll();
        return ResponseEntity.ok(shops);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Shop> findById(@PathVariable Long id) {
        Optional<Shop> shop = shopService.findById(id);
        return shop.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping
    public ResponseEntity<Shop> save(@RequestBody Shop shop) {
        Shop savedShop = shopService.save(shop);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedShop);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Shop> update(@PathVariable Long id, @RequestBody Shop shop) {
        Optional<Shop> existingShop = shopService.findById(id);
        if (existingShop.isPresent()) {
            shop.setId(id);
            Shop updatedShop = shopService.save(shop);
            return ResponseEntity.ok(updatedShop);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        shopService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}