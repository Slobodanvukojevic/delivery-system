package com.delivery.payment_service.repository;

import com.delivery.payment_service.entity.PaymentTransaction;
import com.delivery.payment_service.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {

    List<PaymentTransaction> findByUserId(Long userId);

    List<PaymentTransaction> findByOrderId(Long orderId);

    List<PaymentTransaction> findByStatus(PaymentStatus status);
}