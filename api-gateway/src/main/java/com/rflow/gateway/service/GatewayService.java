package com.rflow.gateway.service;

import com.rflow.gateway.model.Tenant;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GatewayService {

    private final TenantService tenantService;
    private final RouteService routeService;
    private final RequestForwarder requestForwarder;

    public ResponseEntity<?> process(String tenantSlug, HttpServletRequest request, String body) {

        String path = normalize(request.getRequestURI());

        Tenant tenant = tenantService.findBySlug(tenantSlug);

        if(tenant == null || !"ACTIVE".equals(tenant.getStatus())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid Tenant: " + tenantSlug);
        }

        String pathWithoutTenant = normalize(removeTenant(path, tenantSlug));

        com.rflow.gateway.model.Service service = routeService.findService(tenant.getId(), pathWithoutTenant);

        if(service == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No Service for Path: " + pathWithoutTenant);
        }

        return requestForwarder.forward(request, body, service, pathWithoutTenant);
    }

    private String removeTenant(String path, String tenantSlug) {
        return path.replaceFirst("/" + tenantSlug, "");
    }

    private String normalize(String path) {
        if(path == null) return "/";
        if(path.length() > 1 && path.endsWith("/")) {
            return path.substring(0, path.length() - 1);
        }
        return path;
    }
}
