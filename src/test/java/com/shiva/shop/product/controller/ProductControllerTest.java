package com.shiva.shop.product.controller;

import com.shiva.shop.common.exception.GlobalExceptionHandler;
import com.shiva.shop.common.exception.ProductNotFoundException;
import com.shiva.shop.product.dto.ProductResponse;
import com.shiva.shop.product.service.ProductService;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@Import(GlobalExceptionHandler.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @Test
    void shouldReturnProductsAsJson() throws Exception {

        when(productService.getProducts())
                .thenReturn(List.of(
                        new ProductResponse(
                                1L,
                                "iPhone 17",
                                "Apple smartphone",
                                new BigDecimal("79999.00"),
                                10
                        )
                ));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("iPhone 17"))
                .andExpect(jsonPath("$[0].stock").value(10));
    }

    @Test
    void shouldReturn404WhenProductDoesNotExist()
            throws Exception {

        when(productService.getProduct(999L))
                .thenThrow(new ProductNotFoundException(999L));

        mockMvc.perform(get("/api/products/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title")
                        .value("Product not found"))
                .andExpect(jsonPath("$.status").value(404));
    }
}