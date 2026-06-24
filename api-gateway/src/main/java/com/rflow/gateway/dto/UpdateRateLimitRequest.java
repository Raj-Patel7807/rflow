package com.rflow.gateway.dto;

import lombok.Data;

/**
 * Request payload for updating a rate limit policy.
 */
@Data
public class UpdateRateLimitRequest {

    private Integer requestsLimit;

    private Integer windowSeconds;

    private Boolean isActive;
}
