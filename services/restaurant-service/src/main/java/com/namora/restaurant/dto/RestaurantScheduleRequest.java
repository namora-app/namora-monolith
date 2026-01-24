package com.namora.restaurant.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalTime;

public record RestaurantScheduleRequest(
        @NotBlank
        String weekDay,

        @NotBlank
        LocalTime startTime,

        @NotBlank
        LocalTime endTime
) {
}
