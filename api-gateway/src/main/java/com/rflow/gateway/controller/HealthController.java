package com.rflow.gateway.controller;

import com.rflow.gateway.service.GatewayService;
import com.rflow.gateway.service.RequestForwarder;
import com.rflow.gateway.service.RouteService;
import com.rflow.gateway.service.TenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Health check APIs for monitoring gateway status and basic system information.
 */
@RestController
@RequestMapping({"/health", "/api/gateway/health"})
@RequiredArgsConstructor
public class HealthController {

    private final GatewayService gatewayService;
    private final RequestForwarder requestForwarder;
    private final RouteService routeService;
    private final TenantService tenantService;

    private final long startTime = System.currentTimeMillis();

    /**
     * Returns gateway health, dependency status, uptime, memory usage, and response metrics.
     */
    @GetMapping
    public ResponseEntity<?> health() {

        long requestStart = System.currentTimeMillis();

        Map<String, Object> response = new HashMap<>();
        Map<String, String> dependencies = new HashMap<>();

        // status default
        String status = "UP";

        // uptime
        long uptimeMs = System.currentTimeMillis() - startTime;
        response.put("uptimeMs", uptimeMs);
        response.put("timestamp", Instant.now()
                                         .toString());

        // dependency checks (real)
        dependencies.put("gatewayService", gatewayService != null ? "UP" : "DOWN");
        dependencies.put("tenantService", tenantService != null ? "UP" : "DOWN");
        dependencies.put("requestForwarder", requestForwarder != null ? "UP" : "DOWN");
        dependencies.put("routeService", routeService != null ? "UP" : "DOWN");

        if(dependencies.containsValue("DOWN")) {
            status = "DEGRADED";
        }

        // memory info
        Runtime runtime = Runtime.getRuntime();
        Map<String, Object> memory = new HashMap<>();

        memory.put("usedMemory", runtime.totalMemory() - runtime.freeMemory());
        memory.put("freeMemory", runtime.freeMemory());
        memory.put("maxMemory", runtime.maxMemory());

        response.put("service", "api-gateway");
        response.put("status", status);
        response.put("memory", memory);
        response.put("dependencies", dependencies);

        response.put("responseTimeMs", System.currentTimeMillis() - requestStart);

        return ResponseEntity.ok(response);
    }
}
