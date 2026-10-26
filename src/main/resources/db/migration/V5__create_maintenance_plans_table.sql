CREATE TABLE maintenance_plans (
    id BIGSERIAL PRIMARY KEY,
    plant_id BIGINT NOT NULL,
    asset_id BIGINT NOT NULL,
    title VARCHAR(150) NOT NULL,
    tasks VARCHAR(2000) NOT NULL DEFAULT '',
    frequency_days INTEGER NOT NULL,
    next_due_date DATE NOT NULL,
    last_completed_at DATE,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    technician_id BIGINT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT ck_maintenance_plans_frequency CHECK (frequency_days BETWEEN 1 AND 730)
);

CREATE INDEX ix_maintenance_plans_plant_id ON maintenance_plans (plant_id);
CREATE INDEX ix_maintenance_plans_asset_id ON maintenance_plans (asset_id);
