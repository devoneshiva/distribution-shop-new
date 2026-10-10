package com.shiva.shop.product.service;

import com.shiva.shop.common.exception.ProductNotFoundException;
import com.shiva.shop.product.dto.CreateProductRequest;
import com.shiva.shop.product.dto.ProductResponse;
import com.shiva.shop.product.entity.Product;
import com.shiva.shop.product.repository.ProductRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductService {

    private static final Logger log =
            LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getProducts() {

        log.info("Fetching product catalogue");

        List<ProductResponse> products =
                productRepository.findAll()
                        .stream()
                        .map(ProductResponse::from)
                        .toList();

        log.info("Product catalogue fetched, count={}",
                products.size());

        return products;
    }

    @Transactional(readOnly = true)
    public ProductResponse getProduct(Long id) {

        log.debug("Fetching product, productId={}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Product not found, productId={}", id);
                    return new ProductNotFoundException(id);
                });

        return ProductResponse.from(product);
    }

    
@Transactional
public ProductResponse createProduct(CreateProductRequest request) {
    log.info("Creating product: name={}", request.name());

    Product product = new Product(
            request.name(),
            request.description(),
            request.price(),
            request.stock()
    );

    Product savedProduct = productRepository.save(product);

    log.info("Product created successfully: productId={}",
            savedProduct.getId());

    return ProductResponse.from(savedProduct);
}

}