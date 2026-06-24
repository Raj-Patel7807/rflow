package com.rflow.gateway.dto;

import lombok.Builder;
import lombok.Data;

/**
 * Response payload containing a created or updated service.
 */
@Data
@Builder
public class ServiceResponse {

    private Long id;

    private Long tenantId;

    private String serviceName;

    private String serviceSlug;

    private String routePrefix;

    private String targetUrl;

    private String description;

    private String status;

    private String healthCheckPath;

    private Integer requestTimeoutMs;
}
