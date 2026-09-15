package com.delivery.report_service.config;

import com.delivery.report_service.entity.CourierPerformance;
import com.delivery.report_service.repository.CourierPerformanceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final CourierPerformanceRepository courierPerformanceRepository;

    public DataSeeder(CourierPerformanceRepository courierPerformanceRepository) {
        this.courierPerformanceRepository = courierPerformanceRepository;
    }

    @Override
    public void run(String... args) {
        if (courierPerformanceRepository.count() > 0) {
            log.info("CourierPerformance tabela vec ima podatke. Preskacem seed.");
            return;
        }

        String currentMonth = java.time.YearMonth.now().toString();

        List<CourierPerformance> performances = List.of(
                CourierPerformance.builder()
                        .courierId(1L)
                        .month(currentMonth)
                        .deliveredCount(87)
                        .returnedCount(2)
                        .avgDeliveryMinutes(38)
                        .build(),
                CourierPerformance.builder()
                        .courierId(2L)
                        .month(currentMonth)
                        .deliveredCount(79)
                        .returnedCount(4)
                        .avgDeliveryMinutes(45)
                        .build(),
                CourierPerformance.builder()
                        .courierId(3L)
                        .month(currentMonth)
                        .deliveredCount(95)
                        .returnedCount(1)
                        .avgDeliveryMinutes(32)
                        .build()
        );

        courierPerformanceRepository.saveAll(performances);
        log.info("Ubaceno {} zapisa o ucinku kurira za mesec {}", performances.size(), currentMonth);
    }
}