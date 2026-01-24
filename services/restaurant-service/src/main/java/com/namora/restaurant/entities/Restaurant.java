package com.namora.restaurant.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "restaurants")
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private String id;

    @Column(name = "owner_id", nullable = false)
    private String ownerId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "is_open")
    @Builder.Default
    private boolean isOpen = true;

    @Column(columnDefinition = "TEXT")
    private String address;

    private Double latitude;
    private Double longitude;

    @Column(columnDefinition = "geography(Point, 4326)")
    private String location;

    @Column(name = "fssai_license")
    private String fssaiLicense;

    @CreationTimestamp
    @Column(name = "joined_at", updatable = false)
    private LocalDateTime joinedAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RestaurantSchedule> schedules = new ArrayList<>();
}