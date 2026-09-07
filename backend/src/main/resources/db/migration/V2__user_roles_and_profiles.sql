-- Schema Migration V2: User Roles, Account Status, Farmer & Labourer Profiles
-- File: V2__user_roles_and_profiles.sql

-- 1. Add account_status column to users table
ALTER TABLE users ADD COLUMN IF NOT EXISTS account_status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE';

-- 2. User Roles collection table (Supporting multi-role users)
CREATE TABLE IF NOT EXISTS user_roles (
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role VARCHAR(30) NOT NULL,
    PRIMARY KEY (user_id, role)
);

-- 3. Farmer Profiles composition table
CREATE TABLE IF NOT EXISTS farmer_profiles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID UNIQUE NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    full_name VARCHAR(100) NOT NULL,
    farm_location VARCHAR(150),
    farm_size_acres NUMERIC(8,2),
    preferred_crop_types VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 4. Labourer Profiles composition table
CREATE TABLE IF NOT EXISTS labourer_profiles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID UNIQUE NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    full_name VARCHAR(100) NOT NULL,
    daily_wage_rate NUMERIC(10,2),
    experience_years INT DEFAULT 0,
    availability_status VARCHAR(30) NOT NULL DEFAULT 'AVAILABLE',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 5. Labourer Skills collection table (Normalized skills mapping)
CREATE TABLE IF NOT EXISTS labourer_skills (
    labourer_profile_id UUID NOT NULL REFERENCES labourer_profiles(id) ON DELETE CASCADE,
    skill VARCHAR(50) NOT NULL,
    PRIMARY KEY (labourer_profile_id, skill)
);

-- Indexes for performance & discovery query optimization
CREATE INDEX IF NOT EXISTS idx_farmer_profiles_user_id ON farmer_profiles(user_id);
CREATE INDEX IF NOT EXISTS idx_labourer_profiles_user_id ON labourer_profiles(user_id);
CREATE INDEX IF NOT EXISTS idx_labourer_skills_skill ON labourer_skills(skill);
CREATE INDEX IF NOT EXISTS idx_labourer_availability ON labourer_profiles(availability_status);
