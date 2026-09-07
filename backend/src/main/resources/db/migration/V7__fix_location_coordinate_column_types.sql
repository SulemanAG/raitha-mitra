-- Flyway Schema Migration V7: Fix Location Coordinate Column Types for Hibernate Schema Validation
-- File: V7__fix_location_coordinate_column_types.sql

-- Alter machinery table coordinate columns from NUMERIC to DOUBLE PRECISION
ALTER TABLE machinery
    ALTER COLUMN latitude TYPE DOUBLE PRECISION,
    ALTER COLUMN longitude TYPE DOUBLE PRECISION;

-- Alter machinery_owner_profiles table coordinate columns from NUMERIC to DOUBLE PRECISION for consistency
ALTER TABLE machinery_owner_profiles
    ALTER COLUMN latitude TYPE DOUBLE PRECISION,
    ALTER COLUMN longitude TYPE DOUBLE PRECISION;
