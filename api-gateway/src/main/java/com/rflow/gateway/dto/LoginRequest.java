package com.rflow.gateway.dto;

import lombok.Data;

/**
 * Request payload for user auth.
 */
@Data
public class LoginRequest {

    private String email;

    private String password;
}
