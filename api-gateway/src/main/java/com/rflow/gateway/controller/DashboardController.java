package com.rflow.gateway.controller;

import com.rflow.gateway.dto.DashboardStatsResponse;
import com.rflow.gateway.service.AuthorizationService;
import com.rflow.gateway.service.DashboardService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;
    private final AuthorizationService authorizationService;

    @GetMapping
    public ResponseEntity<?> adminDashboard(HttpSession session) {

        authorizationService.requireRole(session, "SUPER_ADMIN");

        return ResponseEntity.ok("Welcome Super Admin");
    }

    @GetMapping("/stats")
    public ResponseEntity<DashboardStatsResponse> stats(HttpSession session) {

        authorizationService.requireLogin(session);

        Long tenantId = (Long) session.getAttribute("tenantId");

        return ResponseEntity.ok(dashboardService.stats(tenantId));
    }

    @GetMapping("/top-services")
    public ResponseEntity<?> topServices(HttpSession session) {

        authorizationService.requireLogin(session);

        Long tenantId = (Long) session.getAttribute("tenantId");

        return ResponseEntity.ok(dashboardService.topServices(tenantId));
    }

    @GetMapping("/slow-services")
    public ResponseEntity<?> slowServices(HttpSession session) {

        authorizationService.requireLogin(session);

        Long tenantId = (Long) session.getAttribute("tenantId");

        return ResponseEntity.ok(dashboardService.slowServices(tenantId));
    }

    @GetMapping("/request-chart")
    public ResponseEntity<?> requestChart(HttpSession session) {

        authorizationService.requireLogin(session);

        Long tenantId = (Long) session.getAttribute("tenantId");

        return ResponseEntity.ok(dashboardService.requestChart(tenantId));
    }
}
