package com.rflow.gateway.repository;

import com.rflow.gateway.model.ServiceHealthLog;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for managing Service health logs.
 */
@Repository
public interface ServiceHealthLogRepository extends JpaRepository<ServiceHealthLog, Long> {

    @Query("""
                SELECT h FROM ServiceHealthLog h
                WHERE h.serviceId IN :serviceIds
                AND (:serviceId IS NULL OR h.serviceId = :serviceId)
                ORDER BY h.checkedAt DESC
            """)
    List<ServiceHealthLog> findForTenant(List<Long> serviceIds, Long serviceId, Pageable pageable);

    @Query("""
                SELECT COUNT(h)
                FROM ServiceHealthLog h
                WHERE h.serviceId IN :serviceIds
                AND (:serviceId IS NULL OR h.serviceId = :serviceId)
            """)
    long countForTenant(List<Long> serviceIds, Long serviceId);
}
