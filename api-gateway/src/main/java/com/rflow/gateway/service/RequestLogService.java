package com.rflow.gateway.service;

import com.rflow.gateway.dto.PagedLogResponse;
import com.rflow.gateway.dto.RequestLogResponse;
import com.rflow.gateway.model.BackendService;
import com.rflow.gateway.model.RequestLog;
import com.rflow.gateway.repository.RequestLogRepository;
import com.rflow.gateway.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RequestLogService {

    private final RequestLogRepository requestLogRepository;
    private final ServiceRepository serviceRepository;

    public void save(RequestLog requestLog) {
        requestLogRepository.save(requestLog);
    }

    public PagedLogResponse getLogs(Long tenantId, String method, Integer status, int page, int size) {

        PageRequest pageRequest = PageRequest.of(page, size);
        List<RequestLog> logs;
        long total;

        if(method == null && status == null) {
            logs = requestLogRepository.findByTenantIdOrderByCreatedAtDesc(tenantId, pageRequest);
            total = requestLogRepository.countByTenantId(tenantId);
        } else {
            logs = requestLogRepository.findFiltered(tenantId, method, status, pageRequest);
            total = requestLogRepository.countFiltered(tenantId, method, status);
        }

        Map<Long, String> serviceNames = loadServiceNames(tenantId);

        return PagedLogResponse.builder()
                               .logs(logs.stream()
                                         .map(log -> map(log, serviceNames))
                                         .toList())
                               .page(page)
                               .size(size)
                               .totalElements(total)
                               .totalPages(size == 0 ? 0 : (int) Math.ceil((double) total / size))
                               .build();
    }

    public List<RequestLogResponse> getRecent(Long tenantId, int limit) {
        return getLogs(tenantId, null, null, 0, limit).getLogs();
    }

    private Map<Long, String> loadServiceNames(Long tenantId) {

        Map<Long, String> names = new HashMap<>();

        for(BackendService service : serviceRepository.findByTenantId(tenantId)) {
            names.put(service.getId(), service.getServiceName());
        }

        return names;
    }

    private RequestLogResponse map(RequestLog log, Map<Long, String> serviceNames) {
        return RequestLogResponse.builder()
                                 .id(log.getId())
                                 .tenantId(log.getTenantId())
                                 .serviceId(log.getServiceId())
                                 .serviceName(serviceNames.get(log.getServiceId()))
                                 .requestMethod(log.getRequestMethod())
                                 .requestPath(log.getRequestPath())
                                 .queryString(log.getQueryString())
                                 .clientIp(log.getClientIp())
                                 .responseStatus(log.getResponseStatus())
                                 .responseTimeMs(log.getResponseTimeMs())
                                 .userAgent(log.getUserAgent())
                                 .errorMessage(log.getErrorMessage())
                                 .createdAt(log.getCreatedAt())
                                 .build();
    }
}
