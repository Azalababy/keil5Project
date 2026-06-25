package com.example.campusfooddelivery.dto;

import com.example.campusfooddelivery.entity.Order;
import com.example.campusfooddelivery.entity.OrderItem;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderDetailDTO {
    private Long id;
    private BigDecimal totalPrice;
    private BigDecimal deliveryFee;
    private String address;
    private String phone;
    private String status;
    private Long shopId;
    private String shopName;
    private String shopAddress;
    private String deliveryTime;
    private List<OrderItem> orderItems;

    public OrderDetailDTO(Order order, String shopName, String shopAddress) {
        this.id = order.getId();
        this.totalPrice = order.getTotalPrice();
        this.deliveryFee = order.getDeliveryFee();
        this.address = order.getAddress();
        this.phone = order.getPhone();
        this.status = order.getStatus().name();
        this.shopId = order.getShopId();
        this.shopName = shopName;
        this.shopAddress = shopAddress;
        this.deliveryTime = order.getDeliveryTime();
        this.orderItems = order.getOrderItems();
    }
}
