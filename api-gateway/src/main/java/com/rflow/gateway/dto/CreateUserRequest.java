package com.rflow.gateway.dto;

import lombok.Data;

/**
 * Request payload for creating a user.
 */
@Data
public class CreateUserRequest {

    private String fullName;

    private String email;

    private String password;

    private String role;

    private Boolean isActive;

    private Long tenantId;
}
