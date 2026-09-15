package com.delivery.report_service.repository;

import com.delivery.report_service.entity.ReportRequest;
import com.delivery.report_service.entity.ReportStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRequestRepository extends JpaRepository<ReportRequest, Long> {

    List<ReportRequest> findByStatus(ReportStatus status);
}