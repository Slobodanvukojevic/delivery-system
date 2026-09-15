package com.delivery.user_service.controller;

import com.delivery.user_service.dto.UserResponse;
import com.delivery.user_service.entity.UserActivityLog;
import com.delivery.user_service.entity.UserRole;
import com.delivery.user_service.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(@AuthenticationPrincipal Jwt jwt) {
        Long userId = extractUserId(jwt);
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers(
            @RequestParam(required = false) UserRole role,
            @RequestParam(required = false) Long branchId) {
        if (role != null) {
            return ResponseEntity.ok(userService.getUsersByRole(role));
        }
        if (branchId != null) {
            return ResponseEntity.ok(userService.getUsersByBranch(branchId));
        }
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PutMapping("/me/fcm-token")
    public ResponseEntity<Map<String, String>> updateFcmToken(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody Map<String, String> body) {
        Long userId = extractUserId(jwt);
        String fcmToken = body.get("fcmToken");
        userService.updateFcmToken(userId, fcmToken);
        return ResponseEntity.ok(Map.of("message", "FCM token uspesno azuriran"));
    }

    @GetMapping("/{id}/activity")
    public ResponseEntity<List<UserActivityLog>> getUserActivity(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserActivity(id));
    }

    private Long extractUserId(Jwt jwt) {
        Object userId = jwt.getClaim("userId");
        if (userId instanceof Number) {
            return ((Number) userId).longValue();
        }
        throw new RuntimeException("userId claim nije pronadjen u tokenu");
    }
}