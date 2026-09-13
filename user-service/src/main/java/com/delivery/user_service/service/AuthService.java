package com.delivery.user_service.service;

import com.delivery.user_service.dto.AuthResponse;
import com.delivery.user_service.dto.LoginRequest;
import com.delivery.user_service.dto.RegisterRequest;
import com.delivery.user_service.entity.ActivityType;
import com.delivery.user_service.entity.User;
import com.delivery.user_service.entity.UserActivityLog;
import com.delivery.user_service.exception.InvalidCredentialsException;
import com.delivery.user_service.exception.UserAlreadyExistsException;
import com.delivery.user_service.exception.UserDeactivatedException;
import com.delivery.user_service.repository.UserActivityLogRepository;
import com.delivery.user_service.repository.UserRepository;
import com.delivery.user_service.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final UserActivityLogRepository activityLogRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       UserActivityLogRepository activityLogRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.activityLogRepository = activityLogRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Korisnik sa email-om " + request.getEmail() + " vec postoji");
        }
        if (userRepository.existsByPhone(request.getPhone())) {
            throw new UserAlreadyExistsException("Korisnik sa telefonom " + request.getPhone() + " vec postoji");
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

        logActivity(saved.getId(), ActivityType.REGISTER, null);

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

    public AuthResponse login(LoginRequest request, String ipAddress) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Pogresan email ili lozinka"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Pogresan email ili lozinka");
        }

        if (!user.getActive()) {
            throw new UserDeactivatedException();
        }

        logActivity(user.getId(), ActivityType.LOGIN, ipAddress);

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

    private void logActivity(Long userId, ActivityType type, String ipAddress) {
        UserActivityLog log = UserActivityLog.builder()
                .userId(userId)
                .activityType(type)
                .ipAddress(ipAddress)
                .build();
        activityLogRepository.save(log);
    }
}