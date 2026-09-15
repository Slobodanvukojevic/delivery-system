package com.delivery.notification_service.service;

import com.delivery.notification_service.entity.EmailTemplate;
import com.delivery.notification_service.exception.EmailSendFailedException;
import com.delivery.notification_service.repository.EmailTemplateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;
    private final EmailTemplateRepository templateRepository;

    public EmailService(JavaMailSender mailSender, EmailTemplateRepository templateRepository) {
        this.mailSender = mailSender;
        this.templateRepository = templateRepository;
    }

    public void sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("noreply@delivery-system.com");
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            log.info("Email poslat na {}", to);
        } catch (Exception e) {
            log.error("Greska pri slanju email-a na {}: {}", to, e.getMessage());
            throw new EmailSendFailedException("Email nije poslat: " + e.getMessage());
        }
    }

    public String renderTemplate(String code, String language, Map<String, String> vars) {
        EmailTemplate template = templateRepository.findByCodeAndLanguage(code, language)
                .orElseGet(() -> templateRepository.findByCodeAndLanguage(code, "en")
                        .orElseThrow(() -> new RuntimeException("Template " + code + " ne postoji")));

        String body = template.getBody();
        for (Map.Entry<String, String> entry : vars.entrySet()) {
            body = body.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }
        return body;
    }

    public String getSubject(String code, String language) {
        return templateRepository.findByCodeAndLanguage(code, language)
                .orElseGet(() -> templateRepository.findByCodeAndLanguage(code, "en")
                        .orElseThrow(() -> new RuntimeException("Template " + code + " ne postoji")))
                .getSubject();
    }
}