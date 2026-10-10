package com.shiva.shop.order.dto;

import com.shiva.shop.order.entity.Order;
import com.shiva.shop.order.entity.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;





public record OrderResponse(
        Long orderId,
        Long productId,
        String productName,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal totalPrice,
        OrderStatus status,
        LocalDateTime createdAt
) {

    public static OrderResponse from(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getProduct().getId(),
                order.getProduct().getName(),
                order.getQuantity(),
                order.getUnitPrice(),
                order.getTotalPrice(),
                order.getStatus(),
                order.getCreatedAt()
        );
    }
}