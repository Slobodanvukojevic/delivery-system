package com.delivery.notification_service.controller;

import com.delivery.notification_service.dto.NotificationResponse;
import com.delivery.notification_service.dto.RegisterTokenRequest;
import com.delivery.notification_service.dto.SendNotificationRequest;
import com.delivery.notification_service.entity.NotificationLog;
import com.delivery.notification_service.entity.PushToken;
import com.delivery.notification_service.service.EmailService;
import com.delivery.notification_service.service.NotificationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/send")
    public ResponseEntity<NotificationResponse> sendNotification(
            @RequestBody SendNotificationRequest request) {
        return ResponseEntity.ok(notificationService.sendNotification(request));
    }

    @PostMapping("/send-bulk")
    public ResponseEntity<Map<String, String>> sendBulk(
            @RequestBody Map<String, Object> body) {
        @SuppressWarnings("unchecked")
        List<Integer> ids = (List<Integer>) body.get("userIds");
        List<Long> userIds = ids.stream().map(Integer::longValue).toList();
        String title = (String) body.getOrDefault("title", "Obavestenje");
        String message = (String) body.getOrDefault("message", "");

        notificationService.sendBulk(userIds, title, message);
        return ResponseEntity.ok(Map.of("message", "Bulk notifikacije poslate"));
    }

    @PostMapping("/tokens")
    public ResponseEntity<PushToken> registerToken(@Valid @RequestBody RegisterTokenRequest request) {
        PushToken saved = notificationService.registerToken(request);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NotificationLog>> getUserNotifications(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.getUserNotifications(userId));
    }

}