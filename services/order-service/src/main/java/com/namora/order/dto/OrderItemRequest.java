package com.namora.order.dto;

import lombok.Data;

import java.util.List;

@Data
public class OrderItemRequest {
    String customerId;
    List<CartItemRequest> orderItems;
}
