package com.rflow.gateway.dto;

import lombok.Data;

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
