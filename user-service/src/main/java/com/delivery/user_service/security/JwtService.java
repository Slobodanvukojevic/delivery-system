package com.delivery.user_service.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class JwtService {

    private final JwtEncoder jwtEncoder;
    private final long expirationMs;

    public JwtService(JwtEncoder jwtEncoder, @Value("${jwt.expiration-ms}") long expirationMs) {
        this.jwtEncoder = jwtEncoder;
        this.expirationMs = expirationMs;
    }

    public String generateToken(Long userId, String email, String role, Long branchId) {
        Instant now = Instant.now();
        JwtClaimsSet.Builder builder = JwtClaimsSet.builder()
                .issuer("user-service")
                .issuedAt(now)
                .expiresAt(now.plus(expirationMs, ChronoUnit.MILLIS))
                .subject(email)
                .claim("userId", userId)
                .claim("role", role);

        // Dodaj branchId samo ako nije null
        if (branchId != null) {
            builder.claim("branchId", branchId);
        }

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(header, builder.build())).getTokenValue();
    }
}