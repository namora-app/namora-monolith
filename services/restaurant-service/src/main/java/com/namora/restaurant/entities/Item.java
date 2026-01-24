package com.namora.restaurant.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "items")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_veg", nullable = false)
    private boolean isVeg;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "is_available")
    @Builder.Default
    private boolean isAvailable = true;

    @Column(name = "rating")
    @Builder.Default
    private float rating = 0;

    @Column(name = "discount_percent")
    @Builder.Default
    private BigDecimal discountPercent = BigDecimal.ZERO;

    public BigDecimal getFinalPrice() {
        if (discountPercent == null || discountPercent.compareTo(BigDecimal.ZERO) == 0) {
            return price;
        }
        BigDecimal discountAmount = price.multiply(discountPercent).divide(new BigDecimal(100));
        return price.subtract(discountAmount);
    }
}
