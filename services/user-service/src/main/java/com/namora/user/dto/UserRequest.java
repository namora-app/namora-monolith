package com.namora.user.dto;

import jakarta.validation.constraints.NotBlank;

public record UserRequest(
        @NotBlank(message = "Name is required")
        String name,
        @NotBlank(message = "Phone Number is required")
        String phoneNumber
) {
}
