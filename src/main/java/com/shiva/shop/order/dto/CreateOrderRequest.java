package com.shiva.shop.order.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;



 public record CreateOrderRequest(

    @NotNull(message = "not null")
    @Positive(message = "only positive")
    Long productId,

    @NotNull(message = "Quantitu is required")
    @Positive (message = "only positie")
    Integer quantity
      
 ){}

// public record CreateOrderRequest(

//         @NotNull(message = "Product ID is required")
//         @Positive(message = "Product ID must be greater than zero")
//         Long productId,

//         @NotNull(message = "Quantity is required")
//         @Positive(message = "Quantity must be greater than zero")
//         Integer quantity

// ) {
// }