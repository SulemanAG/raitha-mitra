-- Flyway Schema Migration V6: Location & Spatial Discovery Infrastructure
-- File: V6__location_and_spatial_indexing.sql

-- Enable PostGIS spatial extension if supported by environment
CREATE EXTENSION IF NOT EXISTS postgis;

-- Add location fields and optimistic locking version column to machinery table
ALTER TABLE machinery
    ADD COLUMN IF NOT EXISTS latitude NUMERIC(10,8),
    ADD COLUMN IF NOT EXISTS longitude NUMERIC(11,8),
    ADD COLUMN IF NOT EXISTS state VARCHAR(100),
    ADD COLUMN IF NOT EXISTS district VARCHAR(100),
    ADD COLUMN IF NOT EXISTS taluk VARCHAR(100),
    ADD COLUMN IF NOT EXISTS village VARCHAR(100),
    ADD COLUMN IF NOT EXISTS location_source VARCHAR(30) DEFAULT 'MANUAL',
    ADD COLUMN IF NOT EXISTS location_updated_at TIMESTAMP WITH TIME ZONE,
    ADD COLUMN IF NOT EXISTS version BIGINT NOT NULL DEFAULT 0;

-- Add location fields to machinery_owner_profiles table
ALTER TABLE machinery_owner_profiles
    ADD COLUMN IF NOT EXISTS latitude NUMERIC(10,8),
    ADD COLUMN IF NOT EXISTS longitude NUMERIC(11,8),
    ADD COLUMN IF NOT EXISTS state VARCHAR(100),
    ADD COLUMN IF NOT EXISTS district VARCHAR(100),
    ADD COLUMN IF NOT EXISTS taluk VARCHAR(100),
    ADD COLUMN IF NOT EXISTS village VARCHAR(100),
    ADD COLUMN IF NOT EXISTS location_updated_at TIMESTAMP WITH TIME ZONE;

-- Create indexes for location filtering and spatial performance
CREATE INDEX IF NOT EXISTS idx_machinery_district ON machinery(district);
CREATE INDEX IF NOT EXISTS idx_machinery_coords ON machinery(latitude, longitude);
