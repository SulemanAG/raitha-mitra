-- Flyway Schema Migration V4: Labour Requirements & Work Postings
-- File: V4__labour_requirements.sql

CREATE TABLE IF NOT EXISTS labour_requirements (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    farmer_user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    title VARCHAR(150) NOT NULL,
    description TEXT,
    task_type VARCHAR(50) NOT NULL,
    location VARCHAR(150) NOT NULL,
    required_workers_count INT NOT NULL DEFAULT 1,
    daily_offered_wage NUMERIC(10,2) NOT NULL,
    work_date DATE NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'OPEN',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS labour_requirement_skills (
    labour_requirement_id UUID NOT NULL REFERENCES labour_requirements(id) ON DELETE CASCADE,
    skill VARCHAR(50) NOT NULL,
    PRIMARY KEY (labour_requirement_id, skill)
);

CREATE INDEX IF NOT EXISTS idx_requirements_farmer_id ON labour_requirements(farmer_user_id);
CREATE INDEX IF NOT EXISTS idx_requirements_status ON labour_requirements(status);
CREATE INDEX IF NOT EXISTS idx_requirements_task_type ON labour_requirements(task_type);
CREATE INDEX IF NOT EXISTS idx_requirements_work_date ON labour_requirements(work_date);
