package com.rflow.gateway.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "services")

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BackendService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id")
    private Long tenantId;

    @Column(name = "service_name")
    private String serviceName;

    @Column(name = "service_slug")
    private String serviceSlug;

    @Column(name = "route_prefix")
    private String routePrefix;

    @Column(name = "target_url")
    private String targetUrl;

    private String description;

    private String status;

    @Column(name = "health_check_path")
    private String healthCheckPath;

    @Column(name = "request_timeout_ms")
    private Integer requestTimeoutMs;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
