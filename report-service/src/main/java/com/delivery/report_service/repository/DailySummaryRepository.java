package com.delivery.report_service.repository;

import com.delivery.report_service.entity.DailySummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DailySummaryRepository extends JpaRepository<DailySummary, Long> {

    Optional<DailySummary> findByBranchIdAndReportDate(Long branchId, LocalDate date);

    List<DailySummary> findByBranchIdAndReportDateBetween(Long branchId, LocalDate from, LocalDate to);

    List<DailySummary> findByReportDate(LocalDate date);
}