-- Flyway Schema Migration V5: Agricultural Machinery & Rental Management
-- File: V5__machinery_and_rentals.sql

CREATE TABLE IF NOT EXISTS machinery_owner_profiles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    full_name VARCHAR(100) NOT NULL,
    address VARCHAR(255) NOT NULL,
    contact_number VARCHAR(15) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS machinery (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    owner_user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    name VARCHAR(150) NOT NULL,
    model_number VARCHAR(100),
    category VARCHAR(50) NOT NULL,
    hp_rating INT,
    location VARCHAR(150) NOT NULL,
    daily_rate NUMERIC(10,2) NOT NULL,
    hourly_rate NUMERIC(10,2),
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS machinery_rental_requests (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    machinery_id UUID NOT NULL REFERENCES machinery(id) ON DELETE CASCADE,
    renter_user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    rental_unit VARCHAR(20) NOT NULL DEFAULT 'DAILY',
    estimated_units INT NOT NULL DEFAULT 1,
    rate_per_unit NUMERIC(10,2) NOT NULL,
    total_amount NUMERIC(10,2) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    renter_notes TEXT,
    owner_notes TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_machinery_owner_id ON machinery(owner_user_id);
CREATE INDEX IF NOT EXISTS idx_machinery_category ON machinery(category);
CREATE INDEX IF NOT EXISTS idx_machinery_status ON machinery(status);
CREATE INDEX IF NOT EXISTS idx_rentals_machinery_id ON machinery_rental_requests(machinery_id);
CREATE INDEX IF NOT EXISTS idx_rentals_renter_id ON machinery_rental_requests(renter_user_id);
CREATE INDEX IF NOT EXISTS idx_rentals_status ON machinery_rental_requests(status);
CREATE INDEX IF NOT EXISTS idx_rentals_dates ON machinery_rental_requests(start_date, end_date);
