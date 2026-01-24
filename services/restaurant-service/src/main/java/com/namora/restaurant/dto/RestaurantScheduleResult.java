package com.namora.restaurant.dto;

import com.namora.restaurant.entities.RestaurantSchedule;

import java.util.List;

public record RestaurantScheduleResult(
        String restaurantId,
        List<RestaurantSchedule> schedule
) {
}
