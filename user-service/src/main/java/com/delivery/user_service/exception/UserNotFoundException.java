package com.delivery.user_service.exception;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends BaseException {
    public UserNotFoundException(Long id) {
        super("Korisnik sa ID " + id + " nije pronadjen", HttpStatus.NOT_FOUND, "USER_NOT_FOUND");
    }

    public UserNotFoundException(String email) {
        super("Korisnik sa email-om " + email + " nije pronadjen", HttpStatus.NOT_FOUND, "USER_NOT_FOUND");
    }
}