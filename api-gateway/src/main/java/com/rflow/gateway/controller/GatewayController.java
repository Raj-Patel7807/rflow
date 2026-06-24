package com.rflow.gateway.controller;

import com.rflow.gateway.service.GatewayService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Main gateway entry point for forwarding tenant requests to backend services.
 */
@RestController
@RequestMapping("/{tenant}/**")
@RequiredArgsConstructor
public class GatewayController {

    private final GatewayService gatewayService;

    /**
     * Processes incoming requests and forwards them to the appropriate backend service.
     */
    @RequestMapping
    public ResponseEntity<?> handle(@PathVariable String tenant, HttpServletRequest request,
                                    @RequestBody(required = false) String body) {
        return gatewayService.process(tenant, request, body);
    }
}
