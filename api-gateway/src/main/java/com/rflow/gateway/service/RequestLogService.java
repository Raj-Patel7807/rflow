package com.rflow.gateway.service;

import com.rflow.gateway.model.RequestLog;
import com.rflow.gateway.repository.RequestLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RequestLogService {

    private final RequestLogRepository requestLogRepository;

    public void save(RequestLog requestLog) {
        requestLogRepository.save(requestLog);
    }
}
