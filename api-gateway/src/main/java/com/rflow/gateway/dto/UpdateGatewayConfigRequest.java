package com.rflow.gateway.dto;

import lombok.Data;

/**
 * Request payload for updating a gateway configuration.
 */
@Data
public class UpdateGatewayConfigRequest {

    private String configValue;

}
