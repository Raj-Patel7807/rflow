package com.rflow.gateway.repository;

import com.rflow.gateway.model.GatewayConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for managing gateway configuration data.
 */
@Repository
public interface GatewayConfigurationRepository extends JpaRepository<GatewayConfiguration, Long> {

    GatewayConfiguration findByConfigKey(String configKey);

}
