package com.delivery.delivery_service.exception;

import org.springframework.http.HttpStatus;

public class InvalidOrderStatusException extends BaseException {
    public InvalidOrderStatusException(String message) {
        super(message, HttpStatus.BAD_REQUEST, "INVALID_ORDER_STATUS");
    }
}