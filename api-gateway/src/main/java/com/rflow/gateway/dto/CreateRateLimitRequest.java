package com.rflow.gateway.dto;

import lombok.Data;

@Data
public class CreateRateLimitRequest {

    private Long serviceId;

    private Integer requestsLimit;

    private Integer windowSeconds;

    private String strategy;
}
