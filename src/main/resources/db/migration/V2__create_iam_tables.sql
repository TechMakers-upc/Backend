CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(150) NOT NULL,
    password_hash VARCHAR(100) NOT NULL,
    full_name VARCHAR(120) NOT NULL,
    job_title VARCHAR(120) NOT NULL DEFAULT '',
    plant_id BIGINT,
    role VARCHAR(30) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT uk_users_email UNIQUE (email),
    CONSTRAINT ck_users_role CHECK (role IN ('PLANT_MANAGER', 'OPERATIONS_MANAGER', 'TECHNICIAN')),
    CONSTRAINT ck_users_plant_required CHECK (role = 'OPERATIONS_MANAGER' OR plant_id IS NOT NULL)
);

CREATE INDEX ix_users_plant_id ON users (plant_id);
