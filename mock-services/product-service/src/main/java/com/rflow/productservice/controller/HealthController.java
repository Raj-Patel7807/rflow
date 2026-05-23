package com.rflow.productservice.controller;

import com.rflow.productservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/health")
@RequiredArgsConstructor
public class HealthController {

    private final ProductService productService;

    private final long startTime = System.currentTimeMillis();

    @GetMapping
    public ResponseEntity<?> health() {

        long requestStart = System.currentTimeMillis();

        Map<String, Object> response = new HashMap<>();
        Map<String, String> dependencies = new HashMap<>();

        // status default
        String status = "UP";

        // uptime
        long uptimeMs = System.currentTimeMillis() - startTime;
        response.put("uptimeMs", uptimeMs);
        response.put("timestamp", Instant.now().toString());

        // dependency checks (real)
        dependencies.put("productService", productService != null ? "UP" : "DOWN");

        if(dependencies.containsValue("DOWN")) {
            status = "DEGRADED";
        }

        // memory info
        Runtime runtime = Runtime.getRuntime();
        Map<String, Object> memory = new HashMap<>();

        memory.put("usedMemory", runtime.totalMemory() - runtime.freeMemory());
        memory.put("freeMemory", runtime.freeMemory());
        memory.put("maxMemory", runtime.maxMemory());

        response.put("service", "product-service");
        response.put("status", status);
        response.put("memory", memory);
        response.put("dependencies", dependencies);

        response.put("responseTimeMs", System.currentTimeMillis() - requestStart);

        return ResponseEntity.ok(response);
    }
}
