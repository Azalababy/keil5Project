package com.example.campusfooddelivery.controller;

import com.example.campusfooddelivery.dto.AdminOrderDTO;
import com.example.campusfooddelivery.dto.OrderDetailDTO;
import com.example.campusfooddelivery.entity.Order;
import com.example.campusfooddelivery.entity.OrderItem;
import com.example.campusfooddelivery.entity.Shop;
import com.example.campusfooddelivery.entity.User;
import com.example.campusfooddelivery.repository.ShopRepository;
import com.example.campusfooddelivery.repository.UserRepository;
import com.example.campusfooddelivery.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<AdminOrderDTO>> findAll() {
        try {
            List<Order> orders = orderService.findAll();
            List<AdminOrderDTO> result = orders.stream()
                .map(order -> {
                    String shopName = "未知商家";
                    String shopAddress = "未知地址";
                    String userName = "未知用户";
                    String userPhone = "未知电话";
                    
                    // 获取商家信息
                    if (order.getShopId() != null) {
                        Optional<Shop> shopOpt = shopRepository.findById(order.getShopId());
                        if (shopOpt.isPresent()) {
                            Shop shop = shopOpt.get();
                            shopName = shop.getName() != null ? shop.getName() : "未知商家";
                            shopAddress = shop.getAddress() != null ? shop.getAddress() : "未知地址";
                        }
                    }
                    
                    // 获取用户信息
                    if (order.getUserId() != null) {
                        Optional<User> userOpt = userRepository.findById(order.getUserId());
                        if (userOpt.isPresent()) {
                            User user = userOpt.get();
                            userName = user.getUsername() != null ? user.getUsername() : "未知用户";
                            userPhone = user.getPhone() != null ? user.getPhone() : "未知电话";
                        }
                    }
                    
                    return new AdminOrderDTO(order, shopName, shopAddress, userName, userPhone);
                })
                .collect(Collectors.toList());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> findById(@PathVariable Long id) {
        Optional<Order> order = orderService.findById(id);
        return order.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Order>> findByUserId(@PathVariable Long userId) {
        List<Order> orders = orderService.findByUserId(userId);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/shop/{shopId}")
    public ResponseEntity<List<Order>> findByShopId(@PathVariable Long shopId) {
        List<Order> orders = orderService.findByShopId(shopId);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}/items")
    public ResponseEntity<List<OrderItem>> findOrderItemsByOrderId(@PathVariable Long id) {
        List<OrderItem> orderItems = orderService.findOrderItemsByOrderId(id);
        return ResponseEntity.ok(orderItems);
    }

    @PostMapping
    public ResponseEntity<Order> save(@RequestBody OrderRequest orderRequest) {
        Order order = orderRequest.getOrder();
        List<OrderItem> orderItems = orderRequest.getOrderItems();
        Order savedOrder = orderService.save(order, orderItems);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedOrder);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Order> update(@PathVariable Long id, @RequestBody Order order) {
        Optional<Order> existingOrder = orderService.findById(id);
        if (existingOrder.isPresent()) {
            order.setId(id);
            // 注意：这里只更新订单本身，不更新订单详情
            Order updatedOrder = orderService.save(order, null);
            return ResponseEntity.ok(updatedOrder);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        orderService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Order> updateOrderStatus(@PathVariable Long id, @RequestBody Map<String, String> request) {
        String status = request.get("status");
        Order updatedOrder = orderService.updateStatus(id, status);
        if (updatedOrder != null) {
            return ResponseEntity.ok(updatedOrder);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
    
    // 商家接单
    @PutMapping("/{id}/accept")
    public ResponseEntity<Order> acceptOrder(@PathVariable Long id) {
        Order updatedOrder = orderService.acceptOrder(id);
        if (updatedOrder != null) {
            return ResponseEntity.ok(updatedOrder);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
    
    // 商家开始制作
    @PutMapping("/{id}/prepare")
    public ResponseEntity<Order> startPreparing(@PathVariable Long id) {
        Order updatedOrder = orderService.startPreparing(id);
        if (updatedOrder != null) {
            return ResponseEntity.ok(updatedOrder);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
    
    // 商家完成制作（待骑手取餐）
    @PutMapping("/{id}/ready-for-pickup")
    public ResponseEntity<Order> readyForPickup(@PathVariable Long id) {
        Order updatedOrder = orderService.readyForPickup(id);
        if (updatedOrder != null) {
            return ResponseEntity.ok(updatedOrder);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
    
    // 骑手接单
    @PutMapping("/{id}/rider-accept")
    public ResponseEntity<Order> riderAcceptOrder(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        Long riderId = ((Number) request.get("riderId")).longValue();
        String riderName = (String) request.get("riderName");
        String riderPhone = (String) request.get("riderPhone");
        Order updatedOrder = orderService.riderAcceptOrder(id, riderId, riderName, riderPhone);
        if (updatedOrder != null) {
            return ResponseEntity.ok(updatedOrder);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
    
    // 骑手开始配送
    @PutMapping("/{id}/deliver")
    public ResponseEntity<Order> startDelivering(@PathVariable Long id) {
        Order updatedOrder = orderService.startDelivering(id);
        if (updatedOrder != null) {
            return ResponseEntity.ok(updatedOrder);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
    
    // 骑手送达
    @PutMapping("/{id}/complete")
    public ResponseEntity<Order> deliverOrder(@PathVariable Long id) {
        Order updatedOrder = orderService.deliverOrder(id);
        if (updatedOrder != null) {
            return ResponseEntity.ok(updatedOrder);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
    
    // 获取待骑手接单的订单
    @GetMapping("/ready-for-pickup")
    public ResponseEntity<List<OrderDetailDTO>> findOrdersReadyForPickup() {
        List<OrderDetailDTO> orders = orderService.findOrdersReadyForPickup();
        return ResponseEntity.ok(orders);
    }
    
    // 获取骑手的订单
    @GetMapping("/rider/{riderId}")
    public ResponseEntity<List<com.example.campusfooddelivery.dto.OrderDetailDTO>> findOrdersByRiderId(@PathVariable Long riderId) {
        List<com.example.campusfooddelivery.dto.OrderDetailDTO> orders = orderService.findOrdersByRiderId(riderId);
        return ResponseEntity.ok(orders);
    }

    // 订单请求DTO
    public static class OrderRequest {
        private Order order;
        private List<OrderItem> orderItems;

        public Order getOrder() {
            return order;
        }

        public void setOrder(Order order) {
            this.order = order;
        }

        public List<OrderItem> getOrderItems() {
            return orderItems;
        }

        public void setOrderItems(List<OrderItem> orderItems) {
            this.orderItems = orderItems;
        }
    }
}