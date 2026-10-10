
package com.shiva.shop.product.controller;

import com.shiva.shop.product.entity.Product;
import com.shiva.shop.product.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ProductControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void shouldCreateProduct() throws Exception {
        String requestJson = """
                {
                    "name": "Integration Keyboard",
                    "description": "Test keyboard",
                    "price": 2499.00,
                    "stock": 20
                }
                """;

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Integration Keyboard"))
                .andExpect(jsonPath("$.stock").value(20));
    }

    @Test
    void shouldRejectInvalidProduct() throws Exception {
        String requestJson = """
                {
                    "name": "   ",
                    "description": "Invalid product",
                    "price": -100,
                    "stock": -5
                }
                """;

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRetrieveCreatedProduct() throws Exception {
        Product product = productRepository.save(
                new Product(
                        "Existing Keyboard",
                        "Saved for retrieval test",
                        new java.math.BigDecimal("1500.00"),
                        5
                )
        );

        mockMvc.perform(get("/api/products/" + product.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(product.getId()))
                .andExpect(jsonPath("$.name").value("Existing Keyboard"));
    }

    
@Test
void shouldReturnBadRequestForInvalidProduct() throws Exception {
    String requestJson = """
            {
                "name": "   ",
                "description": "Invalid product",
                "price": -100,
                "stock": -5
            }
            """;

    mockMvc.perform(post("/api/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestJson))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400));
}

}
