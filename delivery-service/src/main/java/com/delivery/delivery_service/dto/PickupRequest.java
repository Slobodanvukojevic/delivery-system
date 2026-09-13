package com.delivery.delivery_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PickupRequest {

    @NotBlank(message = "Pickup code je obavezan")
    private String pickupCode;
}