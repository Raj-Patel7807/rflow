package com.rflow.gateway.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class HealthLogResponse {

    private Long id;

    private Long serviceId;

    private String serviceName;

    private String healthStatus;

    private Integer responseTimeMs;

    private LocalDateTime checkedAt;
}
