package com.delivery.payment_service.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentRequest {

    @NotNull(message = "Order ID je obavezan")
    private Long orderId;

    private Long userId;

    @NotNull(message = "Iznos je obavezan")
    @DecimalMin(value = "0.0", inclusive = false, message = "Iznos mora biti veci od 0")
    private BigDecimal amount;

    private String currency = "RSD";

    private String promoCode;
}