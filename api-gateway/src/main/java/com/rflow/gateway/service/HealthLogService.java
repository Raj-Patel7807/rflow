package com.rflow.gateway.service;

import com.rflow.gateway.dto.HealthLogResponse;
import com.rflow.gateway.dto.PagedHealthLogResponse;
import com.rflow.gateway.model.BackendService;
import com.rflow.gateway.model.ServiceHealthLog;
import com.rflow.gateway.repository.ServiceHealthLogRepository;
import com.rflow.gateway.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class HealthLogService {

    private final ServiceHealthLogRepository healthLogRepository;
    private final ServiceRepository serviceRepository;
    private final HealthCheckService healthCheckService;

    public PagedHealthLogResponse getHealthLogs(Long tenantId, Long serviceId, int page, int size) {

        List<Long> serviceIds = serviceRepository.findByTenantId(tenantId)
                                                 .stream()
                                                 .map(BackendService::getId)
                                                 .toList();

        if(serviceIds.isEmpty()) {
            return PagedHealthLogResponse.builder()
                                         .logs(List.of())
                                         .page(page)
                                         .size(size)
                                         .totalElements(0)
                                         .totalPages(0)
                                         .build();
        }

        PageRequest pageRequest = PageRequest.of(page, size);

        List<ServiceHealthLog> logs = healthLogRepository.findForTenant(serviceIds, serviceId, pageRequest);
        long total = healthLogRepository.countForTenant(serviceIds, serviceId);

        Map<Long, String> serviceNames = loadServiceNames(tenantId);

        return PagedHealthLogResponse.builder()
                                     .logs(logs.stream()
                                               .map(log -> map(log, serviceNames))
                                               .toList())
                                     .page(page)
                                     .size(size)
                                     .totalElements(total)
                                     .totalPages(size == 0 ? 0 : (int) Math.ceil((double) total / size))
                                     .build();
    }

    private Map<Long, String> loadServiceNames(Long tenantId) {

        Map<Long, String> names = new HashMap<>();

        for(BackendService service : serviceRepository.findByTenantId(tenantId)) {
            names.put(service.getId(), service.getServiceName());
        }

        return names;
    }

    private HealthLogResponse map(ServiceHealthLog log, Map<Long, String> serviceNames) {
        return HealthLogResponse.builder()
                                .id(log.getId())
                                .serviceId(log.getServiceId())
                                .serviceName(serviceNames.get(log.getServiceId()))
                                .healthStatus(log.getHealthStatus())
                                .responseTimeMs(log.getResponseTimeMs())
                                .checkedAt(log.getCheckedAt())
                                .build();
    }

    public HealthLogResponse checkService(Long tenantId, Long serviceId) {
        BackendService service = serviceRepository.findById(serviceId)
                                                  .orElseThrow(() -> new RuntimeException("Service Not Found"));
        if(!service.getTenantId()
                   .equals(tenantId)) {
            throw new RuntimeException("Forbidden");
        }

        long start = System.currentTimeMillis();
        boolean up = healthCheckService.isServiceUp(service.getTargetUrl(), service.getHealthCheckPath());
        long duration = System.currentTimeMillis() - start;

        ServiceHealthLog log = new ServiceHealthLog();
        log.setServiceId(serviceId);
        log.setHealthStatus(up ? "UP" : "DOWN");
        log.setResponseTimeMs((int) duration);
        log.setCheckedAt(LocalDateTime.now());

        ServiceHealthLog saved = healthLogRepository.save(log);

        Map<Long, String> serviceNames = new HashMap<>();
        serviceNames.put(service.getId(), service.getServiceName());
        return map(saved, serviceNames);
    }

    public List<HealthLogResponse> checkAllServices(Long tenantId) {
        List<BackendService> services = serviceRepository.findByTenantId(tenantId);
        Map<Long, String> serviceNames = new HashMap<>();
        for(BackendService service : services) {
            serviceNames.put(service.getId(), service.getServiceName());
        }

        return services.stream()
                       .map(service -> {
                           long start = System.currentTimeMillis();
                           boolean up = healthCheckService.isServiceUp(service.getTargetUrl(),
                                                                       service.getHealthCheckPath());
                           long duration = System.currentTimeMillis() - start;

                           ServiceHealthLog log = new ServiceHealthLog();
                           log.setServiceId(service.getId());
                           log.setHealthStatus(up ? "UP" : "DOWN");
                           log.setResponseTimeMs((int) duration);
                           log.setCheckedAt(LocalDateTime.now());

                           ServiceHealthLog saved = healthLogRepository.save(log);
                           return map(saved, serviceNames);
                       })
                       .toList();
    }
}
