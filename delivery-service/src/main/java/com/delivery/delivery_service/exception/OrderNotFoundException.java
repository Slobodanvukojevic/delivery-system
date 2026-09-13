package com.delivery.delivery_service.exception;

import org.springframework.http.HttpStatus;

public class OrderNotFoundException extends BaseException {
    public OrderNotFoundException(Long id) {
        super("Porudzbina sa ID " + id + " nije pronadjena", HttpStatus.NOT_FOUND, "ORDER_NOT_FOUND");
    }
}