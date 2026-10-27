CREATE TABLE spare_parts (
    id BIGSERIAL PRIMARY KEY,
    plant_id BIGINT NOT NULL,
    sku VARCHAR(40) NOT NULL,
    name VARCHAR(150) NOT NULL,
    category VARCHAR(20) NOT NULL,
    unit VARCHAR(10) NOT NULL,
    stock INTEGER NOT NULL DEFAULT 0,
    min_stock INTEGER NOT NULL DEFAULT 0,
    location VARCHAR(60) NOT NULL DEFAULT '',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT uk_spare_parts_plant_sku UNIQUE (plant_id, sku),
    CONSTRAINT ck_spare_parts_stock CHECK (stock >= 0),
    CONSTRAINT ck_spare_parts_min_stock CHECK (min_stock >= 0)
);

CREATE INDEX ix_spare_parts_plant_id ON spare_parts (plant_id);
