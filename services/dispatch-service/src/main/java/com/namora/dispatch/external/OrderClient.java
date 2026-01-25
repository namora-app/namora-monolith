package com.namora.dispatch.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "ORDER-SERVICE")
public interface OrderClient {
    @PutMapping("/orders/{id}/assign-rider")
    void assignRider(@PathVariable("id") String orderId, @RequestParam("riderId") String riderId);
}
