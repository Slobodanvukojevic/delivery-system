package com.delivery.delivery_service.exception;

import org.springframework.http.HttpStatus;

public class LockerNotFoundException extends BaseException {
    public LockerNotFoundException(Long id) {
        super("Paketomat sa ID " + id + " nije pronadjen", HttpStatus.NOT_FOUND, "LOCKER_NOT_FOUND");
    }
}