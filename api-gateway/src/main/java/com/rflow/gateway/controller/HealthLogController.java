package com.rflow.gateway.controller;

import com.rflow.gateway.dto.PagedHealthLogResponse;
import com.rflow.gateway.service.AuthorizationService;
import com.rflow.gateway.service.HealthLogService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/health-logs")
@RequiredArgsConstructor
public class HealthLogController {

    private final AuthorizationService authorizationService;
    private final HealthLogService healthLogService;

    @GetMapping
    public ResponseEntity<PagedHealthLogResponse> getLogs(HttpSession session, @RequestParam(required = false) Long serviceId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {

        authorizationService.requireRole(session, "SUPER_ADMIN");

        Long tenantId = authorizationService.resolveTenantId(session);

        return ResponseEntity.ok(healthLogService.getHealthLogs(tenantId, serviceId, page, size));
    }

    @PostMapping("/check")
    public ResponseEntity<?> checkService(HttpSession session, @RequestParam Long serviceId) {

        authorizationService.requireRole(session, "SUPER_ADMIN");

        Long tenantId = authorizationService.resolveTenantId(session);

        return ResponseEntity.ok(healthLogService.checkService(tenantId, serviceId));
    }

    @PostMapping("/check-all")
    public ResponseEntity<?> checkAllServices(HttpSession session) {

        authorizationService.requireRole(session, "SUPER_ADMIN");

        Long tenantId = authorizationService.resolveTenantId(session);

        return ResponseEntity.ok(healthLogService.checkAllServices(tenantId));
    }
}
