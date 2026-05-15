package com.rflow.gateway.service;

import com.rflow.gateway.model.RateLimitPolicy;
import com.rflow.gateway.repository.RateLimitPolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RateLimitService {

    private final RateLimitPolicyRepository rateLimitPolicyRepository;

    public RateLimitPolicy findPolicy(Long serviceId) {
        return rateLimitPolicyRepository.findByServiceIdAndIsActive(serviceId, true);
    }
}
