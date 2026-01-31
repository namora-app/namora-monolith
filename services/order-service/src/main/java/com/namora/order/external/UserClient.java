package com.namora.order.external;

import com.namora.order.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "USER-SERVICE")
public interface UserClient {

    @PostMapping("/internal/carts/create-order")
    ApiResponse<?> createOrder();

    @GetMapping("/internal/customers/{customerId}/addresses/{addressId}")
    ApiResponse<?> getAddress(@PathVariable("customerId") String customerId, @PathVariable("addressId") String addressId);

}
