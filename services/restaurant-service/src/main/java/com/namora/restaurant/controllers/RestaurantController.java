package com.namora.restaurant.controllers;

import com.namora.restaurant.dto.ApiResponse;
import com.namora.restaurant.dto.RestaurantCreateRequest;
import com.namora.restaurant.services.RestaurantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/restaurants")
@RequiredArgsConstructor
public class RestaurantController {
    private final RestaurantService restaurantService;

    @PostMapping
    public ResponseEntity<?> createRestaurant(@RequestBody RestaurantCreateRequest restaurantCreateRequest) {
        try {
            return restaurantService.createRestaurant(restaurantCreateRequest);
        } catch (Exception e) {
            return new ResponseEntity<>(ApiResponse.error(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateRestaurant(
            @Valid @RequestBody RestaurantCreateRequest restaurantCreateRequest,
            @PathVariable("id") String id) {
        try {
            return restaurantService.updateRestaurant(id, restaurantCreateRequest);
        } catch (Exception e) {
            return new ResponseEntity<>(ApiResponse.error(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/owner")
    public ResponseEntity<?> getAllRestaurantOwner() {
        try {
            return restaurantService.getAllRestaurants();
        } catch (Exception e) {
            return new ResponseEntity<>(ApiResponse.error(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> toggleIsOpen(@PathVariable("id") String id) {
        try {
            return restaurantService.toggleOpenStatus(id);
        } catch (Exception e) {
            return new ResponseEntity<>(ApiResponse.error(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}