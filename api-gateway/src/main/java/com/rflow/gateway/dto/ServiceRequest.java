package com.rflow.gateway.dto;

import lombok.Data;

/**
 * Request payload for creating and updating a backend service.
 */
@Data
public class ServiceRequest {

    private String serviceName;

    private String serviceSlug;

    private String routePrefix;

    private String targetUrl;

    private String description;

    private String status;

    private String healthCheckPath;

    private Integer requestTimeoutMs;
}
