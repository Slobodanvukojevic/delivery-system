package com.delivery.report_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderStatsResponse {
    private Long branchId;
    private String date;
    private Integer ordersReceived;
    private Integer ordersDelivered;
    private Integer ordersReturned;
    private BigDecimal totalRevenue;
}