package com.namora.restaurant.controllers;

import com.namora.restaurant.dto.ApiResponse;
import com.namora.restaurant.services.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public ResponseEntity<?> getTopItems(
            @RequestParam("latitude") double latitude,
            @RequestParam("longitude") double longitude,
            @RequestParam("name") String name
    ) {
        try {
            return itemService.getTopItems(name, latitude, longitude);
        } catch (Exception ex) {
            return new ResponseEntity<>(ApiResponse.error(ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/restaurants/{restaurantId}")
    public ResponseEntity<?> getItemsByRestaurant(@PathVariable("restaurantId") String restaurantId) {
        try {
            return itemService.getAllItemsByRestaurant(restaurantId);
        } catch (Exception ex) {
            return new ResponseEntity<>(ApiResponse.error(ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
