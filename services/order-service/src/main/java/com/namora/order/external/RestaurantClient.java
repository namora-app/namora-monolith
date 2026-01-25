package com.namora.order.external;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "RESTAURANT-SERVICE")
public interface RestaurantClient {
}
