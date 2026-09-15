package com.delivery.notification_service.feign;

import lombok.Data;

@Data
public class UserResponse {
    private Long id;
    private String email;
    private String fullName;
    private String phone;
    private String role;
    private Long branchId;
    private Boolean active;
}