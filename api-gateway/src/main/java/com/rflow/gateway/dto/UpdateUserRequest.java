package com.rflow.gateway.dto;

import lombok.Data;

@Data
public class UpdateUserRequest {

    private String fullName;

    private String role;

    private Boolean isActive;
}
