package com.rflow.gateway.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardStatsResponse {

    private long totalRequests;

    private long successRequests;

    private long failedRequests;

    private long activeServices;

    private long activeTenants;

    private long rateLimitedRequests;
}
