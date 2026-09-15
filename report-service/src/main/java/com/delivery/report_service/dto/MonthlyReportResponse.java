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
public class MonthlyReportResponse {
    private Long branchId;
    private String branchName;
    private String month;
    private Integer ordersReceived;
    private Integer ordersDelivered;
    private Integer ordersReturned;
    private BigDecimal totalRevenue;
    private Integer averageDeliveryMinutes;
}