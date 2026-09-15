package com.delivery.report_service.service;

import com.delivery.report_service.dto.*;
import com.delivery.report_service.entity.*;
import com.delivery.report_service.exception.ReportNotFoundException;
import com.delivery.report_service.feign.DeliveryClient;
import com.delivery.report_service.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private static final Logger log = LoggerFactory.getLogger(ReportService.class);

    private final DailySummaryRepository dailySummaryRepository;
    private final CourierPerformanceRepository courierPerformanceRepository;
    private final ReportRequestRepository reportRequestRepository;
    private final DeliveryClient deliveryClient;

    public ReportService(DailySummaryRepository dailySummaryRepository,
                         CourierPerformanceRepository courierPerformanceRepository,
                         ReportRequestRepository reportRequestRepository,
                         DeliveryClient deliveryClient) {
        this.dailySummaryRepository = dailySummaryRepository;
        this.courierPerformanceRepository = courierPerformanceRepository;
        this.reportRequestRepository = reportRequestRepository;
        this.deliveryClient = deliveryClient;
    }

    // ============ DNEVNA AGREGACIJA ============

    @Transactional
    public DailySummary generateDailySummary(Long branchId, LocalDate date) {
        log.info("Generisanje dnevnog izvestaja za branch {} za {}", branchId, date);

        OrderStatsResponse stats;
        try {
            stats = deliveryClient.getStats(branchId, date.toString());
        } catch (Exception e) {
            log.warn("Greska pri pozivu Delivery Service-a: {}. Koristim praznu statistiku.", e.getMessage());
            stats = OrderStatsResponse.builder()
                    .branchId(branchId)
                    .date(date.toString())
                    .ordersReceived(0)
                    .ordersDelivered(0)
                    .ordersReturned(0)
                    .totalRevenue(BigDecimal.ZERO)
                    .build();
        }

        DailySummary summary = dailySummaryRepository
                .findByBranchIdAndReportDate(branchId, date)
                .orElseGet(() -> DailySummary.builder()
                        .branchId(branchId)
                        .reportDate(date)
                        .build());

        summary.setOrdersReceived(stats.getOrdersReceived());
        summary.setOrdersDelivered(stats.getOrdersDelivered());
        summary.setOrdersReturned(stats.getOrdersReturned());
        summary.setTotalRevenue(stats.getTotalRevenue());
        summary.setGeneratedAt(java.time.LocalDateTime.now());

        return dailySummaryRepository.save(summary);
    }

    // ============ MESECNI IZVESTAJ ============

    public MonthlyReportResponse getMonthlyReport(Long branchId, String month) {
        YearMonth ym = YearMonth.parse(month);
        LocalDate from = ym.atDay(1);
        LocalDate to = ym.atEndOfMonth();

        List<DailySummary> summaries = dailySummaryRepository
                .findByBranchIdAndReportDateBetween(branchId, from, to);

        if (summaries.isEmpty()) {
            throw new ReportNotFoundException("Nema podataka za branch " + branchId + " u mesecu " + month);
        }

        int received = summaries.stream().mapToInt(DailySummary::getOrdersReceived).sum();
        int delivered = summaries.stream().mapToInt(DailySummary::getOrdersDelivered).sum();
        int returned = summaries.stream().mapToInt(DailySummary::getOrdersReturned).sum();
        BigDecimal revenue = summaries.stream()
                .map(DailySummary::getTotalRevenue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<CourierPerformance> couriers = courierPerformanceRepository
                .findByMonthOrderByDeliveredCountDesc(month);
        int avgMinutes = couriers.isEmpty() ? 0 :
                (int) couriers.stream().mapToInt(CourierPerformance::getAvgDeliveryMinutes).average().orElse(0);

        return MonthlyReportResponse.builder()
                .branchId(branchId)
                .branchName("Poslovnica " + branchId)
                .month(month)
                .ordersReceived(received)
                .ordersDelivered(delivered)
                .ordersReturned(returned)
                .totalRevenue(revenue)
                .averageDeliveryMinutes(avgMinutes)
                .build();
    }

    // ============ UČINAK KURIRA ============

    public CourierPerformanceResponse getCourierPerformance(Long courierId, String month) {
        CourierPerformance perf = courierPerformanceRepository
                .findByCourierIdAndMonth(courierId, month)
                .orElseThrow(() -> new ReportNotFoundException(
                        "Nema podataka za kurira " + courierId + " u mesecu " + month));

        return CourierPerformanceResponse.builder()
                .courierId(perf.getCourierId())
                .courierName("Kurir " + perf.getCourierId())
                .month(perf.getMonth())
                .deliveredCount(perf.getDeliveredCount())
                .returnedCount(perf.getReturnedCount())
                .averageDeliveryMinutes(perf.getAvgDeliveryMinutes())
                .build();
    }

    public List<TopCourierResponse> getTopCouriers(String month, int limit) {
        return courierPerformanceRepository.findByMonthOrderByDeliveredCountDesc(month)
                .stream()
                .limit(limit)
                .map(p -> TopCourierResponse.builder()
                        .courierId(p.getCourierId())
                        .fullName("Kurir " + p.getCourierId())
                        .deliveredCount(p.getDeliveredCount())
                        .build())
                .collect(Collectors.toList());
    }

    // ============ MANUALNO GENERISANJE ============

    @Transactional
    public ReportRequest generateReportRequest(GenerateReportRequest request, Long requestedBy) {
        ReportRequest reportRequest = ReportRequest.builder()
                .requestedBy(requestedBy)
                .reportType("DAILY")
                .period(request.getDate().toString())
                .status(ReportStatus.IN_PROGRESS)
                .build();
        ReportRequest saved = reportRequestRepository.save(reportRequest);

        try {
            generateDailySummary(request.getBranchId(), request.getDate());
            saved.setStatus(ReportStatus.COMPLETED);
        } catch (Exception e) {
            log.error("Greska pri generisanju izvestaja: {}", e.getMessage());
            saved.setStatus(ReportStatus.FAILED);
        }

        return reportRequestRepository.save(saved);
    }

    public List<ReportRequest> getAllRequests() {
        return reportRequestRepository.findAll();
    }

    public List<ReportRequest> getRequestsByStatus(ReportStatus status) {
        return reportRequestRepository.findByStatus(status);
    }
}