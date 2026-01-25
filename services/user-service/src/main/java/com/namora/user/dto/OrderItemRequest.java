package com.namora.user.dto;

public record OrderItemRequest(
        String itemId,
        int quantity
) {
}
