package com.rflow.gateway.controller;

import com.rflow.gateway.dto.LoginRequest;
import com.rflow.gateway.dto.LoginResponse;
import com.rflow.gateway.service.AuthService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest, HttpSession session) {

        return ResponseEntity.ok(authService.login(loginRequest, session));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {

        authService.logout(session);

        return ResponseEntity.ok("Logged Out");
    }
}
