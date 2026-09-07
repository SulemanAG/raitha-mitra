-- Flyway Schema Migration V3: Security OTP Metadata Lifecycle
-- File: V3__security_otp_metadata.sql

CREATE TABLE IF NOT EXISTS otp_metadata (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    mobile_number VARCHAR(15) NOT NULL,
    otp_hash VARCHAR(100) NOT NULL,
    expiry_time TIMESTAMP WITH TIME ZONE NOT NULL,
    resend_available_time TIMESTAMP WITH TIME ZONE NOT NULL,
    attempts_count INT NOT NULL DEFAULT 0,
    verified BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_otp_mobile_number ON otp_metadata(mobile_number);
