package com.namora.payment.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(name = "ORDER-SERVICE")
public interface OrderClient {

    @PostMapping ("/orders/{id}/confirm")
    void markOrderConfirmed(@PathVariable("id") String orderId);
}
