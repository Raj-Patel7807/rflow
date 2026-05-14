package com.rflow.gateway.controller;

import com.rflow.gateway.service.GatewayService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/{tenant}/**")
@RequiredArgsConstructor
public class GatewayController {

    private final GatewayService gatewayService;

    @RequestMapping
    public ResponseEntity<?> handle(@PathVariable String tenant, HttpServletRequest request, @RequestBody(required = false) String body) {
        return gatewayService.process(tenant, request, body);
    }
}
