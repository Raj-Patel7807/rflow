package com.rflow.gateway.service;

import com.rflow.gateway.dto.CreateUserRequest;
import com.rflow.gateway.dto.UpdateUserRequest;
import com.rflow.gateway.model.User;
import com.rflow.gateway.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User create(Long tenantId, CreateUserRequest request) {

        if("SUPER_ADMIN".equals(request.getRole())) {
            throw new RuntimeException("Cannot Create SUPER_ADMIN");
        }

        User user = new User();

        user.setTenantId(tenantId);
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        // BCrypt
        user.setPasswordHash(request.getPassword());
        user.setRole(request.getRole());
        user.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }

    public List<User> getTenantUsers(Long tenantId) {
        return userRepository.findByTenantId(tenantId);
    }

    public User findById(Long id) {

        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User Not Found"));
    }

    public User update(Long id, UpdateUserRequest request) {

        User user = findById(id);

        user.setFullName(request.getFullName());
        user.setRole(request.getRole());
        user.setIsActive(request.getIsActive());

        user.setUpdatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
    }
}
