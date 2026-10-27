CREATE TABLE assets (
    id BIGSERIAL PRIMARY KEY,
    plant_id BIGINT NOT NULL,
    code VARCHAR(40) NOT NULL,
    name VARCHAR(150) NOT NULL,
    category VARCHAR(20) NOT NULL,
    criticality VARCHAR(10) NOT NULL,
    status VARCHAR(20) NOT NULL,
    status_changed_at TIMESTAMP WITH TIME ZONE NOT NULL,
    location VARCHAR(120) NOT NULL DEFAULT '',
    brand VARCHAR(80) NOT NULL DEFAULT '',
    model VARCHAR(80) NOT NULL DEFAULT '',
    serial_number VARCHAR(80) NOT NULL DEFAULT '',
    installed_at DATE,
    description VARCHAR(1000) NOT NULL DEFAULT '',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_assets_plant FOREIGN KEY (plant_id) REFERENCES plants (id),
    CONSTRAINT uk_assets_plant_code UNIQUE (plant_id, code),
    CONSTRAINT ck_assets_status CHECK (status IN ('OPERATIONAL', 'MAINTENANCE', 'DOWN')),
    CONSTRAINT ck_assets_criticality CHECK (criticality IN ('HIGH', 'MEDIUM', 'LOW'))
);

CREATE INDEX ix_assets_plant_id ON assets (plant_id);

CREATE TABLE asset_manuals (
    asset_id BIGINT NOT NULL,
    position INTEGER NOT NULL,
    title VARCHAR(150) NOT NULL,
    url VARCHAR(500) NOT NULL,
    added_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT pk_asset_manuals PRIMARY KEY (asset_id, position),
    CONSTRAINT fk_asset_manuals_asset FOREIGN KEY (asset_id) REFERENCES assets (id) ON DELETE CASCADE
);
