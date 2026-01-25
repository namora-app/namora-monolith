package com.namora.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CartItemRequest(
        @NotNull
        int quantity,
        @NotBlank
        String itemId
) {
}
