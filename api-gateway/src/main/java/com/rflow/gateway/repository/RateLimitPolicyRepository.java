package com.rflow.gateway.repository;

import com.rflow.gateway.model.RateLimitPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for managing Rate limit policies.
 */
@Repository
public interface RateLimitPolicyRepository extends JpaRepository<RateLimitPolicy, Long> {

    RateLimitPolicy findByServiceIdAndIsActive(Long serviceId, Boolean isActive);

    List<RateLimitPolicy> findByServiceId(Long serviceId);

    List<RateLimitPolicy> findByServiceIdIn(List<Long> serviceIds);

}
