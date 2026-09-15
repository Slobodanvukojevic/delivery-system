package com.delivery.payment_service.config;

import com.delivery.payment_service.context.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class UserContextInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String userId = request.getHeader("X-User-Id");
        String role = request.getHeader("X-User-Role");
        String branchId = request.getHeader("X-Branch-Id");
        String email = request.getHeader("X-User-Email");

        if (userId != null && !userId.isEmpty()) {
            try { UserContext.setUserId(Long.parseLong(userId)); } catch (Exception ignored) {}
        }
        if (role != null && !role.isEmpty()) {
            UserContext.setRole(role);
        }
        if (branchId != null && !branchId.isEmpty()) {
            try { UserContext.setBranchId(Long.parseLong(branchId)); } catch (Exception ignored) {}
        }
        if (email != null && !email.isEmpty()) {
            UserContext.setEmail(email);
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        UserContext.clear();
    }
}