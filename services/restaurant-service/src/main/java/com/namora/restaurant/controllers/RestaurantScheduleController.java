package com.namora.restaurant.controllers;

import com.namora.restaurant.dto.ApiResponse;
import com.namora.restaurant.dto.RestaurantScheduleRequest;
import com.namora.restaurant.services.RestaurantScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/restaurants/{id}/schedule")
@RequiredArgsConstructor
public class RestaurantScheduleController {

    private final RestaurantScheduleService restaurantScheduleService;

    @PostMapping
    public ResponseEntity<?> createOrUpdateRestaurantSchedule(@PathVariable String id, @RequestBody RestaurantScheduleRequest restaurantScheduleRequest) {
        try {
            RestaurantScheduleRequest cleaned = new RestaurantScheduleRequest(restaurantScheduleRequest.weekDay(), restaurantScheduleRequest.startTime(), restaurantScheduleRequest.endTime());
            return restaurantScheduleService.createOrUpdateRestaurantSchedule(cleaned, id);
        } catch (Exception e) {
            return new ResponseEntity<>(ApiResponse.error(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<?> getRestaurantSchedule(@PathVariable String id) {
        try {
            return restaurantScheduleService.findRestaurantSchedules(id);
        } catch (Exception e) {
            return new ResponseEntity<>(ApiResponse.error(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
