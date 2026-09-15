package com.delivery.notification_service.service;

import com.delivery.notification_service.entity.PushToken;
import com.delivery.notification_service.repository.PushTokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PushService {

    private static final Logger log = LoggerFactory.getLogger(PushService.class);

    private final PushTokenRepository tokenRepository;

    public PushService(PushTokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    public boolean sendPush(Long userId, String title, String body) {
        Optional<PushToken> tokenOpt = tokenRepository.findByUserId(userId);
        if (tokenOpt.isEmpty()) {
            log.warn("Korisnik {} nema FCM token. Push se preskace.", userId);
            return false;
        }


        String fcmToken = tokenOpt.get().getFcmToken();
        log.info("========== PUSH NOTIFIKACIJA ==========");
        log.info("User ID: {}", userId);
        log.info("FCM Token: {}", fcmToken.substring(0, Math.min(20, fcmToken.length())) + "...");
        log.info("Title: {}", title);
        log.info("Body: {}", body);
        log.info("========================================");

        return true;
    }
}