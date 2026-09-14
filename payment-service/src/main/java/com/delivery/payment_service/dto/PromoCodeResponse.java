package com.delivery.payment_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PromoCodeResponse {
    private Long id;
    private String code;
    private Integer discountPercent;
    private LocalDate expiryDate;
    private Integer usageLimit;
    private Integer usedCount;
    private Boolean active;
}