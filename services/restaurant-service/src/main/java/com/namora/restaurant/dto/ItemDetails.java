package com.namora.restaurant.dto;

import java.math.BigDecimal;

public record ItemDetails(
        String itemId,
        String name,
        String restaurantId,
        BigDecimal discountPercent,
        BigDecimal price
) {
}