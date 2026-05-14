DROP TABLE IF EXISTS
    request_logs,
    service_health_logs,
    rate_limit_policies,
    gateway_configurations,
    services,
    users,
    tenants
CASCADE;

CREATE TABLE IF NOT EXISTS tenants (
    id BIGSERIAL PRIMARY KEY,
    tenant_name VARCHAR(100) NOT NULL,
    tenant_slug VARCHAR(100) NOT NULL UNIQUE,
    owner_email VARCHAR(255) NOT NULL UNIQUE,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'
    CHECK (status IN ('ACTIVE', 'DISABLED', 'SUSPENDED')),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    full_name VARCHAR(150) NOT NULL,
    email VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL
       CHECK (role IN ('SUPER_ADMIN', 'TENANT_ADMIN', 'DEVELOPER')),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    last_login_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uq_users_tenant_email UNIQUE (tenant_id, email)
);

CREATE TABLE services (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    service_name VARCHAR(100) NOT NULL,
    service_slug VARCHAR(100) NOT NULL,
    route_prefix VARCHAR(255) NOT NULL,
    target_url VARCHAR(500) NOT NULL,
    description TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'
      CHECK (status IN ('ACTIVE', 'DISABLED', 'MAINTENANCE')),
    health_check_path VARCHAR(255) NOT NULL DEFAULT '/health',
    request_timeout_ms INTEGER NOT NULL DEFAULT 5000,
    created_by BIGINT REFERENCES users(id) ON DELETE SET NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uq_services_tenant_service_slug UNIQUE (tenant_id, service_slug),
    CONSTRAINT uq_services_tenant_route_prefix UNIQUE (tenant_id, route_prefix)
);

CREATE TABLE rate_limit_policies (
    id BIGSERIAL PRIMARY KEY,
    service_id BIGINT NOT NULL REFERENCES services(id) ON DELETE CASCADE,
    requests_limit INTEGER NOT NULL,
    window_seconds INTEGER NOT NULL,
    strategy VARCHAR(50) NOT NULL DEFAULT 'IP'
     CHECK (strategy IN ('IP', 'API_KEY', 'USER')),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_by BIGINT REFERENCES users(id) ON DELETE SET NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE request_logs (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT REFERENCES tenants(id) ON DELETE SET NULL,
    service_id BIGINT REFERENCES services(id) ON DELETE SET NULL,
    request_method VARCHAR(10) NOT NULL,
    request_path VARCHAR(500) NOT NULL,
    query_string TEXT,
    client_ip VARCHAR(100),
    response_status INTEGER NOT NULL,
    response_time_ms INTEGER NOT NULL,
    user_agent TEXT,
    error_message TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE service_health_logs (
    id BIGSERIAL PRIMARY KEY,
    service_id BIGINT NOT NULL REFERENCES services(id) ON DELETE CASCADE,
    health_status VARCHAR(20) NOT NULL
     CHECK (health_status IN ('UP', 'DOWN', 'DEGRADED')),
    response_time_ms INTEGER,
    checked_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE gateway_configurations (
    id BIGSERIAL PRIMARY KEY,
    config_key VARCHAR(100) NOT NULL UNIQUE,
    config_value TEXT NOT NULL,
    description TEXT,
    is_sensitive BOOLEAN DEFAULT FALSE,
    created_by BIGINT REFERENCES users(id) ON DELETE SET NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);


CREATE INDEX idx_users_tenant_id
ON users (tenant_id);

CREATE INDEX idx_services_tenant_id
ON services (tenant_id);

CREATE INDEX idx_request_logs_tenant_id
ON request_logs (tenant_id);

CREATE INDEX idx_services_tenant_route_prefix
ON services (tenant_id, route_prefix);

-- Fast service lookup
CREATE INDEX idx_services_service_slug
ON services (service_slug);

-- Fast request log queries for dashboard
CREATE INDEX idx_request_logs_created_at
ON request_logs (created_at);

CREATE INDEX idx_request_logs_service_id
ON request_logs (service_id);

CREATE INDEX idx_request_logs_response_status
ON request_logs (response_status);

-- Fast health monitoring queries
CREATE INDEX idx_service_health_logs_service_id
ON service_health_logs (service_id);

CREATE INDEX idx_service_health_logs_checked_at
ON service_health_logs (checked_at);
