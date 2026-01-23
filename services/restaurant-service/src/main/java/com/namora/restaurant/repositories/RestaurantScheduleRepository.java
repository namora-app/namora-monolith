package com.namora.restaurant.repositories;

import com.namora.restaurant.entities.RestaurantSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RestaurantScheduleRepository extends JpaRepository<RestaurantSchedule, String> {
}
