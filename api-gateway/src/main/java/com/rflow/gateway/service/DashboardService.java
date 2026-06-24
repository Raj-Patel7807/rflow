package com.rflow.gateway.service;

import com.rflow.gateway.dto.DashboardStatsResponse;
import com.rflow.gateway.dto.RequestLogResponse;
import com.rflow.gateway.dto.SystemStatsResponse;
import com.rflow.gateway.repository.RequestLogRepository;
import com.rflow.gateway.repository.ServiceRepository;
import com.rflow.gateway.repository.TenantRepository;
import com.rflow.gateway.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Provides dashboard statistics and analytics for the gateway.
 */
@Service
@RequiredArgsConstructor
public class DashboardService {

    private final RequestLogRepository requestLogRepository;
    private final ServiceRepository serviceRepository;
    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final RequestLogService requestLogService;

    /**
     * Returns overall system statistics across all tenants.
     */
    public SystemStatsResponse systemStats() {

        return SystemStatsResponse.builder()
                                  .totalTenants(tenantRepository.count())
                                  .activeTenants(tenantRepository.countByStatus("ACTIVE"))
                                  .totalServices(serviceRepository.count())
                                  .totalUsers(userRepository.count())
                                  .totalRequests(requestLogRepository.count())
                                  .build();
    }

    /**
     * Returns dashboard statistics for a specific tenant.
     */
    public DashboardStatsResponse stats(Long tenantId) {

        long total = requestLogRepository.countByTenantId(tenantId);

        long success = requestLogRepository.countByTenantIdAndResponseStatusBetween(tenantId, 200, 299);

        long failed = requestLogRepository.countByTenantIdAndResponseStatusBetween(tenantId, 400, 599);

        long activeServices = serviceRepository.countByTenantIdAndStatus(tenantId, "ACTIVE");

        long activeTenants = tenantRepository.countByStatus("ACTIVE");

        long rateLimited = requestLogRepository.countByTenantIdAndResponseStatus(tenantId, 429);

        Double avgResponse = requestLogRepository.avgResponseTime(tenantId);

        return DashboardStatsResponse.builder()
                                     .totalRequests(total)
                                     .successRequests(success)
                                     .failedRequests(failed)
                                     .activeServices(activeServices)
                                     .activeTenants(activeTenants)
                                     .rateLimitedRequests(rateLimited)
                                     .blockedRequests(rateLimited)
                                     .avgResponseTimeMs(avgResponse == null ? 0 : Math.round(avgResponse * 10.0) / 10.0)
                                     .build();
    }

    /**
     * Returns the most recent requests for a tenant.
     */
    public List<RequestLogResponse> recentRequests(Long tenantId, int limit) {

        return requestLogService.getRecent(tenantId, limit);
    }

    /**
     * Returns services with the highest request traffic.
     */
    public List<Object[]> topServices(Long tenantId) {

        return requestLogRepository.topServices(tenantId);
    }

    /**
     * Returns services with the highest average response time.
     */
    public List<Object[]> slowServices(Long tenantId) {

        return requestLogRepository.slowServices(tenantId);
    }

    /**
     * Returns request count data for dashboard charts.
     */
    public List<Object[]> requestChart(Long tenantId) {

        return requestLogRepository.requestChart(tenantId);
    }
}
