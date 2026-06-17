package com.rflow.gateway.service;

import com.rflow.gateway.model.BackendService;
import com.rflow.gateway.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RouteService {

    private final ServiceRepository serviceRepository;

    public BackendService findService(Long tenantId, String path) {
        List<BackendService> services = serviceRepository.findByTenantIdAndStatus(tenantId, "ACTIVE");

        for(BackendService service : services) {
            if(path.startsWith(service.getRoutePrefix())) {
                return service;
            }
        }

        return null;
    }

    public BackendService findById(Long id) {
        return serviceRepository.findById(id).orElseThrow(() -> new RuntimeException("Service Not Found!"));
    }
}
