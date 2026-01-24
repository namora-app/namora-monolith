package com.namora.restaurant.repositories;

import com.namora.restaurant.entities.Item;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, String> {

    @Query("SELECT r FROM Item r WHERE r.restaurant.id = :restaurantId")
    List<Item> findByRestaurantId(@Param("restaurantId") String restaurantId);

    @Cacheable(value = "topItems", key = "#name + '_' + #latitude + '_' + #longitude")
    @Query(value = """
            SELECT i.* FROM items i
            INNER JOIN restaurants r ON i.restaurant_id = r.id
            WHERE i.is_available = true
            AND r.location IS NOT NULL
            AND ST_DWithin(
                r.location,
                ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)::geography,
                50000
            )
            AND (
                LOWER(i.name) LIKE LOWER(CONCAT('%', :name, '%'))
                OR similarity(LOWER(i.name), LOWER(:name)) > 0.3
            )
            ORDER BY 
                CASE 
                    WHEN LOWER(i.name) = LOWER(:name) THEN 0
                    WHEN LOWER(i.name) LIKE LOWER(CONCAT(:name, '%')) THEN 1
                    ELSE 2
                END,
                ST_Distance(
                    r.location,
                    ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)::geography
                ),
                i.rating DESC
            LIMIT 10
            """, nativeQuery = true)
    List<Item> getTopItems(
            @Param("name") String name,
            @Param("latitude") double latitude,
            @Param("longitude") double longitude
    );
}