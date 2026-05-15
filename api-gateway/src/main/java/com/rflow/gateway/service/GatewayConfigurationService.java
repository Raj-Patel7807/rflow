package com.rflow.gateway.service;

import com.rflow.gateway.model.GatewayConfiguration;
import com.rflow.gateway.repository.GatewayConfigurationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GatewayConfigurationService {

    private final GatewayConfigurationRepository gatewayConfigurationRepository;

    public boolean isEnabled(String key) {
        GatewayConfiguration config = gatewayConfigurationRepository.findByConfigKey(key);

        return config != null && "true".equalsIgnoreCase(config.getConfigValue());
    }
}
