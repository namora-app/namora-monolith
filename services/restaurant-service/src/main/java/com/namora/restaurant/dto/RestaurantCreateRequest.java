package com.namora.restaurant.dto;

import jakarta.validation.constraints.NotBlank;

public record RestaurantCreateRequest(
        @NotBlank
        String name,

        @NotBlank
        String address,

        @NotBlank
        Double latitude,

        @NotBlank
        Double longitude,

        @NotBlank
        String fssaiLicense
) {
}
