package com.rflow.gateway.repository;

import com.rflow.gateway.model.RequestLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestLogRepository extends JpaRepository<RequestLog, Long> {

    long countByTenantId(Long tenantId);

    long countByTenantIdAndResponseStatusBetween(Long tenantId, Integer start, Integer end);

    @Query("""
        SELECT r.serviceId, COUNT(r)
        FROM RequestLog r
        WHERE r.tenantId = :tenantId
        GROUP BY r.serviceId
        ORDER BY COUNT(r) DESC
    """)
    List<Object[]> topServices(Long tenantId);

    @Query("""
        SELECT r.serviceId, AVG(r.responseTimeMs)
        FROM RequestLog r
        WHERE r.tenantId = :tenantId
        GROUP BY r.serviceId
        ORDER BY AVG(r.responseTimeMs) DESC
    """)
    List<Object[]> slowServices(Long tenantId);

    @Query("""
        SELECT DATE(r.createdAt), COUNT(r)
        FROM RequestLog r
        WHERE r.tenantId = :tenantId
        GROUP BY DATE(r.createdAt)
        ORDER BY DATE(r.createdAt)
    """)
    List<Object[]> requestChart(Long tenantId);

    long countByTenantIdAndResponseStatus(Long tenantId, Integer status);
}
