package com.rflow.gateway.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class RateLimiterService {

    private final Map<String, List<Long>> requestTracker = new ConcurrentHashMap<>();

    public boolean allowRequest(String key, int maxRequests, int windowSeconds) {

        long now = System.currentTimeMillis();

        requestTracker.putIfAbsent(key, new ArrayList<>());

        List<Long> requests = requestTracker.get(key);

        requests.removeIf(time -> time < now - (windowSeconds * 1000L));

        if(requests.size() >= maxRequests) {
            return false;
        }

        requests.add(now);

        return true;
    }
}
