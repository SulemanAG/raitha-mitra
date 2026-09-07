-- Initial Schema Migration for Raitha Mitra Backend
-- File: V1__init_backend_schema.sql

CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    mobile_number VARCHAR(15) NOT NULL UNIQUE,
    primary_role VARCHAR(30) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Index on mobile_number for fast lookup
CREATE INDEX IF NOT EXISTS idx_users_mobile_number ON users(mobile_number);
