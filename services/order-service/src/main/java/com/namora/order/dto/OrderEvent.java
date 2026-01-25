package com.namora.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderEvent {
    private String orderId;
    private String restaurantId;
    private String status;
    private Double pickupLat;
    private Double pickupLon;
    private Double dropLat;
    private Double dropLon;
}
