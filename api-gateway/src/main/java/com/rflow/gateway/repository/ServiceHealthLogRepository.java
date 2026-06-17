package com.rflow.gateway.repository;

import com.rflow.gateway.model.ServiceHealthLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceHealthLogRepository extends JpaRepository<ServiceHealthLog, Long> {

}
