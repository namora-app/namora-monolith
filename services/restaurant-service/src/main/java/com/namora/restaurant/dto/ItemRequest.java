package com.namora.restaurant.dto;

import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public record ItemRequest(
        @NotBlank
        String name,
        @NotBlank
        String description,
        boolean isVeg,
        @NotBlank
        BigDecimal price,
        String category
) {
}
