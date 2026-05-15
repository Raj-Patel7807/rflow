package com.rflow.gateway.controller;

import com.rflow.gateway.dto.CreateRateLimitRequest;
import com.rflow.gateway.dto.UpdateRateLimitRequest;
import com.rflow.gateway.model.BackendService;
import com.rflow.gateway.model.RateLimitPolicy;
import com.rflow.gateway.service.AuthorizationService;
import com.rflow.gateway.service.RateLimitService;
import com.rflow.gateway.service.RouteService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rate-limits")
@RequiredArgsConstructor
public class RateLimitController {

    private final RateLimitService rateLimitService;
    private final RouteService routeService;
    private final AuthorizationService authorizationService;

    @PostMapping
    public ResponseEntity<RateLimitPolicy> create(@RequestBody CreateRateLimitRequest request, HttpSession session) {

        authorizationService.requireRoles(session, "SUPER_ADMIN", "TENANT_ADMIN");

        BackendService backendService = routeService.findById(request.getServiceId());

        authorizationService.requireTenants(session, backendService.getTenantId());

        Long userId = (Long) session.getAttribute("userId");

        return ResponseEntity.ok(rateLimitService.create(request, userId));
    }

    @GetMapping("/service/{serviceId}")
    public ResponseEntity<List<RateLimitPolicy>> getPolicies(@PathVariable Long serviceId, HttpSession session) {

        authorizationService.requireLogin(session);

        BackendService backendService = routeService.findById(serviceId);

        authorizationService.requireTenants(session, backendService.getTenantId());

        return ResponseEntity.ok(rateLimitService.getServicePolicies(serviceId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RateLimitPolicy> update(@PathVariable Long id, @RequestBody UpdateRateLimitRequest request, HttpSession session) {

        authorizationService.requireRoles(session, "SUPER_ADMIN", "TENANT_ADMIN");

        RateLimitPolicy policy = rateLimitService.findById(id);

        BackendService backendService = routeService.findById(policy.getServiceId());

        authorizationService.requireTenants(session, backendService.getTenantId());

        return ResponseEntity.ok(rateLimitService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id, HttpSession session) {

        authorizationService.requireRoles(session, "SUPER_ADMIN", "TENANT_ADMIN");

        RateLimitPolicy policy = rateLimitService.findById(id);

        BackendService backendService = routeService.findById(policy.getServiceId());

        authorizationService.requireTenants(session, backendService.getTenantId());

        rateLimitService.delete(id);

        return ResponseEntity.ok("Policy Deleted");
    }
}
