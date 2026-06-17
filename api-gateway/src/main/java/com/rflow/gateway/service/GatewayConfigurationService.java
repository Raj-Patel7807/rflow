package com.rflow.gateway.service;

import com.rflow.gateway.dto.UpdateGatewayConfigRequest;
import com.rflow.gateway.model.GatewayConfiguration;
import com.rflow.gateway.repository.GatewayConfigurationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GatewayConfigurationService {

    private final GatewayConfigurationRepository gatewayConfigurationRepository;

    public List<GatewayConfiguration> getAll() {
        return gatewayConfigurationRepository.findAll();
    }

    public boolean isEnabled(String key) {
        GatewayConfiguration config = gatewayConfigurationRepository.findByConfigKey(key);

        return config != null && "true".equalsIgnoreCase(config.getConfigValue());
    }

    public GatewayConfiguration update(String key, UpdateGatewayConfigRequest request) {

        GatewayConfiguration config = gatewayConfigurationRepository.findByConfigKey(key);

        if(config == null) {
            throw new RuntimeException("Config Not Found");
        }

        config.setConfigValue(request.getConfigValue());

        return gatewayConfigurationRepository.save(config);
    }
}
