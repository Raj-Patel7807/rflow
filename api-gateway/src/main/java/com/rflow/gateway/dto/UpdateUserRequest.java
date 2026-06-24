package com.rflow.gateway.dto;

import lombok.Data;

/**
 * Request payload for updating a user.
 */
@Data
public class UpdateUserRequest {

    private String fullName;

    private String role;

    private Boolean isActive;
}
