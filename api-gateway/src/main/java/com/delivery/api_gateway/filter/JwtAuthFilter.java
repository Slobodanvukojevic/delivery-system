package com.delivery.api_gateway.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthFilter.class);

    private final JwtDecoder jwtDecoder;
    private final List<String> publicPaths;

    public JwtAuthFilter(JwtDecoder jwtDecoder,
                         @Value("${jwt.public-paths}") String publicPathsCsv) {
        this.jwtDecoder = jwtDecoder;
        this.publicPaths = Arrays.asList(publicPathsCsv.split(","));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        String path = request.getRequestURI();

        // Ako je putanja javna, samo prosledi dalje
        if (isPublicPath(path)) {
            chain.doFilter(request, response);
            return;
        }

        // Inace, trazi JWT
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Zahtev bez Authorization header-a: {}", path);
            sendUnauthorized(response, "NO_TOKEN", "Authorization header je obavezan");
            return;
        }

        String token = authHeader.substring(7);

        try {
            Jwt jwt = jwtDecoder.decode(token);

            Long userId = extractLongClaim(jwt, "userId");
            String role = jwt.getClaimAsString("role");
            Long branchId = extractLongClaim(jwt, "branchId");
            String email = jwt.getSubject();

            log.debug("Token validan. userId={}, role={}, branchId={}", userId, role, branchId);

            // Umotaj request da bi se dodali header-i
            HttpServletRequestWrapper wrapper = new HttpServletRequestWrapper(request) {
                @Override
                public String getHeader(String name) {
                    if ("X-User-Id".equalsIgnoreCase(name)) return userId != null ? userId.toString() : "";
                    if ("X-User-Role".equalsIgnoreCase(name)) return role != null ? role : "";
                    if ("X-Branch-Id".equalsIgnoreCase(name)) return branchId != null ? branchId.toString() : "";
                    if ("X-User-Email".equalsIgnoreCase(name)) return email != null ? email : "";
                    return super.getHeader(name);
                }
            };

            chain.doFilter(wrapper, response);

        } catch (Exception e) {
            log.warn("Nevalidan JWT token: {}", e.getMessage());
            sendUnauthorized(response, "INVALID_TOKEN", "Token nije validan ili je istekao");
        }
    }

    private boolean isPublicPath(String path) {
        for (String pattern : publicPaths) {
            String trimmed = pattern.trim();
            // Podrska za ** wildcard
            String prefix = trimmed.replace("/**", "");
            if (path.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

    private Long extractLongClaim(Jwt jwt, String claimName) {
        Object value = jwt.getClaim(claimName);
        if (value == null) return null;
        if (value instanceof Number) return ((Number) value).longValue();
        return null;
    }

    private void sendUnauthorized(HttpServletResponse response, String error, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write(
                "{\"error\":\"" + error + "\",\"message\":\"" + message + "\"}"
        );
    }

    // Ne filtriraj OPTIONS zahteve (CORS preflight)
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return "OPTIONS".equalsIgnoreCase(request.getMethod());
    }
}