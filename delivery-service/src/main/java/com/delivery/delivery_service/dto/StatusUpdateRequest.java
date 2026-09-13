package com.delivery.delivery_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class StatusUpdateRequest {

    @NotBlank(message = "Status je obavezan")
    private String status;
}