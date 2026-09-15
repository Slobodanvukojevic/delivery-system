package com.delivery.notification_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RegisterTokenRequest {

    @NotNull(message = "User ID je obavezan")
    private Long userId;

    @NotBlank(message = "FCM token je obavezan")
    private String fcmToken;

    private String deviceType;
}