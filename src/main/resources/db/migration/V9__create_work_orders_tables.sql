CREATE TABLE work_orders (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(20) NOT NULL,
    type VARCHAR(20) NOT NULL,
    plant_id BIGINT NOT NULL,
    asset_id BIGINT NOT NULL,
    failure_id BIGINT,
    plan_id BIGINT,
    title VARCHAR(150) NOT NULL,
    description VARCHAR(2000) NOT NULL DEFAULT '',
    priority VARCHAR(10) NOT NULL,
    due_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL,
    technician_id BIGINT,
    opened_at TIMESTAMP WITH TIME ZONE NOT NULL,
    opened_by BIGINT,
    started_at TIMESTAMP WITH TIME ZONE,
    completed_at TIMESTAMP WITH TIME ZONE,
    solution VARCHAR(2000),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT uk_work_orders_code UNIQUE (code),
    CONSTRAINT fk_work_orders_failure FOREIGN KEY (failure_id) REFERENCES failures (id),
    CONSTRAINT ck_work_orders_status CHECK (status IN ('PENDING', 'IN_PROGRESS', 'COMPLETED')),
    CONSTRAINT ck_work_orders_type CHECK (type IN ('CORRECTIVE', 'PREVENTIVE'))
);

CREATE INDEX ix_work_orders_plant_id ON work_orders (plant_id);
CREATE INDEX ix_work_orders_technician_id ON work_orders (technician_id);

CREATE TABLE work_order_log_entries (
    work_order_id BIGINT NOT NULL,
    position INTEGER NOT NULL,
    occurred_at TIMESTAMP WITH TIME ZONE NOT NULL,
    author_id BIGINT,
    note VARCHAR(1000) NOT NULL,
    CONSTRAINT pk_work_order_log_entries PRIMARY KEY (work_order_id, position),
    CONSTRAINT fk_work_order_log_entries_order FOREIGN KEY (work_order_id) REFERENCES work_orders (id) ON DELETE CASCADE
);

CREATE TABLE work_order_part_usages (
    work_order_id BIGINT NOT NULL,
    position INTEGER NOT NULL,
    part_id BIGINT NOT NULL,
    quantity INTEGER NOT NULL,
    occurred_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT pk_work_order_part_usages PRIMARY KEY (work_order_id, position),
    CONSTRAINT fk_work_order_part_usages_order FOREIGN KEY (work_order_id) REFERENCES work_orders (id) ON DELETE CASCADE,
    CONSTRAINT ck_work_order_part_usages_quantity CHECK (quantity > 0)
);
