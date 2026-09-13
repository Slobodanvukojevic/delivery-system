package com.delivery.delivery_service.dto;

import com.delivery.delivery_service.entity.DeliveryMethod;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateOrderRequest {

    private Long senderId;

    @NotBlank(message = "Ime posiljaoca je obavezno")
    private String senderName;

    @NotBlank(message = "Telefon posiljaoca je obavezan")
    private String senderPhone;

    @NotBlank(message = "Ime primaoca je obavezno")
    private String customerName;

    @NotBlank(message = "Telefon primaoca je obavezan")
    private String customerPhone;

    @NotNull(message = "Tezina je obavezna")
    @Min(value = 1, message = "Minimalna tezina je 1 kg")
    @Max(value = 30, message = "Maksimalna tezina je 30 kg")
    private Double weight;

    @NotBlank(message = "Adresa preuzimanja je obavezna")
    private String pickupAddress;

    @NotBlank(message = "Adresa dostave je obavezna")
    private String dropoffAddress;

    @NotNull(message = "Nacin dostave je obavezan")
    private DeliveryMethod deliveryMethod;

    private Long selectedBranchId;

    private Long selectedLockerId;
}