package com.rflow.gateway.controller;

import com.rflow.gateway.dto.PagedLogResponse;
import com.rflow.gateway.service.AuthorizationService;
import com.rflow.gateway.service.RequestLogService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * APIs for viewing and filtering gateway request logs.
 */
@RestController
@RequestMapping("/api/logs")
@RequiredArgsConstructor
public class LogController {

    private final AuthorizationService authorizationService;
    private final RequestLogService requestLogService;

    /**
     * Returns gateway request logs.
     */
    @GetMapping
    public ResponseEntity<PagedLogResponse> getLogs(HttpSession session, @RequestParam(required = false) String method,
                                                    @RequestParam(required = false) Integer status,
                                                    @RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "20") int size) {

        authorizationService.requireRole(session, "SUPER_ADMIN");

        Long tenantId = authorizationService.resolveTenantId(session);

        return ResponseEntity.ok(requestLogService.getLogs(tenantId, method, status, page, size));
    }
}
