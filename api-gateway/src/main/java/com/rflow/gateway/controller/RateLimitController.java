package com.rflow.gateway.controller;

import com.rflow.gateway.dto.CreateRateLimitRequest;
import com.rflow.gateway.dto.UpdateRateLimitRequest;
import com.rflow.gateway.model.BackendService;
import com.rflow.gateway.model.RateLimitPolicy;
import com.rflow.gateway.repository.ServiceRepository;
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
    private final ServiceRepository serviceRepository;
    private final AuthorizationService authorizationService;

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

    @PostMapping
    public ResponseEntity<RateLimitPolicy> create(@RequestBody CreateRateLimitRequest request, HttpSession session) {

        authorizationService.requireRole(session, "SUPER_ADMIN");

        Long userId = (Long) session.getAttribute("userId");

        return ResponseEntity.ok(rateLimitService.create(request, userId));
    }

    @GetMapping("/service/{serviceId}")
    public ResponseEntity<List<RateLimitPolicy>> getPolicies(@PathVariable Long serviceId, HttpSession session) {

        authorizationService.requireRole(session, "SUPER_ADMIN");

        return ResponseEntity.ok(rateLimitService.getServicePolicies(serviceId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RateLimitPolicy> update(@PathVariable Long id, @RequestBody UpdateRateLimitRequest request,
                                                  HttpSession session) {

        authorizationService.requireRole(session, "SUPER_ADMIN");

        return ResponseEntity.ok(rateLimitService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id, HttpSession session) {

        authorizationService.requireRole(session, "SUPER_ADMIN");

        rateLimitService.delete(id);

        return ResponseEntity.ok("Policy Deleted");
    }
}
