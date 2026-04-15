package com.example.campusfooddelivery.service;

import com.example.campusfooddelivery.entity.Order;
import com.example.campusfooddelivery.entity.OrderItem;
import com.example.campusfooddelivery.repository.OrderRepository;
import com.example.campusfooddelivery.repository.OrderItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    public Optional<Order> findById(Long id) {
        return orderRepository.findById(id);
    }

    public List<Order> findByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    public List<Order> findByShopId(Long shopId) {
        return orderRepository.findByShopId(shopId);
    }

    @Transactional
    public Order save(Order order, List<OrderItem> orderItems) {
        Order savedOrder = orderRepository.save(order);
        if (orderItems != null) {
            for (OrderItem item : orderItems) {
                item.setOrderId(savedOrder.getId());
                orderItemRepository.save(item);
            }
        }
        return savedOrder;
    }

    public void deleteById(Long id) {
        orderRepository.deleteById(id);
    }

    public List<OrderItem> findOrderItemsByOrderId(Long orderId) {
        return orderItemRepository.findByOrderId(orderId);
    }
}