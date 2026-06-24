package com.rflow.gateway.controller;

import com.rflow.gateway.dto.CreateRateLimitRequest;
import com.rflow.gateway.dto.UpdateRateLimitRequest;
import com.rflow.gateway.model.BackendService;
import com.rflow.gateway.model.RateLimitPolicy;
import com.rflow.gateway.repository.ServiceRepository;
import com.rflow.gateway.service.AuthorizationService;
import com.rflow.gateway.service.RateLimitService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * APIs for managing rate limit policies for backend services.
 */
@RestController
@RequestMapping("/api/rate-limits")
@RequiredArgsConstructor
public class RateLimitController {

    private final RateLimitService rateLimitService;
    private final ServiceRepository serviceRepository;
    private final AuthorizationService authorizationService;

    /**
     * Returns all rate limit policies for the current tenant.
     */
    @GetMapping
    public ResponseEntity<List<RateLimitPolicy>> getAll(HttpSession session) {

        authorizationService.requireRole(session, "SUPER_ADMIN");

        Long tenantId = authorizationService.resolveTenantId(session);

        List<Long> serviceIds = serviceRepository.findByTenantId(tenantId)
                                                 .stream()
                                                 .map(BackendService::getId)
                                                 .toList();

        return ResponseEntity.ok(rateLimitService.getTenantPolicies(tenantId, serviceIds));
    }

    /**
     * Creates a new rate limit policy.
     */
    @PostMapping
    public ResponseEntity<RateLimitPolicy> create(@RequestBody CreateRateLimitRequest request, HttpSession session) {

        authorizationService.requireRole(session, "SUPER_ADMIN");

        Long userId = (Long) session.getAttribute("userId");

        return ResponseEntity.ok(rateLimitService.create(request, userId));
    }

    /**
     * Returns rate limit policies for a service.
     */
    @GetMapping("/service/{serviceId}")
    public ResponseEntity<List<RateLimitPolicy>> getPolicies(@PathVariable Long serviceId, HttpSession session) {

        authorizationService.requireRole(session, "SUPER_ADMIN");

        return ResponseEntity.ok(rateLimitService.getServicePolicies(serviceId));
    }

    /**
     * Updates an existing rate limit policy.
     */
    @PutMapping("/{id}")
    public ResponseEntity<RateLimitPolicy> update(@PathVariable Long id, @RequestBody UpdateRateLimitRequest request,
                                                  HttpSession session) {

        authorizationService.requireRole(session, "SUPER_ADMIN");

        return ResponseEntity.ok(rateLimitService.update(id, request));
    }

    /**
     * Deletes a rate limit policy.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id, HttpSession session) {

        authorizationService.requireRole(session, "SUPER_ADMIN");

        rateLimitService.delete(id);

        return ResponseEntity.ok("Policy Deleted");
    }
}
