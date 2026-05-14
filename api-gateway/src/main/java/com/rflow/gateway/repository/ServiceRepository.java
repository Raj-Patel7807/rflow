package com.rflow.gateway.repository;

import com.rflow.gateway.model.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Long> {

    List<Service> findByStatus(String status);

    List<Service> findByTenantIdAndStatus(Long tenantId, String status);
}
