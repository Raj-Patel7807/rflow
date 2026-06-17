package com.rflow.gateway.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {

        String message = ex.getMessage();

        if("Unauthorized".equals(message)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", message));
        }

        if("Forbidden".equals(message)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", message));
        }

        return ResponseEntity.badRequest().body(Map.of("error", message));
    }
}
