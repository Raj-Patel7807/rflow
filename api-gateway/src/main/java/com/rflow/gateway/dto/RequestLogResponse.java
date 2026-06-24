package com.rflow.gateway.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Response payload containing request logs.
 */
@Data
@Builder
public class RequestLogResponse {

    private Long id;

    private Long tenantId;

    private Long serviceId;

    private String serviceName;

    private String requestMethod;

    private String requestPath;

    private String queryString;

    private String clientIp;

    private Integer responseStatus;

    private Integer responseTimeMs;

    private String userAgent;

    private String errorMessage;

    private LocalDateTime createdAt;
}
