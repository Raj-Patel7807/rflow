package com.rflow.gateway.dto;

import lombok.Data;

/**
 * Request payload for selecting a tenant.
 */
@Data
public class SelectTenantRequest {

    private Long tenantId;
}
