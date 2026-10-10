package com.shiva.shop.product.controller;

import com.shiva.shop.product.dto.CreateProductRequest;
import com.shiva.shop.product.dto.ProductResponse;
import com.shiva.shop.product.service.ProductService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<ProductResponse> getProducts() {
        return productService.getProducts();
    }

    @GetMapping("/{id}")
    public ProductResponse getProduct(@PathVariable Long id) {
        return productService.getProduct(id);
    }

    @PostMapping
@ResponseStatus(HttpStatus.CREATED)
public ProductResponse createProduct(
        @Valid @RequestBody CreateProductRequest request) {
    return productService.createProduct(request);
}
}