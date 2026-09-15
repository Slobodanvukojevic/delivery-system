package com.delivery.notification_service.dto;

import com.delivery.notification_service.entity.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SendNotificationRequest {
    private Long userId;
    private Long orderId;
    private NotificationType type;
    private String pickupCode;
    private String phoneLocale;
    private String customMessage;
}