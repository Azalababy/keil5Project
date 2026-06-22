package com.example.campusfooddelivery.controller;

import com.example.campusfooddelivery.dto.ShopLoginDTO;
import com.example.campusfooddelivery.entity.Order;
import com.example.campusfooddelivery.entity.Shop;
import com.example.campusfooddelivery.repository.OrderRepository;
import com.example.campusfooddelivery.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/shops")
@CrossOrigin(origins = "*")
public class ShopController {

    @Autowired
    private ShopService shopService;

    @Autowired
    private OrderRepository orderRepository;

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> findAll() {
        List<Shop> shops = shopService.findAll();
        List<Map<String, Object>> result = shops.stream()
                .map(shop -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", shop.getId());
                    map.put("name", shop.getName());
                    map.put("phone", shop.getPhone());
                    map.put("address", shop.getAddress());
                    map.put("openingHours", shop.getOpeningHours());
                    map.put("description", shop.getDescription());
                    map.put("status", shop.getStatus());
                    map.put("createdAt", shop.getCreatedAt());
                    map.put("updatedAt", shop.getUpdatedAt());
                    
                    // 添加订单量和营业额统计
                    List<Order> orders = orderRepository.findByShopId(shop.getId());
                    map.put("orderCount", orders.size());
                    BigDecimal revenue = orders.stream()
                            .map(Order::getTotalPrice)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    map.put("revenue", revenue);
                    
                    return map;
                })
                .toList();
        return ResponseEntity.ok(result);
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
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (shopService.findById(id).isPresent()) {
            shopService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody ShopLoginDTO loginDTO) {
        Map<String, Object> response = new HashMap<>();
        Shop shop = shopService.login(loginDTO);
        if (shop != null) {
            response.put("success", true);
            response.put("message", "登录成功");
            response.put("shop", shop);
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "手机号或密码错误");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Shop> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> request) {
        String status = request.get("status");
        Shop.Status shopStatus = Shop.Status.valueOf(status);
        Shop updatedShop = shopService.updateStatus(id, shopStatus);
        if (updatedShop != null) {
            return ResponseEntity.ok(updatedShop);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    // 获取近7天订单趋势（包含订单数和收入）- 按周一到周日顺序
    @GetMapping("/{id}/order-trend")
    public ResponseEntity<List<Map<String, Object>>> getOrderTrend(@PathVariable Long id) {
        List<Map<String, Object>> result = new ArrayList<>();
        String[] dayNames = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};
        
        LocalDate today = LocalDate.now();
        
        // 获取本周一的日期
        LocalDate monday = today.minusDays(today.getDayOfWeek().getValue() - 1);
        
        // 从周一到周日循环
        for (int i = 0; i < 7; i++) {
            LocalDate date = monday.plusDays(i);
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
            
            List<Order> dayOrders = orderRepository.findByShopIdAndCreatedAtBetween(id, startOfDay, endOfDay);
            
            long orderCount = dayOrders.size();
            BigDecimal revenue = dayOrders.stream()
                    .filter(o -> o.getStatus() == Order.Status.delivered)
                    .map(Order::getTotalPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            Map<String, Object> dayData = new HashMap<>();
            dayData.put("label", dayNames[i]);  // 直接按顺序使用dayNames
            dayData.put("value", orderCount);
            dayData.put("revenue", revenue.doubleValue());
            
            result.add(dayData);
        }
        
        return ResponseEntity.ok(result);
    }
}