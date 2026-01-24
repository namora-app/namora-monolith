package com.namora.restaurant.repositories;

import com.namora.restaurant.entities.RestaurantSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RestaurantScheduleRepository extends JpaRepository<RestaurantSchedule, String> {

    @Query("SELECT r FROM RestaurantSchedule r WHERE r.restaurant.id = :restaurantId")
    List<RestaurantSchedule> findByRestaurantId(@Param("restaurantId") String restaurantId);
}