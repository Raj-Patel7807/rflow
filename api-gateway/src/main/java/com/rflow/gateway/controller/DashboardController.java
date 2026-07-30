package com.rflow.gateway.controller;

import com.rflow.gateway.dto.DashboardStatsResponse;
import com.rflow.gateway.dto.RequestLogResponse;
import com.rflow.gateway.dto.SystemStatsResponse;
import com.rflow.gateway.service.AuthorizationService;
import com.rflow.gateway.service.DashboardService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Dashboard APIs for monitoring gateway activity, traffic statistics, and service performance.
 */
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;
    private final AuthorizationService authorizationService;

    /**
     * Just a testing purpose route.
     */
    @GetMapping
    public ResponseEntity<?> adminDashboard(HttpSession session) {

        authorizationService.requireRole(session, "SUPER_ADMIN");

        return ResponseEntity.ok("Welcome Super Admin");
    }

    /**
     * Returns overall system stats.
     */
    @GetMapping("/system")
    public ResponseEntity<SystemStatsResponse> systemStats(HttpSession session) {

        authorizationService.requireRoles(session, "SUPER_ADMIN", "TENANT_ADMIN", "DEVELOPER");

        return ResponseEntity.ok(dashboardService.systemStats());
    }

    /**
     * Returns dashboard metrics for the current tenant.
     */
    @GetMapping("/stats")
    public ResponseEntity<DashboardStatsResponse> stats(HttpSession session) {

        authorizationService.requireRoles(session, "SUPER_ADMIN", "TENANT_ADMIN", "DEVELOPER");

        Long tenantId = authorizationService.resolveTenantId(session);

        return ResponseEntity.ok(dashboardService.stats(tenantId));
    }

    /**
     * Returns the most frequently used services.
     */
    @GetMapping("/top-services")
    public ResponseEntity<?> topServices(HttpSession session) {

        authorizationService.requireRoles(session, "SUPER_ADMIN", "TENANT_ADMIN", "DEVELOPER");

        Long tenantId = authorizationService.resolveTenantId(session);

        return ResponseEntity.ok(dashboardService.topServices(tenantId));
    }

    /**
     * Returns services with the highest response times.
     */
    @GetMapping("/slow-services")
    public ResponseEntity<?> slowServices(HttpSession session) {

        authorizationService.requireRoles(session, "SUPER_ADMIN", "TENANT_ADMIN", "DEVELOPER");

        Long tenantId = authorizationService.resolveTenantId(session);

        return ResponseEntity.ok(dashboardService.slowServices(tenantId));
    }

    /**
     * Returns request volume data for dashboard charts.
     */
    @GetMapping("/request-chart")
    public ResponseEntity<?> requestChart(HttpSession session) {

        authorizationService.requireRoles(session, "SUPER_ADMIN", "TENANT_ADMIN", "DEVELOPER");

        Long tenantId = authorizationService.resolveTenantId(session);

        return ResponseEntity.ok(dashboardService.requestChart(tenantId));
    }

    /**
     * Returns recent API request logs.
     */
    @GetMapping("/recent-requests")
    public ResponseEntity<List<RequestLogResponse>> recentRequests(HttpSession session,
                                                                   @RequestParam(defaultValue = "10") int limit) {

        authorizationService.requireRoles(session, "SUPER_ADMIN", "TENANT_ADMIN", "DEVELOPER");

        Long tenantId = authorizationService.resolveTenantId(session);

        return ResponseEntity.ok(dashboardService.recentRequests(tenantId, limit));
    }
}
