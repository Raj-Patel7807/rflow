package com.rflow.gateway.service;

import com.rflow.gateway.dto.CreateTenantRequest;
import com.rflow.gateway.dto.TenantResponse;
import com.rflow.gateway.dto.UpdateTenantRequest;
import com.rflow.gateway.model.Tenant;
import com.rflow.gateway.model.User;
import com.rflow.gateway.repository.TenantRepository;
import com.rflow.gateway.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TenantService {

    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;

    public Tenant findBySlug(String slug) {
        return tenantRepository.findByTenantSlug(slug);
    }

    public Tenant findById(Long id) {
        return tenantRepository.findById(id)
                               .orElseThrow(() -> new RuntimeException("Tenant Not Found"));
    }

    public List<Tenant> getAll() {
        return tenantRepository.findAll();
    }

    public TenantResponse create(CreateTenantRequest request) {

        Tenant tenant = new Tenant();

        tenant.setTenantName(request.getTenantName());
        tenant.setTenantSlug(request.getTenantSlug());
        tenant.setOwnerEmail(request.getOwnerEmail());
        tenant.setStatus(request.getStatus() != null ? request.getStatus() : "ACTIVE");
        tenant.setCreatedAt(LocalDateTime.now());
        tenant.setUpdatedAt(LocalDateTime.now());

        Tenant savedTenant = tenantRepository.save(tenant);

        User owner = new User();
        owner.setTenantId(savedTenant.getId());
        owner.setFullName(request.getOwnerName() != null && !request.getOwnerName()
                                                                    .isBlank() ? request.getOwnerName() :
                          (savedTenant.getTenantName() + " Owner"));
        owner.setEmail(savedTenant.getOwnerEmail());
        owner.setPasswordHash(request.getOwnerPassword() != null && !request.getOwnerPassword()
                                                                            .isBlank() ? request.getOwnerPassword() :
                              "admin123");
        owner.setRole("TENANT_ADMIN");
        owner.setIsActive(true);
        owner.setCreatedAt(LocalDateTime.now());
        owner.setUpdatedAt(LocalDateTime.now());
        userRepository.save(owner);

        return map(savedTenant);
    }

    public TenantResponse update(Long id, UpdateTenantRequest request) {

        Tenant tenant = findById(id);

        tenant.setTenantName(request.getTenantName());
        tenant.setOwnerEmail(request.getOwnerEmail());
        tenant.setStatus(request.getStatus());
        tenant.setUpdatedAt(LocalDateTime.now());

        return map(tenantRepository.save(tenant));
    }

    public void delete(Long id) {
        tenantRepository.deleteById(id);
    }

    public TenantResponse map(Tenant tenant) {
        return TenantResponse.builder()
                             .id(tenant.getId())
                             .tenantName(tenant.getTenantName())
                             .tenantSlug(tenant.getTenantSlug())
                             .ownerEmail(tenant.getOwnerEmail())
                             .status(tenant.getStatus())
                             .build();
    }
}
