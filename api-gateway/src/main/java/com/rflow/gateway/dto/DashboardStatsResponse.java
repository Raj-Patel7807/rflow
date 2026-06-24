package com.rflow.gateway.dto;

import lombok.Builder;
import lombok.Data;

/**
 * Response payload containing Dashboard stats.
 */
@Data
@Builder
public class DashboardStatsResponse {

    private long totalRequests;

    private long successRequests;

    private long failedRequests;

    private long activeServices;

    private long activeTenants;

    private long rateLimitedRequests;

    private long blockedRequests;

    private double avgResponseTimeMs;
}
