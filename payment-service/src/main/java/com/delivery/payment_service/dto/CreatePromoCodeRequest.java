package com.delivery.payment_service.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreatePromoCodeRequest {

    @NotBlank(message = "Kod je obavezan")
    private String code;

    @NotNull(message = "Popust je obavezan")
    @Min(value = 1, message = "Popust mora biti najmanje 1%")
    @Max(value = 100, message = "Popust ne moze biti veci od 100%")
    private Integer discountPercent;

    @NotNull(message = "Datum isteka je obavezan")
    @Future(message = "Datum isteka mora biti u buducnosti")
    private LocalDate expiryDate;

    @NotNull(message = "Limit koriscenja je obavezan")
    @Min(value = 1, message = "Limit mora biti najmanje 1")
    private Integer usageLimit;
}