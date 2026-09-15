package com.delivery.notification_service.service;

import com.delivery.notification_service.dto.NotificationResponse;
import com.delivery.notification_service.dto.RegisterTokenRequest;
import com.delivery.notification_service.dto.SendNotificationRequest;
import com.delivery.notification_service.entity.Channel;
import com.delivery.notification_service.entity.NotificationLog;
import com.delivery.notification_service.entity.NotificationStatus;
import com.delivery.notification_service.entity.PushToken;
import com.delivery.notification_service.repository.NotificationLogRepository;
import com.delivery.notification_service.repository.PushTokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final EmailService emailService;
    private final PushService pushService;
    private final NotificationLogRepository logRepository;
    private final PushTokenRepository tokenRepository;

    public NotificationService(EmailService emailService,
                               PushService pushService,
                               NotificationLogRepository logRepository,
                               PushTokenRepository tokenRepository) {
        this.emailService = emailService;
        this.pushService = pushService;
        this.logRepository = logRepository;
        this.tokenRepository = tokenRepository;
    }

    public NotificationResponse sendNotification(SendNotificationRequest request) {
        String language = detectLanguage(request.getPhoneLocale());

        // Za sada, saljemo samo push (email zahteva email adresu korisnika, koju nemamo u requestu)
        // U pravoj app, dobavili bismo email iz User Service-a preko Feign-a.

        String title = buildTitle(request.getType());
        String body = buildBody(request, language);

        boolean pushSent = pushService.sendPush(request.getUserId(), title, body);

        // Logujemo notifikaciju
        NotificationLog notifLog = NotificationLog.builder()
                .userId(request.getUserId())
                .orderId(request.getOrderId())
                .type(request.getType())
                .channel(Channel.PUSH)
                .status(pushSent ? NotificationStatus.SENT : NotificationStatus.FAILED)
                .message(title + " - " + body)
                .build();
        NotificationLog saved = logRepository.save(notifLog);

        log.info("Notifikacija poslata korisniku {} za order {}", request.getUserId(), request.getOrderId());

        return NotificationResponse.builder()
                .notificationId(saved.getId())
                .emailSent(false)
                .pushSent(pushSent)
                .message("Notifikacija obradjena")
                .build();
    }

    public void sendEmailNotification(Long userId, String email, String code, String language, Map<String, String> vars) {
        try {
            String subject = emailService.getSubject(code, language);
            String body = emailService.renderTemplate(code, language, vars);
            emailService.sendEmail(email, subject, body);

            NotificationLog notifLog = NotificationLog.builder()
                    .userId(userId)
                    .type(com.delivery.notification_service.entity.NotificationType.ORDER_STATUS_CHANGED)
                    .channel(Channel.EMAIL)
                    .status(NotificationStatus.SENT)
                    .message(subject)
                    .build();
            logRepository.save(notifLog);
        } catch (Exception e) {
            log.error("Greska pri slanju email-a: {}", e.getMessage());
        }
    }

    public PushToken registerToken(RegisterTokenRequest request) {
        // Ako vec postoji, azuriraj
        tokenRepository.findByUserId(request.getUserId()).ifPresent(existing -> {
            tokenRepository.delete(existing);
        });

        PushToken token = PushToken.builder()
                .userId(request.getUserId())
                .fcmToken(request.getFcmToken())
                .deviceType(request.getDeviceType())
                .build();

        return tokenRepository.save(token);
    }

    public List<NotificationLog> getUserNotifications(Long userId) {
        return logRepository.findByUserIdOrderBySentAtDesc(userId);
    }

    public void sendBulk(List<Long> userIds, String title, String body) {
        for (Long userId : userIds) {
            pushService.sendPush(userId, title, body);
        }
    }

    private String detectLanguage(String phoneLocale) {
        if (phoneLocale == null) return "en";
        String loc = phoneLocale.toLowerCase();
        if (loc.startsWith("sr") || loc.startsWith("hr") || loc.startsWith("bs")) {
            return "sr";
        }
        return "en";
    }

    private String buildTitle(com.delivery.notification_service.entity.NotificationType type) {
        return switch (type) {
            case ORDER_CREATED -> "Porudzbina primljena";
            case LOCKER_ASSIGNED -> "Paket u paketomatu";
            case READY_FOR_PICKUP -> "Stigao u poslovnicu";
            case DELIVERED -> "Paket dostavljen";
            case RETURN_TO_SENDER -> "Paket se vraca";
            case ORDER_STATUS_CHANGED -> "Status porudzbine promenjen";
        };
    }

    private String buildBody(SendNotificationRequest request, String language) {
        if (request.getCustomMessage() != null) {
            return request.getCustomMessage();
        }
        if (request.getPickupCode() != null) {
            return language.equals("sr") ? "Kod: " + request.getPickupCode() : "Code: " + request.getPickupCode();
        }
        return language.equals("sr") ? "Imate novu notifikaciju" : "You have a new notification";
    }
}