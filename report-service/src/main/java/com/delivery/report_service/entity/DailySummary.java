package com.delivery.report_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "daily_summary",
        uniqueConstraints = @UniqueConstraint(columnNames = {"branch_id", "report_date"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailySummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "branch_id", nullable = false)
    private Long branchId;

    @Column(name = "report_date", nullable = false)
    private LocalDate reportDate;

    @Column(name = "orders_received", nullable = false)
    @Builder.Default
    private Integer ordersReceived = 0;

    @Column(name = "orders_delivered", nullable = false)
    @Builder.Default
    private Integer ordersDelivered = 0;

    @Column(name = "orders_returned", nullable = false)
    @Builder.Default
    private Integer ordersReturned = 0;

    @Column(name = "total_revenue", precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal totalRevenue = BigDecimal.ZERO;

    @Column(name = "generated_at", nullable = false)
    private LocalDateTime generatedAt;

    @PrePersist
    protected void onCreate() {
        if (this.generatedAt == null) {
            this.generatedAt = LocalDateTime.now();
        }
    }
}