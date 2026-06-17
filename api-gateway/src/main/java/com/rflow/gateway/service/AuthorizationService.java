package com.rflow.gateway.service;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthorizationService {

    public void requireLogin(HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");

        if(userId == null) {
            throw new RuntimeException("Unauthorized");
        }
    }

    public void requireRole(HttpSession session, String role) {

        requireLogin(session);

        String currRole = (String) session.getAttribute("role");

        if(!role.equals(currRole)) {
            throw new RuntimeException("Forbidden");
        }
    }

    public void requireRoles(HttpSession session, String... roles) {

        requireLogin(session);

        String currRole = (String) session.getAttribute("role");

        for(String role : roles) {
            if(role.equals(currRole)) {
                return;
            }
        }

        throw new RuntimeException("Forbidden");
    }

    public void requireTenants(HttpSession session, Long... tenantIds) {

        requireLogin(session);

        Long tenantId = (Long) session.getAttribute("tenantId");
        String currRole = (String) session.getAttribute("role");

        if(currRole.equals("SUPER_ADMIN")) {
            return;
        }

        for(Long tId : tenantIds) {
            if(tId.equals(tenantId)) {
                return;
            }
        }

        throw new RuntimeException("Forbidden");
    }
}
