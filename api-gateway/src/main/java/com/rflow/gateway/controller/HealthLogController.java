package com.rflow.gateway.controller;

import com.rflow.gateway.dto.PagedHealthLogResponse;
import com.rflow.gateway.service.AuthorizationService;
import com.rflow.gateway.service.HealthLogService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * APIs for viewing health check logs and monitoring backend service availability.
 */
@RestController
@RequestMapping("/api/health-logs")
@RequiredArgsConstructor
public class HealthLogController {

    private final AuthorizationService authorizationService;
    private final HealthLogService healthLogService;

    /**
     * Returns health check logs for services.
     */
    @GetMapping
    public ResponseEntity<PagedHealthLogResponse> getLogs(HttpSession session,
                                                          @RequestParam(required = false) Long serviceId,
                                                          @RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "20") int size) {

        authorizationService.requireRoles(session, "SUPER_ADMIN", "TENANT_ADMIN", "DEVELOPER");

        Long tenantId = authorizationService.resolveTenantId(session);

        return ResponseEntity.ok(healthLogService.getHealthLogs(tenantId, serviceId, page, size));
    }

    /**
     * Runs a health check for a specific service.
     */
    @PostMapping("/check")
    public ResponseEntity<?> checkService(HttpSession session, @RequestParam Long serviceId) {

        authorizationService.requireRoles(session, "SUPER_ADMIN", "TENANT_ADMIN");

        Long tenantId = authorizationService.resolveTenantId(session);

        return ResponseEntity.ok(healthLogService.checkService(tenantId, serviceId));
    }

    /**
     * Runs health checks for all services.
     */
    @PostMapping("/check-all")
    public ResponseEntity<?> checkAllServices(HttpSession session) {

        authorizationService.requireRoles(session, "SUPER_ADMIN", "TENANT_ADMIN");

        Long tenantId = authorizationService.resolveTenantId(session);

        return ResponseEntity.ok(healthLogService.checkAllServices(tenantId));
    }
}
