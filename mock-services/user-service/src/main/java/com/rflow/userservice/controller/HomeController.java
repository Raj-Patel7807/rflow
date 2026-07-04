package com.rflow.userservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class HomeController {

    @Value("${spring.application.name}")
    private String serviceName;

    @GetMapping({"", "/"})
    public ResponseEntity<Map<String, Object>> home() {
        LinkedHashMap<String, Object> res = new LinkedHashMap<>();

        res.put("service", serviceName);
        res.put("status", "running");
        res.put("timestamp", Instant.now()
                                    .toString());
        res.put("endpoints", Map.of("health", "/health"));

        return ResponseEntity.ok(res);
    }
}
