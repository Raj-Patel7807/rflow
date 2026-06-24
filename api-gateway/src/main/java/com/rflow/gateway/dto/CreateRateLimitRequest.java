package com.rflow.gateway.dto;

import lombok.Data;

/**
 * Request payload for creating a rate limit policy.
 */
@Data
public class CreateRateLimitRequest {

    private Long serviceId;

    private Integer requestsLimit;

    private Integer windowSeconds;

    private String strategy;
}
