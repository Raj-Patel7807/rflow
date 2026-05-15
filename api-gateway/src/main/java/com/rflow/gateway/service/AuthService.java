package com.rflow.gateway.service;

import com.rflow.gateway.dto.LoginRequest;
import com.rflow.gateway.dto.LoginResponse;
import com.rflow.gateway.model.User;
import com.rflow.gateway.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    public LoginResponse login(LoginRequest loginRequest, HttpSession session) {

        User user = userRepository.findByEmail(loginRequest.getEmail());

        if(user == null) {
            throw new RuntimeException("Invalid Email..");
        }

        if(Boolean.FALSE.equals(user.getIsActive())) {
            throw new RuntimeException("User Disabled");
        }

        // We can use BCrypt here...
        boolean matches = loginRequest.getPassword().equals(user.getPasswordHash());

        if(!matches) {
            throw new RuntimeException("Invalid Password");
        }

        session.setAttribute("userId", user.getId());
        session.setAttribute("tenantId", user.getTenantId());
        session.setAttribute("role", user.getRole());

        user.setLastLoginAt(LocalDateTime.now());

        userRepository.save(user);

        return LoginResponse.builder()
                .userId(user.getId())
                .tenantId(user.getTenantId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }

    public void logout(HttpSession session) {
        session.invalidate();
    }

    public LoginResponse currentUser(HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");

        if(userId == null) {
            throw new RuntimeException("Unauthorized");
        }

        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User Not Found"));

        return LoginResponse.builder()
                .userId(user.getId())
                .tenantId(user.getTenantId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}
