
package com.shiva.shop.order.controller;

import com.shiva.shop.product.entity.Product;
import com.shiva.shop.product.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class OrderControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void shouldCreateAndRetrieveOrderThroughHttp() throws Exception {

        // Arrange: create a product in the database
        Product product = productRepository.save(
                new Product(
                        "HTTP Integration Product",
                        "Test product",
                        new BigDecimal("100.00"),
                        10
                )
        );

        String requestJson = """
                {
                    "productId": %d,
                    "quantity": 2
                }
                """.formatted(product.getId());

        // Act: create an order through POST
        String responseJson = mockMvc.perform(
                        post("/api/orders")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Extract orderId from the JSON response
        long orderId = com.fasterxml.jackson.databind.json.JsonMapper
                .builder()
                .build()
                .readTree(responseJson)
                .get("orderId")
                .asLong();

        // Assert: retrieve the same order through GET
        mockMvc.perform(get("/api/orders/" + orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(orderId))
                .andExpect(jsonPath("$.productId").value(product.getId()))
                .andExpect(jsonPath("$.quantity").value(2))
                .andExpect(jsonPath("$.totalPrice").value(200.00));
    }


    @Test
void shouldRejectOrderWithInvalidQuantity() throws Exception {
    String requestJson = """
            {
                "productId": 1,
                "quantity": 0
            }
            """;

    mockMvc.perform(
                    post("/api/orders")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJson)
            )
            .andExpect(status().isBadRequest());
}

@Test
void shouldReturnBadRequestForInvalidOrder() throws Exception {
    String requestJson = """
        {
          "productId": 1,
          "quantity": 0
        }
        """;

    mockMvc.perform(post("/api/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.title").value("Validation failed"))
        .andExpect(jsonPath("$.detail").value(
            org.hamcrest.Matchers.containsString("quantity")
        ));
}
}
