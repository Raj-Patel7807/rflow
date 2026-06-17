package com.rflow.gateway.dto;

import lombok.Data;

@Data
public class UpdateTenantRequest {

    private String tenantName;

    private String ownerEmail;

    private String status;
}
