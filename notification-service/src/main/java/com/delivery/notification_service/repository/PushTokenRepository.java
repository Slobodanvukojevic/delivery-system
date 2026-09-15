package com.delivery.notification_service.repository;

import com.delivery.notification_service.entity.PushToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PushTokenRepository extends JpaRepository<PushToken, Long> {

    Optional<PushToken> findByUserId(Long userId);

    void deleteByUserId(Long userId);
}