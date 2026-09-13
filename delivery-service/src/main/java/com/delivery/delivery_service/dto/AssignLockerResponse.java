package com.delivery.delivery_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignLockerResponse {
    private Long orderId;
    private Long lockerId;
    private String lockerName;
    private Integer compartmentNumber;
    private String pickupCode;
    private String status;
    private String message;
}