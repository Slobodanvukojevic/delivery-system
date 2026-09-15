package com.delivery.delivery_service.feign;

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
    private String type;
    private String pickupCode;
    private String phoneLocale;
    private String customMessage;
}