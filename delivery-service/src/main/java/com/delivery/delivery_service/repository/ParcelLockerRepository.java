package com.delivery.delivery_service.repository;

import com.delivery.delivery_service.entity.ParcelLocker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParcelLockerRepository extends JpaRepository<ParcelLocker, Long> {
}