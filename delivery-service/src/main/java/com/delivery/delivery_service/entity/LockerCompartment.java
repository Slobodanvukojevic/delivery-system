package com.delivery.delivery_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "locker_compartment")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LockerCompartment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "locker_id", nullable = false)
    private Long lockerId;

    @Column(name = "compartment_number", nullable = false)
    private Integer compartmentNumber;

    @Column(name = "is_occupied", nullable = false)
    @Builder.Default
    private Boolean isOccupied = false;

    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "reserved_at")
    private LocalDateTime reservedAt;
}