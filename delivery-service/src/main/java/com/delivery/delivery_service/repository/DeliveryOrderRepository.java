package com.delivery.delivery_service.repository;

import com.delivery.delivery_service.entity.DeliveryOrder;
import com.delivery.delivery_service.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryOrderRepository extends JpaRepository<DeliveryOrder, Long> {

    List<DeliveryOrder> findByStatus(OrderStatus status);

    List<DeliveryOrder> findByCustomerPhone(String customerPhone);

    Optional<DeliveryOrder> findByPickupCode(String pickupCode);

    List<DeliveryOrder> findByAssignedCourierId(Long courierId);

    List<DeliveryOrder> findBySelectedBranchId(Long branchId);
}