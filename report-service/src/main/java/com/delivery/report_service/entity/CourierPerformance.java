package com.delivery.report_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "courier_performance",
        uniqueConstraints = @UniqueConstraint(columnNames = {"courier_id", "month"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourierPerformance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "courier_id", nullable = false)
    private Long courierId;

    @Column(nullable = false, length = 7)
    private String month;

    @Column(name = "delivered_count", nullable = false)
    @Builder.Default
    private Integer deliveredCount = 0;

    @Column(name = "returned_count", nullable = false)
    @Builder.Default
    private Integer returnedCount = 0;

    @Column(name = "avg_delivery_minutes")
    @Builder.Default
    private Integer avgDeliveryMinutes = 0;
}