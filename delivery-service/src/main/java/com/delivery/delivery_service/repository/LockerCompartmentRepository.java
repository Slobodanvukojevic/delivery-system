package com.delivery.delivery_service.repository;

import com.delivery.delivery_service.entity.LockerCompartment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LockerCompartmentRepository extends JpaRepository<LockerCompartment, Long> {

    List<LockerCompartment> findByLockerIdAndIsOccupiedFalse(Long lockerId);

    Optional<LockerCompartment> findByOrderId(Long orderId);
}