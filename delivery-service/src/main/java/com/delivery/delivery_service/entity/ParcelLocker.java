package com.delivery.delivery_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "parcel_locker")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParcelLocker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "location_name", nullable = false, length = 100)
    private String locationName;

    @Column(nullable = false, length = 255)
    private String address;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(name = "total_compartments", nullable = false)
    private Integer totalCompartments;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;
}