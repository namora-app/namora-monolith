package com.namora.payment.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String orderId;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private String paymentMode;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    private String transactionReference;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
