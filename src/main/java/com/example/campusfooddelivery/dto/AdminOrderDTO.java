package com.example.campusfooddelivery.dto;

import com.example.campusfooddelivery.entity.Order;
import com.example.campusfooddelivery.entity.OrderItem;
import lombok.Data;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Data
public class AdminOrderDTO {
    private Long id;
    private Long shopId;
    private String shopName;
    private String shopAddress;
    private Long userId;
    private String userName;
    private String userPhone;
    private String userAddress;
    private Double totalPrice;
    private String status;
    private String createdAt;
    private List<OrderItemDTO> items;

    @Data
    public static class OrderItemDTO {
        private Long id;
        private String dishName;
        private Integer quantity;
        private Double price;

        public OrderItemDTO(OrderItem item) {
            this.id = item.getId();
            this.dishName = item.getDishName();
            this.quantity = item.getQuantity();
            this.price = item.getPrice() != null ? item.getPrice().doubleValue() : 0.0;
        }
    }

    public AdminOrderDTO(Order order, String shopName, String shopAddress, String userName, String userPhone) {
        this.id = order.getId();
        this.shopId = order.getShopId();
        this.shopName = shopName;
        this.shopAddress = shopAddress;
        this.userId = order.getUserId();
        this.userName = userName;
        this.userPhone = userPhone;
        this.userAddress = order.getAddress();
        this.totalPrice = order.getTotalPrice() != null ? order.getTotalPrice().doubleValue() : 0.0;
        this.status = order.getStatus().name();
        this.createdAt = order.getCreatedAt() != null ? 
            order.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : "";
        this.items = order.getOrderItems().stream()
            .map(OrderItemDTO::new)
            .toList();
    }
}
