package com.delivery.report_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class GenerateReportRequest {

    @NotNull(message = "Branch ID je obavezan")
    private Long branchId;

    @NotNull(message = "Datum je obavezan")
    private LocalDate date;
}