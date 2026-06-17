package com.rflow.gateway.controller;

import com.rflow.gateway.dto.ServiceRequest;
import com.rflow.gateway.service.AdminService;
import com.rflow.gateway.service.AuthorizationService;
import com.rflow.gateway.service.RouteService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
public class ServiceController {

    private final AuthorizationService authorizationService;
    private final AdminService adminService;
    private final RouteService routeService;

    @GetMapping
    public ResponseEntity<?> getAll(HttpSession session) {

        authorizationService.requireRole(session, "SUPER_ADMIN");

        Long tenantId = authorizationService.resolveTenantId(session);

        return ResponseEntity.ok(adminService.getAll(tenantId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id, HttpSession session) {

        authorizationService.requireRole(session, "SUPER_ADMIN");

        return ResponseEntity.ok(adminService.getById(id));
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody ServiceRequest request, HttpSession session) {

        authorizationService.requireRole(session, "SUPER_ADMIN");

        Long tenantId = authorizationService.resolveTenantId(session);
        Long userId = (Long) session.getAttribute("userId");

        return ResponseEntity.ok(adminService.create(tenantId, userId, request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody ServiceRequest request, HttpSession session) {

        authorizationService.requireRole(session, "SUPER_ADMIN");

        return ResponseEntity.ok(adminService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id, HttpSession session) {

        authorizationService.requireRole(session, "SUPER_ADMIN");

        adminService.delete(id);

        return ResponseEntity.ok("Service Deleted");
    }
}
