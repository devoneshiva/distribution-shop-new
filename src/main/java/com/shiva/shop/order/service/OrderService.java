
package com.shiva.shop.order.service;

import com.shiva.shop.common.exception.InsufficientStockException;
import com.shiva.shop.order.dto.CreateOrderRequest;
import com.shiva.shop.order.dto.OrderResponse;
import com.shiva.shop.order.entity.Order;
import com.shiva.shop.order.repository.OrderRepository;
import com.shiva.shop.product.entity.Product;
import com.shiva.shop.product.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    private static final Logger log =
            LoggerFactory.getLogger(OrderService.class);

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    public OrderService(
            ProductRepository productRepository,
            OrderRepository orderRepository) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

  @Transactional
public OrderResponse createOrder(CreateOrderRequest request) {
    log.info(
        "Creating order for productId={}, quantity={}",
        request.productId(),
        request.quantity()
    );

    Product product = productRepository.findById(request.productId())
        .orElseThrow(() -> new EntityNotFoundException(
            "Product not found with id: " + request.productId()
        ));

    int updatedRows = productRepository.deductStock(
        request.productId(),
        request.quantity()
    );

    if (updatedRows == 0) {
        log.warn(
            "Order rejected: insufficient stock for productId={}",
            request.productId()
        );

        throw new InsufficientStockException(
            "Insufficient stock for product id: " + request.productId()
        );
    }

    Order order = new Order(
        product,
        request.quantity(),
        product.getPrice()
    );

    Order savedOrder = orderRepository.save(order);

    log.info(
        "Order created successfully with orderId={}",
        savedOrder.getId()
    );

    return OrderResponse.from(savedOrder);
}

    @Transactional(readOnly = true)
    public OrderResponse getOrder(Long orderId){

        log.debug("Fetching order with id={}",orderId);
        Order order=orderRepository.findById(orderId).orElseThrow(()->new EntityNotFoundException("order not found:"+orderId));

        return OrderResponse.from(order);
    }
}
