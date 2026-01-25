package com.namora.user.dto;

import jakarta.validation.constraints.NotBlank;

public record RiderRequest(
        @NotBlank
        String licenseNumber,
        @NotBlank
        String vehicleNumber
) {
}
