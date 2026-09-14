package com.delivery.payment_service.dto;

import com.delivery.payment_service.entity.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponse {
    private Long transactionId;
    private Long orderId;
    private BigDecimal originalAmount;
    private BigDecimal discountApplied;
    private BigDecimal finalAmount;
    private String currency;
    private PaymentStatus status;
    private String invoiceNumber;
    private LocalDateTime timestamp;
    private String message;
}