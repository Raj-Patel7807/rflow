package com.rflow.gateway.controller;

import com.rflow.gateway.dto.UpdateGatewayConfigRequest;
import com.rflow.gateway.model.GatewayConfiguration;
import com.rflow.gateway.service.AuthorizationService;
import com.rflow.gateway.service.GatewayConfigurationService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/gateway-configs")
@RequiredArgsConstructor
public class GatewayConfigController {

    private final GatewayConfigurationService gatewayConfigService;

    private final AuthorizationService authorizationService;

    @GetMapping
    public ResponseEntity<List<GatewayConfiguration>> getAll(HttpSession session) {

        authorizationService.requireRole(session, "SUPER_ADMIN");

        return ResponseEntity.ok(gatewayConfigService.getAll());
    }

    @PutMapping("/{key}")
    public ResponseEntity<GatewayConfiguration> update(@PathVariable String key, @RequestBody UpdateGatewayConfigRequest request, HttpSession session) {

        authorizationService.requireRole(session, "SUPER_ADMIN");

        return ResponseEntity.ok(gatewayConfigService.update(key, request));
    }
}
