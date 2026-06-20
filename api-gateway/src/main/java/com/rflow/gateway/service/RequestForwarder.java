package com.rflow.gateway.service;

import com.rflow.gateway.model.BackendService;
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

    public ResponseEntity<?> forward(HttpServletRequest request, String body, BackendService service,
                                     String pathWithoutTenant) {

        String targetUrl = service.getTargetUrl() + pathWithoutTenant;

        System.out.println("================================");
        System.out.println("TARGET URL = " + targetUrl);
        System.out.println("METHOD = " + request.getMethod());
        System.out.println("PATH = " + pathWithoutTenant);
        System.out.println("================================");

        HttpMethod method = HttpMethod.valueOf(request.getMethod());

        HttpHeaders headers = new HttpHeaders();

        Enumeration<String> headerNames = request.getHeaderNames();

        while(headerNames.hasMoreElements()) {

            String headerName = headerNames.nextElement();

            if("host".equalsIgnoreCase(headerName) || "origin".equalsIgnoreCase(
                    headerName) || "referer".equalsIgnoreCase(headerName) || "accept-encoding".equalsIgnoreCase(
                    headerName)) {
                continue;
            }

            headers.put(headerName, Collections.list(request.getHeaders(headerName)));
        }

        headers.set("User-Agent", "Mozilla/5.0");

        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        try {

            ResponseEntity<String> response = restTemplate.exchange(targetUrl, method, entity, String.class);

            System.out.println(response.getHeaders());

            return ResponseEntity.status(response.getStatusCode())
                                 .body(response.getBody());

        } catch(Exception e) {

            System.out.println("FORWARDING FAILED");
            System.out.println("TARGET URL = " + targetUrl);

            e.printStackTrace();

            return ResponseEntity.status(500)
                                 .body(e.toString());
        }
    }
}