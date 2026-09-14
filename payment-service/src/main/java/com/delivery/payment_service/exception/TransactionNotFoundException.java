package com.delivery.payment_service.exception;

import org.springframework.http.HttpStatus;

public class TransactionNotFoundException extends BaseException {
    public TransactionNotFoundException(Long id) {
        super("Transakcija sa ID " + id + " nije pronadjena", HttpStatus.NOT_FOUND, "TRANSACTION_NOT_FOUND");
    }
}