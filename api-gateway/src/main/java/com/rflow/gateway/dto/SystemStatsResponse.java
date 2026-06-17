package com.rflow.gateway.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SystemStatsResponse {

    private long totalTenants;

    private long activeTenants;

    private long totalServices;

    private long totalUsers;

    private long totalRequests;
}
