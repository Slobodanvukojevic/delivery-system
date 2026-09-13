package com.delivery.delivery_service.exception;

import org.springframework.http.HttpStatus;

public class InvalidPickupCodeException extends BaseException {
    public InvalidPickupCodeException(String message) {
        super(message, HttpStatus.BAD_REQUEST, "INVALID_PICKUP_CODE");
    }
}