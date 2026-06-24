package com.rflow.gateway.dto;

import lombok.Data;

/**
 * Request payload for updating a tenant.
 */
@Data
public class UpdateTenantRequest {

    private String tenantName;

    private String ownerEmail;

    private String status;
}
