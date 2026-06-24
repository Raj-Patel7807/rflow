package com.rflow.gateway.dto;

import lombok.Builder;
import lombok.Data;

/**
 * Response payload containing System stats.
 */
@Data
@Builder
public class SystemStatsResponse {

    private long totalTenants;

    private long activeTenants;

    private long totalServices;

    private long totalUsers;

    private long totalRequests;
}
