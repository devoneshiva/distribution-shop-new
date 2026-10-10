package com.shiva.shop.product.service;

import com.shiva.shop.common.exception.ProductNotFoundException;
import com.shiva.shop.product.dto.ProductResponse;
import com.shiva.shop.product.entity.Product;
import com.shiva.shop.product.repository.ProductRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void shouldReturnAllProducts() {

        Product product = new Product(
                "iPhone 17",
                "Apple smartphone",
                new BigDecimal("79999.00"),
                10
        );

        when(productRepository.findAll())
                .thenReturn(List.of(product));

        List<ProductResponse> result =
                productService.getProducts();

        assertEquals(1, result.size());
        assertEquals("iPhone 17", result.get(0).name());
        assertEquals(10, result.get(0).stock());

        verify(productRepository).findAll();
    }

    @Test
    void shouldReturnProductWhenItExists() {

        Product product = new Product(
                "iPhone 17",
                "Apple smartphone",
                new BigDecimal("79999.00"),
                10
        );

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        ProductResponse result =
                productService.getProduct(1L);

        assertEquals("iPhone 17", result.name());
        assertEquals(10, result.stock());

        verify(productRepository).findById(1L);
    }

    @Test
    void shouldThrowWhenProductDoesNotExist() {

        when(productRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                ProductNotFoundException.class,
                () -> productService.getProduct(999L)
        );

        verify(productRepository).findById(999L);
    }
}