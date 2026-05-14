package com.rflow.gateway.service;

import com.rflow.gateway.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RouteService {

    private final ServiceRepository serviceRepository;

    public com.rflow.gateway.model.Service findService(Long tenantId, String path) {
        List<com.rflow.gateway.model.Service> services = serviceRepository.findByTenantIdAndStatus(tenantId, "ACTIVE");

        for(com.rflow.gateway.model.Service service : services) {
            if(path.startsWith(service.getRoutePrefix())) {
                return service;
            }
        }

        return null;
    }
}
