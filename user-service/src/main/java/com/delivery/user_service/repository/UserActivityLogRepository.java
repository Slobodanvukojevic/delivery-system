package com.delivery.user_service.repository;

import com.delivery.user_service.entity.UserActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserActivityLogRepository extends JpaRepository<UserActivityLog, Long> {

    List<UserActivityLog> findByUserIdOrderByTimestampDesc(Long userId);

    List<UserActivityLog> findTop10ByUserIdOrderByTimestampDesc(Long userId);
}