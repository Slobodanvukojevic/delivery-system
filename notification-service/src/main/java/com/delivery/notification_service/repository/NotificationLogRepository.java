package com.delivery.notification_service.repository;

import com.delivery.notification_service.entity.NotificationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationLogRepository extends JpaRepository<NotificationLog, Long> {

    List<NotificationLog> findByUserIdOrderBySentAtDesc(Long userId);

    List<NotificationLog> findByOrderId(Long orderId);
}