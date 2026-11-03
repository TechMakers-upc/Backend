CREATE TABLE alert_read_receipts (
    user_id BIGINT NOT NULL,
    alert_id VARCHAR(160) NOT NULL,
    read_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT pk_alert_read_receipts PRIMARY KEY (user_id, alert_id),
    CONSTRAINT fk_alert_read_receipts_user
        FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE INDEX ix_alert_read_receipts_user_id
    ON alert_read_receipts (user_id);
