package com.delivery.report_service.repository;

import com.delivery.report_service.entity.CourierPerformance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourierPerformanceRepository extends JpaRepository<CourierPerformance, Long> {

    Optional<CourierPerformance> findByCourierIdAndMonth(Long courierId, String month);

    List<CourierPerformance> findByMonthOrderByDeliveredCountDesc(String month);
}