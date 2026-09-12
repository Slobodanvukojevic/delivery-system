package com.delivery.user_service.dto;

import com.delivery.user_service.entity.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "Email je obavezan")
    @Email(message = "Email nije validan")
    private String email;

    @NotBlank(message = "Lozinka je obavezna")
    @Size(min = 6, message = "Lozinka mora imati najmanje 6 karaktera")
    private String password;

    @NotBlank(message = "Ime i prezime su obavezni")
    private String fullName;

    @NotBlank(message = "Telefon je obavezan")
    private String phone;

    @NotNull(message = "Uloga je obavezna")
    private UserRole role;

    private Long branchId;
}