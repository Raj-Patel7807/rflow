package com.rflow.gateway.controller;

import com.rflow.gateway.dto.LoginRequest;
import com.rflow.gateway.dto.LoginResponse;
import com.rflow.gateway.service.AuthService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication APIs for login, logout and user session management.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Authenticates a user and creates a session.
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest, HttpSession session) {

        return ResponseEntity.ok(authService.login(loginRequest, session));
    }

    /**
     * Ends the current user session.
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {

        authService.logout(session);

        return ResponseEntity.ok("Logged Out");
    }

    /**
     * Return details of the logged-in user
     */
    @GetMapping("/me")
    public ResponseEntity<?> me(HttpSession session) {

        return ResponseEntity.ok(authService.currentUser(session));
    }
}
