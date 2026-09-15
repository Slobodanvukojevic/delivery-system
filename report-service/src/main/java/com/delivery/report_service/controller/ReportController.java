package com.delivery.report_service.controller;

import com.delivery.report_service.dto.*;
import com.delivery.report_service.entity.ReportRequest;
import com.delivery.report_service.entity.ReportStatus;
import com.delivery.report_service.service.ReportService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/branch/{branchId}/monthly")
    public ResponseEntity<MonthlyReportResponse> getMonthlyReport(
            @PathVariable Long branchId,
            @RequestParam String month) {
        return ResponseEntity.ok(reportService.getMonthlyReport(branchId, month));
    }

    @GetMapping("/courier/{courierId}")
    public ResponseEntity<CourierPerformanceResponse> getCourierPerformance(
            @PathVariable Long courierId,
            @RequestParam String month) {
        return ResponseEntity.ok(reportService.getCourierPerformance(courierId, month));
    }

    @GetMapping("/top-couriers")
    public ResponseEntity<List<TopCourierResponse>> getTopCouriers(
            @RequestParam String month,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(reportService.getTopCouriers(month, limit));
    }

    @PostMapping("/generate")
    public ResponseEntity<ReportRequest> generateReport(
            @Valid @RequestBody GenerateReportRequest request) {
        return ResponseEntity.ok(reportService.generateReportRequest(request, 1L));
    }

    @GetMapping("/requests")
    public ResponseEntity<List<ReportRequest>> getRequests(
            @RequestParam(required = false) ReportStatus status) {
        if (status != null) {
            return ResponseEntity.ok(reportService.getRequestsByStatus(status));
        }
        return ResponseEntity.ok(reportService.getAllRequests());
    }
}