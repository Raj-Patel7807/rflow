package com.rflow.gateway.service;

import com.rflow.gateway.model.BackendService;
import com.rflow.gateway.model.RateLimitPolicy;
import com.rflow.gateway.model.RequestLog;
import com.rflow.gateway.model.Tenant;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class GatewayService {

    private final TenantService tenantService;
    private final RouteService routeService;
    private final RequestForwarder requestForwarder;
    private final RequestLogService requestLogService;
    private final GatewayConfigurationService gatewayConfigurationService;
    private final RateLimitService rateLimitService;
    private final RateLimiterService rateLimiterService;
    private final HealthCheckService healthCheckService;

    public ResponseEntity<?> process(String tenantSlug, HttpServletRequest request, String body) {

        if(!gatewayConfigurationService.isEnabled("gateway.enabled")) {
            return ResponseEntity.status(503).body("Gateway Disabled");
        }

        long start = System.currentTimeMillis();

        String path = normalize(request.getRequestURI());

        Tenant tenant = tenantService.findBySlug(tenantSlug);

        if(tenant == null || !"ACTIVE".equals(tenant.getStatus())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid Tenant: " + tenantSlug);
        }

        String pathWithoutTenant = normalize(removeTenant(path, tenantSlug));

        BackendService service = routeService.findService(tenant.getId(), pathWithoutTenant);

        if(service == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No Service for Path: " + pathWithoutTenant);
        }

        RequestLog log = new RequestLog();

        log.setTenantId(tenant.getId());
        log.setServiceId(service.getId());
        log.setRequestMethod(request.getMethod());
        log.setQueryString(request.getQueryString());
        log.setUserAgent(request.getHeader("User-Agent"));
        log.setRequestPath(path);
        log.setClientIp(request.getRemoteAddr());
        log.setCreatedAt(LocalDateTime.now());

        RateLimitPolicy policy = rateLimitService.findPolicy(service.getId());

        if(policy != null) {

            String rateKey = tenant.getTenantSlug() + ":" + request.getRemoteAddr() + ":" + service.getId();

            boolean allowed = rateLimiterService.allowRequest(rateKey, policy.getRequestsLimit(), policy.getWindowSeconds());

            if(!allowed) {
                log.setResponseStatus(429);
                log.setResponseTimeMs(0);
                log.setErrorMessage("Rate Limit Exceeded");
                requestLogService.save(log);

                return ResponseEntity.status(429).body("Too Many Requests");
            }
        }

        boolean healthy = healthCheckService.isServiceUp(service.getTargetUrl(), service.getHealthCheckPath());

        if(!healthy) {
            log.setResponseStatus(503);
            log.setResponseTimeMs(0);
            log.setErrorMessage("Service Unavailable");
            requestLogService.save(log);

            return ResponseEntity.status(503).body("Service Unavailable");
        }

        ResponseEntity<?> response = requestForwarder.forward(request, body, service, pathWithoutTenant);

        long responseTime = System.currentTimeMillis() - start;

        log.setResponseTimeMs((int) responseTime);
        log.setResponseStatus(response.getStatusCode().value());

        requestLogService.save(log);

        return response;
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
