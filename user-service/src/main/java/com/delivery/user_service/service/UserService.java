package com.delivery.user_service.service;

import com.delivery.user_service.dto.UserResponse;
import com.delivery.user_service.entity.User;
import com.delivery.user_service.entity.UserActivityLog;
import com.delivery.user_service.entity.UserRole;
import com.delivery.user_service.exception.UserNotFoundException;
import com.delivery.user_service.repository.UserActivityLogRepository;
import com.delivery.user_service.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserActivityLogRepository activityLogRepository;

    public UserService(UserRepository userRepository,
                       UserActivityLogRepository activityLogRepository) {
        this.userRepository = userRepository;
        this.activityLogRepository = activityLogRepository;
    }

    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        return mapToResponse(user);
    }

    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
        return mapToResponse(user);
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<UserResponse> getUsersByRole(UserRole role) {
        return userRepository.findByRole(role).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<UserResponse> getUsersByBranch(Long branchId) {
        return userRepository.findByBranchId(branchId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public void updateFcmToken(Long userId, String fcmToken) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        user.setFcmToken(fcmToken);
        userRepository.save(user);
    }

    public List<UserActivityLog> getUserActivity(Long userId) {
        return activityLogRepository.findByUserIdOrderByTimestampDesc(userId);
    }

    private UserResponse mapToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .role(user.getRole())
                .branchId(user.getBranchId())
                .active(user.getActive())
                .build();
    }
}