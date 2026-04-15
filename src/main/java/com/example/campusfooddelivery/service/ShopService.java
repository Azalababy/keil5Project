package com.example.campusfooddelivery.service;

import com.example.campusfooddelivery.entity.Shop;
import com.example.campusfooddelivery.repository.ShopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ShopService {
    @Autowired
    private ShopRepository shopRepository;

    public List<Shop> findAll() {
        return shopRepository.findAll();
    }

    public Optional<Shop> findById(Long id) {
        return shopRepository.findById(id);
    }

    public Shop save(Shop shop) {
        return shopRepository.save(shop);
    }

    public void deleteById(Long id) {
        shopRepository.deleteById(id);
    }
}