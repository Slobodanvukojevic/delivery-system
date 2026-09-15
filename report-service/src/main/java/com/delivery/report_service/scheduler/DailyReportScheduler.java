package com.delivery.report_service.scheduler;

import com.delivery.report_service.service.ReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class DailyReportScheduler {

    private static final Logger log = LoggerFactory.getLogger(DailyReportScheduler.class);

    private static final List<Long> BRANCH_IDS = List.of(1L, 2L, 3L);

    private final ReportService reportService;

    public DailyReportScheduler(ReportService reportService) {
        this.reportService = reportService;
    }

    @Scheduled(cron = "${report.daily-cron:0 0 1 * * *}")
    public void generateDailyReports() {
        log.info("Pokrecem generisanje dnevnih izvestaja za dan {}", LocalDate.now());
        LocalDate yesterday = LocalDate.now().minusDays(1);

        for (Long branchId : BRANCH_IDS) {
            try {
                reportService.generateDailySummary(branchId, yesterday);
                log.info("Izvestaj generisan za branch {}", branchId);
            } catch (Exception e) {
                log.error("Greska pri generisanju izvestaja za branch {}: {}", branchId, e.getMessage());
            }
        }
    }
}