package com.delivery.payment_service.exception;

import org.springframework.http.HttpStatus;

public class PromoCodeExhaustedException extends BaseException {
    public PromoCodeExhaustedException(String message) {
        super(message, HttpStatus.CONFLICT, "PROMO_CODE_EXHAUSTED");
    }
}