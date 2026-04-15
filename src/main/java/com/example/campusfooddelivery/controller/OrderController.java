package com.example.campusfooddelivery.controller;

import com.example.campusfooddelivery.entity.Order;
import com.example.campusfooddelivery.entity.OrderItem;
import com.example.campusfooddelivery.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @GetMapping
    public ResponseEntity<List<Order>> findAll() {
        List<Order> orders = orderService.findAll();
        return ResponseEntity.ok(orders);
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