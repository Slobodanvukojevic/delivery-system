package com.delivery.report_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TopCourierResponse {
    private Long courierId;
    private String fullName;
    private Integer deliveredCount;
}