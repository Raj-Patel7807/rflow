package com.rflow.gateway.controller;

import com.rflow.gateway.dto.CreateTenantRequest;
import com.rflow.gateway.dto.SelectTenantRequest;
import com.rflow.gateway.dto.TenantResponse;
import com.rflow.gateway.dto.UpdateTenantRequest;
import com.rflow.gateway.model.Tenant;
import com.rflow.gateway.service.AuthorizationService;
import com.rflow.gateway.service.TenantService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tenant")
@RequiredArgsConstructor
public class TenantController {

    private final AuthorizationService authorizationService;
    private final TenantService tenantService;

    @GetMapping("/all")
    public ResponseEntity<List<TenantResponse>> getAll(HttpSession session) {

        authorizationService.requireRole(session, "SUPER_ADMIN");

        return ResponseEntity.ok(tenantService.getAll().stream().map(tenantService::map).toList());
    }

    @PostMapping
    public ResponseEntity<TenantResponse> create(@RequestBody CreateTenantRequest request, HttpSession session) {

        authorizationService.requireRole(session, "SUPER_ADMIN");

        return ResponseEntity.ok(tenantService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TenantResponse> update(@PathVariable Long id, @RequestBody UpdateTenantRequest request, HttpSession session) {

        authorizationService.requireRole(session, "SUPER_ADMIN");

        return ResponseEntity.ok(tenantService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id, HttpSession session) {

        authorizationService.requireRole(session, "SUPER_ADMIN");

        tenantService.delete(id);

        Long selected = (Long) session.getAttribute("selectedTenantId");

        if(id.equals(selected)) {
            session.removeAttribute("selectedTenantId");
            session.removeAttribute("selectedTenantSlug");
        }

        return ResponseEntity.ok("Tenant Deleted");
    }

    @PostMapping("/select")
    public ResponseEntity<TenantResponse> select(@RequestBody SelectTenantRequest request, HttpSession session) {

        authorizationService.requireRole(session, "SUPER_ADMIN");

        Tenant tenant = tenantService.findById(request.getTenantId());

        session.setAttribute("selectedTenantId", tenant.getId());
        session.setAttribute("selectedTenantSlug", tenant.getTenantSlug());

        return ResponseEntity.ok(tenantService.map(tenant));
    }

    @GetMapping("/selected")
    public ResponseEntity<TenantResponse> selected(HttpSession session) {

        authorizationService.requireRole(session, "SUPER_ADMIN");

        Long tenantId = (Long) session.getAttribute("selectedTenantId");

        if(tenantId == null) {
            return ResponseEntity.ok(null);
        }

        return ResponseEntity.ok(tenantService.map(tenantService.findById(tenantId)));
    }

    @PostMapping("/clear")
    public ResponseEntity<?> clearSelection(HttpSession session) {

        authorizationService.requireRole(session, "SUPER_ADMIN");

        session.removeAttribute("selectedTenantId");
        session.removeAttribute("selectedTenantSlug");

        return ResponseEntity.ok("Selection Cleared");
    }
}
