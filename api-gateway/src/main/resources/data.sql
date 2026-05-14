INSERT INTO tenants (tenant_name, tenant_slug, owner_email, status)
VALUES ('Raj Tech', 'raj', 'raj@google.com', 'ACTIVE');

INSERT INTO users (tenant_id, full_name, email, password_hash, role, is_active)
VALUES
    (1, 'Raj Admin', 'rajadmin@google.com', 'Raj@7807', 'TENANT_ADMIN', TRUE),
    (1, 'Raj Developer', 'rajdev@google.com', 'Raj@7807', 'DEVELOPER', TRUE);

INSERT INTO services (tenant_id, service_name, service_slug, route_prefix, target_url, description, status, health_check_path, request_timeout_ms, created_by)
VALUES
    (1, 'user-service', 'user-service', '/api/users', 'http://localhost:8081', 'Handles user related APIs', 'ACTIVE', '/health', 5000, 1),
    (1, 'product-service', 'product-service', '/api/products', 'http://localhost:8083', 'Handles product related APIs', 'ACTIVE', '/health', 5000, 1),
    (1, 'payment-service', 'payment-service', '/api/payments', 'http://localhost:8082', 'Handles payment related APIs', 'ACTIVE', '/health', 5000, 1);

INSERT INTO rate_limit_policies (service_id, requests_limit, window_seconds, strategy, is_active, created_by)
VALUES
    (1, 100, 60, 'IP', TRUE, 1),
    (2, 200, 60, 'IP', TRUE, 1),
    (3, 50, 60, 'IP', TRUE, 1);

INSERT INTO gateway_configurations (config_key, config_value, description, is_sensitive, created_by)
VALUES
    ('gateway.default_timeout_ms', '5000', 'Default request timeout for gateway', FALSE, 1),
    ('gateway.enable_request_logging', 'true', 'Enable request logging system', FALSE, 1),
    ('gateway.enable_rate_limiting', 'true', 'Enable API rate limiting', FALSE, 1);

INSERT INTO service_health_logs (service_id, health_status, response_time_ms)
VALUES
    (1, 'UP', 20),
    (2, 'UP', 30),
    (3, 'DEGRADED', 120);
