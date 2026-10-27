CREATE TABLE stock_movements (
    id BIGSERIAL PRIMARY KEY,
    plant_id BIGINT NOT NULL,
    part_id BIGINT NOT NULL,
    type VARCHAR(20) NOT NULL,
    quantity INTEGER NOT NULL,
    stock_after INTEGER NOT NULL,
    reason VARCHAR(200) NOT NULL DEFAULT '',
    occurred_at TIMESTAMP WITH TIME ZONE NOT NULL,
    user_id BIGINT,
    work_order_id BIGINT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_stock_movements_part FOREIGN KEY (part_id) REFERENCES spare_parts (id) ON DELETE CASCADE,
    CONSTRAINT ck_stock_movements_type CHECK (type IN ('ENTRY', 'ADJUSTMENT', 'CONSUMPTION')),
    CONSTRAINT ck_stock_movements_stock_after CHECK (stock_after >= 0)
);

CREATE INDEX ix_stock_movements_part_id ON stock_movements (part_id);
CREATE INDEX ix_stock_movements_plant_id ON stock_movements (plant_id);
