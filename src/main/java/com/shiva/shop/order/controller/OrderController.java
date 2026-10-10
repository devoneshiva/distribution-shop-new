
package com.shiva.shop.order.controller;

import com.shiva.shop.order.dto.CreateOrderRequest;
import com.shiva.shop.order.dto.OrderResponse;
import com.shiva.shop.order.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse createOrder(
            @Valid @RequestBody CreateOrderRequest request) {
        return orderService.createOrder(request);
    }

    @GetMapping ("/{orderId}")
    public OrderResponse getOrder(@PathVariable Long orderId){
        return orderService.getOrder(orderId);
    }
}
