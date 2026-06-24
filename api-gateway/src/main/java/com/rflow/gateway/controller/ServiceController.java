package com.rflow.gateway.controller;

import com.rflow.gateway.dto.ServiceRequest;
import com.rflow.gateway.service.AdminService;
import com.rflow.gateway.service.AuthorizationService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * APIs for managing backend services and routes.
 */
@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
public class ServiceController {

    private final AuthorizationService authorizationService;
    private final AdminService adminService;

    /**
     * Returns All services.
     */
    @GetMapping
    public ResponseEntity<?> getAll(HttpSession session) {

        authorizationService.requireRole(session, "SUPER_ADMIN");

        Long tenantId = authorizationService.resolveTenantId(session);

        return ResponseEntity.ok(adminService.getAll(tenantId));
    }

    /**
     * Returns a service by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id, HttpSession session) {

        authorizationService.requireRole(session, "SUPER_ADMIN");

        return ResponseEntity.ok(adminService.getById(id));
    }

    /**
     * Creates a new Service.
     */
    @PostMapping
    public ResponseEntity<?> create(@RequestBody ServiceRequest request, HttpSession session) {

        authorizationService.requireRole(session, "SUPER_ADMIN");

        Long tenantId = authorizationService.resolveTenantId(session);
        Long userId = (Long) session.getAttribute("userId");

        return ResponseEntity.ok(adminService.create(tenantId, userId, request));
    }

    /**
     * Update an existing service.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody ServiceRequest request, HttpSession session) {

        authorizationService.requireRole(session, "SUPER_ADMIN");

        return ResponseEntity.ok(adminService.update(id, request));
    }

    /**
     * Delete a Service.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id, HttpSession session) {

        authorizationService.requireRole(session, "SUPER_ADMIN");

        adminService.delete(id);

        return ResponseEntity.ok("Service Deleted");
    }
}
