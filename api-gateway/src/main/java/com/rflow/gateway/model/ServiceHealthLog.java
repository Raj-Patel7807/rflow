package com.rflow.gateway.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "service_health_logs")

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceHealthLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "service_id")
    private Long serviceId;

    @Column(name = "health_status")
    private String healthStatus;

    @Column(name = "response_time_ms")
    private Integer responseTimeMs;

    @Column(name = "checked_at")
    private LocalDateTime checkedAt;
}
