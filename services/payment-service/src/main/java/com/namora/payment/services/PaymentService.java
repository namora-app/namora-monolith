package com.namora.payment.services;

import com.namora.payment.entities.Payment;
import com.namora.payment.entities.PaymentStatus;
import com.namora.payment.external.OrderClient;
import com.namora.payment.repositories.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository repository;
    private final OrderClient orderClient;

    public Payment processPayment(String orderId, BigDecimal amount, String mode) {
        Payment payment = Payment.builder()
                .orderId(orderId)
                .amount(amount)
                .paymentMode(mode)
                .status(PaymentStatus.PENDING)
                .transactionReference(UUID.randomUUID().toString())
                .build();
        repository.save(payment);
        boolean isSuccess = mockBankCall(amount);
        if (isSuccess) {
            payment.setStatus(PaymentStatus.SUCCESS);
            repository.save(payment);
            orderClient.markOrderConfirmed(orderId);
        } else {
            payment.setStatus(PaymentStatus.FAILED);
            payment = repository.save(payment);
            return payment;
        }
        return payment;
    }

    private boolean mockBankCall(BigDecimal amount) {
        return true;
    }
}