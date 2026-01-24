package com.namora.restaurant.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemSearchResult {
    private String id;
    private String restaurantId;
    private String name;
    private String description;
    private boolean isVeg;
    private BigDecimal price;
    private boolean isAvailable;
    private float rating;
    private BigDecimal discountPercent;
    private Double distance;
    private Double distanceKm;

    public ItemSearchResult(String id, String restaurantId, String name, String description,
                            boolean isVeg, BigDecimal price, boolean isAvailable,
                            float rating, BigDecimal discountPercent, Double distance) {
        this.id = id;
        this.restaurantId = restaurantId;
        this.name = name;
        this.description = description;
        this.isVeg = isVeg;
        this.price = price;
        this.isAvailable = isAvailable;
        this.rating = rating;
        this.discountPercent = discountPercent;
        this.distance = distance;
        this.distanceKm = distance != null ? distance / 1000 : null;
    }
}
