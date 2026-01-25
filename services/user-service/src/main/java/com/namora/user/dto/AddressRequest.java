package com.namora.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AddressRequest(
        @NotNull(message = "Latitude is required")
        double latitude,
        @NotNull(message = "Longitude is required")
        double longitude,
        @NotBlank(message = "Address is required")
        String address
) {
}
