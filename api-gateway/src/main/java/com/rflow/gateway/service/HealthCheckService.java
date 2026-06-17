package com.rflow.gateway.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class HealthCheckService {

    private final RestTemplate restTemplate;

    public boolean isServiceUp(String targetUrl, String healthPath) {
        try {

            ResponseEntity<String> response = restTemplate.getForEntity(targetUrl + healthPath, String.class);

            return response.getStatusCode()
                           .is2xxSuccessful();

        } catch(Exception e) {

            return false;
        }
    }
}
