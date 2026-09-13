package com.delivery.delivery_service.exception;

import org.springframework.http.HttpStatus;

public class LockerFullException extends BaseException {
    public LockerFullException(Long lockerId) {
        super("Paketomat sa ID " + lockerId + " je pun", HttpStatus.CONFLICT, "LOCKER_FULL");
    }

    public LockerFullException(String message) {
        super(message, HttpStatus.CONFLICT, "LOCKER_FULL");
    }
}