package com.rflow.gateway.service;

import com.rflow.gateway.model.Tenant;
import com.rflow.gateway.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TenantService {

    private final TenantRepository tenantRepository;

    public Tenant findBySlug(String slug) {
        return tenantRepository.findByTenantSlug(slug);
    }
}
