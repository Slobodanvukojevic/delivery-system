package com.delivery.user_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "Email je obavezan")
    @Email
    private String email;

    @NotBlank(message = "Lozinka je obavezna")
    private String password;
}