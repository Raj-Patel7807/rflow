package com.rflow.gateway.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {

    private Long userId;

    private Long tenantId;

    private String fullName;

    private String email;

    private String role;

    private String tenantSlug;

    private String tenantName;
}
