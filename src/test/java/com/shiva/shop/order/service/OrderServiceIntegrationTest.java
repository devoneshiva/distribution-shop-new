
package com.shiva.shop.order.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import com.shiva.shop.common.exception.InsufficientStockException;
import com.shiva.shop.order.dto.CreateOrderRequest;
import com.shiva.shop.order.dto.OrderResponse;
import com.shiva.shop.order.repository.OrderRepository;
import com.shiva.shop.product.repository.ProductRepository;
import com.shiva.shop.product.entity.Product;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional
class OrderServiceIntegrationTest{

    @Autowired
    private OrderService orderService;

    @Autowired 
    private ProductRepository productRepository;
@PersistenceContext
private EntityManager entityManager;
    @Autowired 
    private OrderRepository orderRepository;

    @Test
    void shouldCreateOrderAndReduceProductStock(){

        Product product=new Product(
            "Integration Test Product",
            "Created by integration test",
            new BigDecimal("100.00"),
            10
        );
    

    Product savedProduct=productRepository.save(product);

    CreateOrderRequest request=new CreateOrderRequest(savedProduct.getId(), 3);
    OrderResponse response=orderService.createOrder(request);

     assertNotNull(response.orderId());
        assertEquals(savedProduct.getId(), response.productId());
        assertEquals(3, response.quantity());
        assertEquals(new BigDecimal("100.00"), response.unitPrice());
        assertEquals(new BigDecimal("300.00"), response.totalPrice());

        // Assert: verify the order was persisted
        assertTrue(orderRepository.existsById(response.orderId()));

        // Assert: verify stock decreased from 10 to 7
       entityManager.flush();
entityManager.clear();

        Product updatedProduct = productRepository.findById(product.getId())
        .orElseThrow();

assertEquals(7, updatedProduct.getStock());


    }

    
@Test
void shouldRejectOrderWhenStockIsInsufficient() {
    Product product = new Product(
            "Limited Stock Product",
            "Only two available",
            new BigDecimal("50.00"),
            2
    );

    Product savedProduct = productRepository.save(product);

    CreateOrderRequest request =
            new CreateOrderRequest(savedProduct.getId(), 3);

    assertThrows(
            InsufficientStockException.class,
            () -> orderService.createOrder(request)
    );

    Product unchangedProduct = productRepository
            .findById(savedProduct.getId())
            .orElseThrow();

    assertEquals(2, unchangedProduct.getStock());
}


    
}