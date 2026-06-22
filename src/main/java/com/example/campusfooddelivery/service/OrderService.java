package com.example.campusfooddelivery.service;

import com.example.campusfooddelivery.dto.OrderDetailDTO;
import com.example.campusfooddelivery.entity.Dish;
import com.example.campusfooddelivery.entity.Order;
import com.example.campusfooddelivery.entity.OrderItem;
import com.example.campusfooddelivery.entity.Shop;
import com.example.campusfooddelivery.repository.DishRepository;
import com.example.campusfooddelivery.repository.OrderRepository;
import com.example.campusfooddelivery.repository.OrderItemRepository;
import com.example.campusfooddelivery.repository.ShopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private DishRepository dishRepository;

    @Autowired
    private ShopRepository shopRepository;

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
        if (orderItems != null && !orderItems.isEmpty()) {
            for (OrderItem item : orderItems) {
                item.setOrderId(savedOrder.getId());
                
                // 自动获取菜品信息
                Optional<Dish> dishOpt = dishRepository.findById(item.getDishId());
                if (dishOpt.isPresent()) {
                    Dish dish = dishOpt.get();
                    item.setDishName(dish.getName());
                    item.setImageUrl(dish.getImageUrl());
                } else {
                    item.setDishName("未知菜品");
                }
                
                orderItemRepository.save(item);
            }
            // 重新加载订单以获取关联的订单项
            return orderRepository.findById(savedOrder.getId()).orElse(savedOrder);
        }
        return savedOrder;
    }

    public void deleteById(Long id) {
        orderRepository.deleteById(id);
    }

    public List<OrderItem> findOrderItemsByOrderId(Long orderId) {
        return orderItemRepository.findByOrderId(orderId);
    }

    public Order updateStatus(Long id, String status) {
        Optional<Order> existingOrder = orderRepository.findById(id);
        if (existingOrder.isPresent()) {
            Order order = existingOrder.get();
            order.setStatus(Order.Status.valueOf(status));
            order.setUpdatedAt(LocalDateTime.now());
            return orderRepository.save(order);
        }
        return null;
    }
    
    // 商家接单
    public Order acceptOrder(Long orderId) {
        Optional<Order> existingOrder = orderRepository.findById(orderId);
        if (existingOrder.isPresent()) {
            Order order = existingOrder.get();
            if (order.getStatus() == Order.Status.pending) {
                order.setStatus(Order.Status.accepted);
                order.setShopAccepted(true);
                order.setUpdatedAt(LocalDateTime.now());
                return orderRepository.save(order);
            }
        }
        return null;
    }
    
    // 商家开始制作
    public Order startPreparing(Long orderId) {
        Optional<Order> existingOrder = orderRepository.findById(orderId);
        if (existingOrder.isPresent()) {
            Order order = existingOrder.get();
            if (order.getStatus() == Order.Status.accepted) {
                order.setStatus(Order.Status.preparing);
                order.setUpdatedAt(LocalDateTime.now());
                return orderRepository.save(order);
            }
        }
        return null;
    }
    
    // 商家完成制作（待骑手取餐）
    public Order readyForPickup(Long orderId) {
        Optional<Order> existingOrder = orderRepository.findById(orderId);
        if (existingOrder.isPresent()) {
            Order order = existingOrder.get();
            if (order.getStatus() == Order.Status.preparing) {
                order.setStatus(Order.Status.ready_for_pickup);
                order.setUpdatedAt(LocalDateTime.now());
                return orderRepository.save(order);
            }
        }
        return null;
    }
    
    // 骑手接单
    public Order riderAcceptOrder(Long orderId, Long riderId, String riderName, String riderPhone) {
        Optional<Order> existingOrder = orderRepository.findById(orderId);
        if (existingOrder.isPresent()) {
            Order order = existingOrder.get();
            if (order.getStatus() == Order.Status.ready_for_pickup) {
                order.setStatus(Order.Status.picked_up);
                order.setRiderId(riderId);
                order.setRiderName(riderName);
                order.setRiderPhone(riderPhone);
                order.setRiderAcceptedAt(LocalDateTime.now());
                order.setUpdatedAt(LocalDateTime.now());
                return orderRepository.save(order);
            }
        }
        return null;
    }
    
    // 骑手开始配送
    public Order startDelivering(Long orderId) {
        Optional<Order> existingOrder = orderRepository.findById(orderId);
        if (existingOrder.isPresent()) {
            Order order = existingOrder.get();
            if (order.getStatus() == Order.Status.picked_up) {
                order.setStatus(Order.Status.delivering);
                order.setUpdatedAt(LocalDateTime.now());
                return orderRepository.save(order);
            }
        }
        return null;
    }
    
    // 骑手送达
    public Order deliverOrder(Long orderId) {
        Optional<Order> existingOrder = orderRepository.findById(orderId);
        if (existingOrder.isPresent()) {
            Order order = existingOrder.get();
            if (order.getStatus() == Order.Status.delivering) {
                order.setStatus(Order.Status.delivered);
                order.setDeliveredAt(LocalDateTime.now());
                order.setUpdatedAt(LocalDateTime.now());
                
                // 计算配送时间（分钟）
                long minutes = 0;
                if (order.getRiderAcceptedAt() != null) {
                    minutes = java.time.Duration.between(order.getRiderAcceptedAt(), LocalDateTime.now()).toMinutes();
                } else if (order.getCreatedAt() != null) {
                    minutes = java.time.Duration.between(order.getCreatedAt(), LocalDateTime.now()).toMinutes();
                }
                // 确保配送时间至少为1分钟
                if (minutes < 1) {
                    minutes = 1;
                }
                order.setDeliveryTime(String.valueOf(minutes));
            }
                
            return orderRepository.save(order);
        }
        return null;
    }
    
    // 查找待骑手接单的订单（商家制作完成，等待骑手取餐）
    public List<OrderDetailDTO> findOrdersReadyForPickup() {
        List<Order> orders = orderRepository.findByStatus(Order.Status.ready_for_pickup);
        List<OrderDetailDTO> result = new ArrayList<>();
        for (Order order : orders) {
            Optional<Shop> shopOpt = shopRepository.findById(order.getShopId());
            String shopName = shopOpt.map(Shop::getName).orElse("未知商家");
            String shopAddress = shopOpt.map(Shop::getAddress).orElse("未知地址");
            result.add(new OrderDetailDTO(order, shopName, shopAddress));
        }
        return result;
    }
    
    // 查找骑手的订单（包含商家信息）
    public List<OrderDetailDTO> findOrdersByRiderId(Long riderId) {
        List<Order> orders = orderRepository.findByRiderId(riderId);
        List<OrderDetailDTO> result = new ArrayList<>();
        for (Order order : orders) {
            // 如果配送时间为空或为0，尝试计算
            if ((order.getDeliveryTime() == null || order.getDeliveryTime().isEmpty() || order.getDeliveryTime().equals("0")) 
                && order.getStatus() == Order.Status.delivered 
                && order.getCreatedAt() != null && order.getDeliveredAt() != null) {
                long minutes = java.time.Duration.between(order.getCreatedAt(), order.getDeliveredAt()).toMinutes();
                if (minutes < 1) {
                    minutes = 1;
                }
                order.setDeliveryTime(String.valueOf(minutes));
                orderRepository.save(order);
            }
            Optional<Shop> shopOpt = shopRepository.findById(order.getShopId());
            String shopName = shopOpt.map(Shop::getName).orElse("未知商家");
            String shopAddress = shopOpt.map(Shop::getAddress).orElse("未知地址");
            result.add(new OrderDetailDTO(order, shopName, shopAddress));
        }
        return result;
    }
}