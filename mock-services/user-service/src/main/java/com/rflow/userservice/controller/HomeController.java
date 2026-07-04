package com.rflow.userservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class HomeController {

    @Value("${spring.application.name}")
    private String serviceName;

    @GetMapping({"", "/"})
    public ResponseEntity<Map<String, Object>> home() {
        return ResponseEntity.ok(Map.of("service", serviceName, "status", "running", "timestamp", Instant.now()
                                                                                                         .toString(),
                                        "endpoints", Map.of("health", "/health")));
    }
}
