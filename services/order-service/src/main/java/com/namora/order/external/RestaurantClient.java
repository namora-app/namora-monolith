package com.namora.order.external;

import com.namora.order.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "RESTAURANT-SERVICE")
public interface RestaurantClient {

    @GetMapping("/internal/items/{itemId}")
    ApiResponse<?> getItemDetails(@PathVariable("itemId") String itemId);

    @GetMapping("/internal/restaurants/{restaurantId}/location")
    ApiResponse<?> getRestaurantAddress(@PathVariable("restaurantId") String restaurantId);
}
