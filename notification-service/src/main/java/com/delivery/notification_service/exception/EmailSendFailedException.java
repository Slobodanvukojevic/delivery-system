package com.delivery.notification_service.exception;

import org.springframework.http.HttpStatus;

public class EmailSendFailedException extends BaseException {
    public EmailSendFailedException(String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR, "EMAIL_SEND_FAILED");
    }
}