package com.namora.restaurant.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ItemRequest(
        @NotBlank
        String name,
        @NotBlank
        String description,
        boolean isVeg,
        @NotNull
        BigDecimal price
) {
}
