package com.rflow.gateway.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Enumeration;

@Service
@RequiredArgsConstructor
public class RequestForwarder {

    private final RestTemplate restTemplate;

    public ResponseEntity<?> forward(HttpServletRequest request, String body, com.rflow.gateway.model.Service service, String pathWithoutTenant) {

        String targetUrl = service.getTargetUrl() + pathWithoutTenant;

        HttpMethod method = HttpMethod.valueOf(request.getMethod());

        HttpHeaders headers = new HttpHeaders();

        Enumeration<String> headerNames = request.getHeaderNames();

        while(headerNames.hasMoreElements()) {

            String headerName = headerNames.nextElement();

            headers.put(headerName, Collections.list(request.getHeaders(headerName)));
        }

        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        return restTemplate.exchange(targetUrl, method, entity, String.class);
    }
}
