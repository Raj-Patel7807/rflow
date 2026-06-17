package com.rflow.gateway.service;

import com.rflow.gateway.dto.CreateRateLimitRequest;
import com.rflow.gateway.dto.UpdateRateLimitRequest;
import com.rflow.gateway.model.RateLimitPolicy;
import com.rflow.gateway.repository.RateLimitPolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RateLimitService {

    private final RateLimitPolicyRepository rateLimitPolicyRepository;

    public RateLimitPolicy findPolicy(Long serviceId) {
        return rateLimitPolicyRepository.findByServiceIdAndIsActive(serviceId, true);
    }

    public RateLimitPolicy create(CreateRateLimitRequest request, Long createdBy) {

        RateLimitPolicy policy = new RateLimitPolicy();

        policy.setServiceId(request.getServiceId());
        policy.setRequestsLimit(request.getRequestsLimit());
        policy.setWindowSeconds(request.getWindowSeconds());
        policy.setStrategy(request.getStrategy());
        policy.setIsActive(true);
        policy.setCreatedBy(createdBy);
        policy.setCreatedAt(LocalDateTime.now());
        policy.setUpdatedAt(LocalDateTime.now());

        return rateLimitPolicyRepository.save(policy);
    }

    public List<RateLimitPolicy> getServicePolicies(Long serviceId) {

        return rateLimitPolicyRepository.findByServiceId(serviceId);
    }

    public List<RateLimitPolicy> getTenantPolicies(Long tenantId, List<Long> serviceIds) {

        if(serviceIds.isEmpty()) {
            return List.of();
        }

        return rateLimitPolicyRepository.findByServiceIdIn(serviceIds);
    }

    public RateLimitPolicy findById(Long id) {

        return rateLimitPolicyRepository.findById(id).orElseThrow(() -> new RuntimeException("Policy Not Found"));
    }

    public RateLimitPolicy update(Long id, UpdateRateLimitRequest request) {

        RateLimitPolicy policy = findById(id);

        policy.setRequestsLimit(request.getRequestsLimit());
        policy.setWindowSeconds(request.getWindowSeconds());
        policy.setIsActive(request.getIsActive());
        policy.setUpdatedAt(LocalDateTime.now());

        return rateLimitPolicyRepository.save(policy);
    }

    public void delete(Long id) {
        rateLimitPolicyRepository.deleteById(id);
    }
}
