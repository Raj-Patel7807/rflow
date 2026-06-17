package com.rflow.gateway.service;

import com.rflow.gateway.dto.ServiceRequest;
import com.rflow.gateway.dto.ServiceResponse;
import com.rflow.gateway.model.BackendService;
import com.rflow.gateway.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final ServiceRepository serviceRepository;

    public List<BackendService> getAll(Long tenantId) {

        return serviceRepository.findByTenantId(tenantId);
    }

    public BackendService getById(Long id) {

        return serviceRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Service Not Found!"));
    }

    public ServiceResponse create(Long tenantId, Long createdBy, ServiceRequest request) {

        BackendService service = new BackendService();

        service.setTenantId(tenantId);
        service.setServiceName(request.getServiceName());
        service.setServiceSlug(request.getServiceSlug());
        service.setRoutePrefix(request.getRoutePrefix());
        service.setTargetUrl(request.getTargetUrl());
        service.setDescription(request.getDescription());
        service.setStatus(request.getStatus());
        service.setHealthCheckPath(request.getHealthCheckPath());
        service.setRequestTimeoutMs(request.getRequestTimeoutMs());
        service.setCreatedBy(createdBy);

        BackendService saved = serviceRepository.save(service);

        return map(saved);
    }

    public ServiceResponse update(Long id, ServiceRequest request) {

        BackendService service = getById(id);

        service.setServiceName(request.getServiceName());
        service.setServiceSlug(request.getServiceSlug());
        service.setRoutePrefix(request.getRoutePrefix());
        service.setTargetUrl(request.getTargetUrl());
        service.setDescription(request.getDescription());
        service.setStatus(request.getStatus());
        service.setHealthCheckPath(request.getHealthCheckPath());
        service.setRequestTimeoutMs(request.getRequestTimeoutMs());

        BackendService updated = serviceRepository.save(service);

        return map(updated);
    }

    public void delete(Long id) {

        serviceRepository.deleteById(id);
    }

    private ServiceResponse map(BackendService service) {

        return ServiceResponse.builder()
                              .id(service.getId())
                              .tenantId(service.getTenantId())
                              .serviceName(service.getServiceName())
                              .serviceSlug(service.getServiceSlug())
                              .routePrefix(service.getRoutePrefix())
                              .targetUrl(service.getTargetUrl())
                              .description(service.getDescription())
                              .status(service.getStatus())
                              .healthCheckPath(service.getHealthCheckPath())
                              .requestTimeoutMs(service.getRequestTimeoutMs())
                              .build();
    }
}
