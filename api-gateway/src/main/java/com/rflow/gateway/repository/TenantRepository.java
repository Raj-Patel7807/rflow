package com.rflow.gateway.repository;

import com.rflow.gateway.model.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, Long> {

    Tenant findByTenantSlug(String TenantSlug);

}
