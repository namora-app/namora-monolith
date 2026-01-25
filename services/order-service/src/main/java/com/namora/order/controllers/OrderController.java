package com.namora.order.controllers;

import com.namora.order.dto.ApiResponse;
import com.namora.order.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/{addressId}")
    public ResponseEntity<?> createOrder(@PathVariable("addressId") String addressId) {
        try {
            return orderService.placeOrder(addressId);
        } catch (Exception e) {
            return new ResponseEntity<>(ApiResponse.error(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("{orderId}/confirm")
    public ResponseEntity<?> confirmOrder(@PathVariable("orderId") String orderId) {
        try {
            return orderService.confirmOrder(orderId);
        } catch (Exception e) {
            return new ResponseEntity<>(ApiResponse.error(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
