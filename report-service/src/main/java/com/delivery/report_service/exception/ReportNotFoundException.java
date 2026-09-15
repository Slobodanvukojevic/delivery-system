package com.delivery.report_service.exception;

import org.springframework.http.HttpStatus;

public class ReportNotFoundException extends BaseException {
    public ReportNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND, "REPORT_NOT_FOUND");
    }
}