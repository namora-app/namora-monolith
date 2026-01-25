package com.namora.user.dto;

import java.time.LocalDateTime;

public record RiderDto(
        String userId,
        String name,
        String phoneNumber,
        String profileImageUrl,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String riderId,
        String licenseNumber,
        String vehicleNumber,
        boolean isApproved,
        double rating
) {
}
