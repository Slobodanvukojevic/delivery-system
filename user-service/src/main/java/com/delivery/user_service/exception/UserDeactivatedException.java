package com.delivery.user_service.exception;

import org.springframework.http.HttpStatus;

public class UserDeactivatedException extends BaseException {
    public UserDeactivatedException() {
        super("Nalog je deaktiviran", HttpStatus.FORBIDDEN, "USER_DEACTIVATED");
    }
}