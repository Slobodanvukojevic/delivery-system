package com.delivery.report_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourierPerformanceResponse {
    private Long courierId;
    private String courierName;
    private String month;
    private Integer deliveredCount;
    private Integer returnedCount;
    private Integer averageDeliveryMinutes;
}