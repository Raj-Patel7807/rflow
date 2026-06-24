package com.rflow.gateway.dto;

import lombok.Builder;
import lombok.Data;

/**
 * Response payload containing a Tenant.
 */
@Data
@Builder
public class TenantResponse {

    private Long id;

    private String tenantName;

    private String tenantSlug;

    private String ownerEmail;

    private String status;
}
