package com.rflow.gateway.dto;

import lombok.Data;

/**
 * Request payload for creating a tenant.
 */
@Data
public class CreateTenantRequest {

    private String tenantName;

    private String tenantSlug;

    private String ownerEmail;

    private String ownerName;

    private String ownerPassword;

    private String status;
}
