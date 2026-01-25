package com.namora.payment.controllers;

import com.namora.payment.dto.PaymentRequest;
import com.namora.payment.entities.Payment;
import com.namora.payment.services.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService service;

    @PostMapping("/pay")
    public ResponseEntity<Payment> makePayment(@RequestBody PaymentRequest request) {
        return ResponseEntity.ok(service.processPayment(
                request.getOrderId(),
                request.getAmount(),
                request.getPaymentMode()
        ));
    }
}