-- Create Endpoint Entity Table
CREATE TABLE IF NOT EXISTS endpoint_entity (
    endpoint_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    endpoint VARCHAR(255) NOT NULL,
    method VARCHAR(16) NOT NULL,
    is_active BOOLEAN DEFAULT FALSE,
    created_by VARCHAR(255),
    created_at TIMESTAMP,
    updated_by VARCHAR(255),
    updated_at TIMESTAMP,
    version BIGINT
);

-- Create Response Entity Table
CREATE TABLE IF NOT EXISTS response_entity (
    response_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    endpoint_id BIGINT NOT NULL,
    http_status INT NOT NULL,
    response_body TEXT NOT NULL,
    response_headers TEXT NOT NULL,
    created_by VARCHAR(255),
    created_at TIMESTAMP,
    updated_by VARCHAR(255),
    updated_at TIMESTAMP,
    version BIGINT,
    is_active BOOLEAN DEFAULT TRUE,
    CONSTRAINT fk_response_for_endpoint FOREIGN KEY (endpoint_id) REFERENCES endpoint_entity(endpoint_id));

-- Create a unique index on endpoint and method
CREATE UNIQUE INDEX idx_unique_endpoint_method ON endpoint_entity (endpoint, method);