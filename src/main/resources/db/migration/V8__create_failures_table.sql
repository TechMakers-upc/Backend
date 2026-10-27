CREATE TABLE failures (
    id BIGSERIAL PRIMARY KEY,
    plant_id BIGINT NOT NULL,
    asset_id BIGINT NOT NULL,
    kind VARCHAR(20) NOT NULL,
    description VARCHAR(1000) NOT NULL DEFAULT '',
    line_stopped BOOLEAN NOT NULL DEFAULT FALSE,
    priority VARCHAR(10),
    status VARCHAR(20) NOT NULL,
    reported_by BIGINT,
    reported_at TIMESTAMP WITH TIME ZONE NOT NULL,
    resolved_at TIMESTAMP WITH TIME ZONE,
    work_order_id BIGINT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT ck_failures_status CHECK (status IN ('OPEN', 'IN_PROGRESS', 'RESOLVED')),
    CONSTRAINT ck_failures_priority CHECK (priority IS NULL OR priority IN ('CRITICAL', 'HIGH', 'MEDIUM', 'LOW'))
);

CREATE INDEX ix_failures_plant_id ON failures (plant_id);
CREATE INDEX ix_failures_asset_id ON failures (asset_id);
