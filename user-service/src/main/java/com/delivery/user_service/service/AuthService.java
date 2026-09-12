package com.delivery.user_service.service;

import com.delivery.user_service.dto.AuthResponse;
import com.delivery.user_service.dto.LoginRequest;
import com.delivery.user_service.dto.RegisterRequest;
import com.delivery.user_service.entity.User;
import com.delivery.user_service.repository.UserRepository;
import com.delivery.user_service.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Korisnik sa tim email-om vec postoji");
        }
        if (userRepository.existsByPhone(request.getPhone())) {
            throw new RuntimeException("Korisnik sa tim telefonom vec postoji");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .role(request.getRole())
                .branchId(request.getBranchId())
                .active(true)
                .build();

        User saved = userRepository.save(user);

        String token = jwtService.generateToken(
                saved.getId(),
                saved.getEmail(),
                saved.getRole().name(),
                saved.getBranchId()
        );

        return new AuthResponse(
                token,
                saved.getId(),
                saved.getFullName(),
                saved.getRole().name(),
                saved.getBranchId(),
                "Uspesno ste registrovani"
        );
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Pogresan email ili lozinka"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Pogresan email ili lozinka");
        }

        if (!user.getActive()) {
            throw new RuntimeException("Nalog je deaktiviran");
        }

        String token = jwtService.generateToken(
                user.getId(),
                user.getEmail(),
                user.getRole().name(),
                user.getBranchId()
        );

        return new AuthResponse(
                token,
                user.getId(),
                user.getFullName(),
                user.getRole().name(),
                user.getBranchId(),
                "Uspesno ste prijavljeni"
        );
    }
}