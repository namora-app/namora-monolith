package com.namora.user.dto;

import java.time.LocalDateTime;

public record CustomerDto(
        String userId,
        String name,
        String phoneNumber,
        String profileImageUrl,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String customerId
) {
}
