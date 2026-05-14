package com.rflow.gateway.repository;

import com.rflow.gateway.model.BackendService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceRepository extends JpaRepository<BackendService, Long> {

    List<BackendService> findByStatus(String status);

    List<BackendService> findByTenantIdAndStatus(Long tenantId, String status);
}
