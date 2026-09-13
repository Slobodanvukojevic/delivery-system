package com.delivery.delivery_service.dto;

import com.delivery.delivery_service.entity.DeliveryMethod;
import com.delivery.delivery_service.entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {
    private Long id;
    private Long senderId;
    private Long customerId;
    private String senderName;
    private String senderPhone;
    private String customerName;
    private String customerPhone;
    private Double weight;
    private String pickupAddress;
    private String dropoffAddress;
    private OrderStatus status;
    private DeliveryMethod deliveryMethod;
    private Long selectedBranchId;
    private Long selectedLockerId;
    private String pickupCode;
    private Long assignedCourierId;
    private Double price;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deliveredAt;
}