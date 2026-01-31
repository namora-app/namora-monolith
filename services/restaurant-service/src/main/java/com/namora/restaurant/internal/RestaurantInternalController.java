package com.namora.restaurant.internal;

import com.namora.restaurant.services.ItemService;
import com.namora.restaurant.services.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal")
@RequiredArgsConstructor
public class RestaurantInternalController {

    private final ItemService itemService;
    private final RestaurantService restaurantService;

    @GetMapping("/items/{itemId}")
    public ResponseEntity<?> getItemDetail(@PathVariable("itemId") String itemId) {
        try {
            return itemService.getItemById(itemId);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().build();
        }
    }


    @GetMapping("/restaurants/{restaurantId}/location")
    public ResponseEntity<?> getLocation(@PathVariable("restaurantId") String restaurantId) {
        return restaurantService.getLocation(restaurantId);
    }
}
