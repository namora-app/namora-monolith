package com.namora.restaurant.controllers;

import com.namora.restaurant.dto.ApiResponse;
import com.namora.restaurant.dto.ItemRequest;
import com.namora.restaurant.services.ItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/restaurants/{restaurantId}/items")
@RequiredArgsConstructor
public class RestaurantItemController {

    private final ItemService itemService;

    @PostMapping
    public ResponseEntity<?> addItem(@PathVariable("restaurantId") String restaurantId, @Valid @RequestBody ItemRequest itemRequest) {
        try {
            return itemService.addItem(restaurantId, itemRequest);
        } catch (Exception error) {
            return new ResponseEntity<>(ApiResponse.error(error.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{itemId}")
    public ResponseEntity<?> updateItem(@Valid @RequestBody ItemRequest itemRequest, @PathVariable("itemId") String itemId, @PathVariable("restaurantId") String restaurantId) {
        try {
            return itemService.updateItem(restaurantId, itemId, itemRequest);
        } catch (Exception error) {
            return new ResponseEntity<>(ApiResponse.error(error.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<?> deleteItem(@PathVariable String restaurantId, @PathVariable("itemId") String itemId) {
        try {
            return itemService.deleteItem(restaurantId, itemId);
        } catch (Exception error) {
            return new ResponseEntity<>(ApiResponse.error(error.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
