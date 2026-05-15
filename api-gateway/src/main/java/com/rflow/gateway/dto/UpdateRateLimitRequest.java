package com.rflow.gateway.dto;

import lombok.Data;

@Data
public class UpdateRateLimitRequest {

    private Integer requestsLimit;

    private Integer windowSeconds;

    private Boolean isActive;
}
